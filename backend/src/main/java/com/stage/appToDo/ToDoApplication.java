package com.stage.appToDo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ToDoApplication {

	public static void main(String[] args) {
		PostgresDatabaseInitializer.ensureDatabaseExists();
		SpringApplication.run(ToDoApplication.class, args);
	}
}
