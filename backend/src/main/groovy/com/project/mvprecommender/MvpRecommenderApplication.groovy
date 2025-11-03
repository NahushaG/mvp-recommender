package com.project.mvprecommender

import io.github.cdimascio.dotenv.Dotenv
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class MvpRecommenderApplication {

	static void main(String[] args) {
		Dotenv dotenv = Dotenv.configure()
				.ignoreIfMissing() // optional, won't fail if .env not present
				.load();

		System.setProperty("OPENAI_API_KEY", dotenv.get("OPENAI_API_KEY"));

		SpringApplication.run(MvpRecommenderApplication.class, args);
	}

}
