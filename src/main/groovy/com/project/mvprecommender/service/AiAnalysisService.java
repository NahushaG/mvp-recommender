package com.project.mvprecommender.service;


import com.openai.client.OpenAIClient;
import com.openai.models.chat.completions.*;
import com.project.mvprecommender.configuration.MvpExternalClientProperties;
import com.project.mvprecommender.model.Fixture;
import com.project.mvprecommender.model.Player;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiAnalysisService {

    private final OpenAIClient openAIClient;
    private final MvpExternalClientProperties properties;

    public CompletableFuture<String> analyzeTopPlayers(List<Player> players, String position) {
        String prompt = buildTopPlayersPrompt(players, position);
        return getChatCompletionAsync(prompt);
    }

    public CompletableFuture<String> generateWeeklyInsights(List<Player> players, Integer gameWeek) {
        String prompt = buildWeeklyInsightsPrompt(players, gameWeek);
        return getChatCompletionAsync(prompt);
    }

    public CompletableFuture<String> analyzeSquad(List<Player> squad, Double budget) {
        String prompt = buildSquadAnalysisPrompt(squad, budget);
        return getChatCompletionAsync(prompt);
    }

    private CompletableFuture<String> getChatCompletionAsync(String prompt) {
        List<ChatCompletionMessageParam> messages = List.of(
                ChatCompletionMessageParam.ofSystem(
                        ChatCompletionSystemMessageParam.builder()
                                .content("You are an expert Fantasy Premier League analyst. Provide concise, data-driven insights.")
                                .build()
                ),
                ChatCompletionMessageParam.ofUser(
                        ChatCompletionUserMessageParam.builder()
                                .content(prompt)
                                .build()
                )
        );

        ChatCompletionCreateParams request = ChatCompletionCreateParams.builder()
                .model(properties.getModel())
                .messages(messages)
                .maxTokens(properties.getMaxTokens())
                .temperature(properties.getTemperature())
                .build();

        return sendWithRetries(request, 3, 1);
    }

    // --- Retry logic with backoff ---
    private CompletableFuture<String> sendWithRetries(ChatCompletionCreateParams params, int maxRetries, int attempt) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChatCompletion chatCompletion = openAIClient.chat().completions().create(params);
                String result = chatCompletion.choices().get(0).message().content().orElse("No response");
                log.debug("AI response received (attempt {}): {}", attempt, result);
                return result;
            } catch (Exception ex) {
                if (attempt < maxRetries) {
                    long backoff = 1000L * attempt;
                    log.warn("Attempt {}/{} failed: {}. Retrying in {} ms...", attempt, maxRetries, ex.getMessage(), backoff);
                    try {
                        Thread.sleep(backoff);
                    } catch (InterruptedException ignored) {
                    }
                    return sendWithRetries(params, maxRetries, attempt + 1).join();
                } else {
                    log.error("All retries failed. Returning fallback message.", ex);
                    return "AI analysis temporarily unavailable";
                }
            }
        });
    }

    // --- Prompt builders ---
    private String buildPlayerAnalysisPrompt(Player player, List<Fixture> fixtures) {
        return String.format("""
                        Analyze this Fantasy Premier League player in 2-3 sentences:
                        
                        Player: %s (%s)
                        Position: %s
                        Price: £%.1fm
                        Total Points: %d
                        Form: %s
                        Goals: %d, Assists: %d
                        Expected Goals: %s, Expected Assists: %s
                        Injury Status: %s
                        
                        Upcoming Fixtures Difficulty: %s
                        
                        Provide: 1) Current form assessment, 2) Value for money verdict, 3) Recommendation (BUY/HOLD/SELL/AVOID).
                        """,
                player.getWebName(),
                player.getTeamName(),
                getPositionName(player.getPosition()),
                player.getPriceInMillions(),
                player.getTotalPoints(),
                player.getForm(),
                player.getGoalsScored(),
                player.getAssists(),
                player.getExpectedGoals(),
                player.getExpectedAssists(),
                player.getStatus(),
                fixtures.stream().map(f -> String.valueOf(f.getTeamAwayDifficulty()))
                        .collect(Collectors.joining(", "))
        );
    }

    private String buildTopPlayersPrompt(List<Player> players, String position) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Analyze these top 5 %s players for Fantasy Premier League:\n\n", position));
        for (int i = 0; i < players.size(); i++) {
            Player p = players.get(i);
            prompt.append(String.format("%d. %s (£%.1fm) - %d pts, Form: %s\n",
                    i + 1, p.getWebName(), p.getPriceInMillions(), p.getTotalPoints(), p.getForm()));
        }
        prompt.append("\nProvide: 1) Best value pick, 2) Premium option, 3) Differential pick. Max 3 sentences total.");
        return prompt.toString();
    }

    private String buildWeeklyInsightsPrompt(List<Player> players, Integer gameweek) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Gameweek %d FPL Analysis:\n\n", gameweek));
        prompt.append("Key Players to Watch:\n");
        players.stream().limit(10).forEach(p ->
                prompt.append(String.format("- %s: %s pts, Form %s\n",
                        p.getWebName(), p.getTotalPoints(), p.getForm()))
        );
        prompt.append("\nProvide 3-4 sentence summary: 1) Top captain pick, 2) Best differential, 3) Budget option.");
        return prompt.toString();
    }

    private String buildSquadAnalysisPrompt(List<Player> squad, Double budget) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("Analyze this FPL squad (Budget: £%.1fm):\n\n", budget));
        squad.forEach(p ->
                prompt.append(String.format("%s - %s (£%.1fm): %d pts\n",
                        p.getWebName(), getPositionName(p.getPosition()),
                        p.getPriceInMillions(), p.getTotalPoints()))
        );
        prompt.append("\nProvide: 1) Squad balance assessment, 2) Weak areas, 3) Transfer suggestions. Max 4 sentences.");
        return prompt.toString();
    }

    private String getPositionName(Integer position) {
        return switch (position) {
            case 1 -> "Goalkeeper";
            case 2 -> "Defender";
            case 3 -> "Midfielder";
            case 4 -> "Forward";
            default -> "Unknown";
        };
    }

}
