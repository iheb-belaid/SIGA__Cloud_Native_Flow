package com.stage.appToDo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, String> home() {
        return Map.of(
                "application", "SIGA ToDo Cloud Native Flow API",
                "frontend", "Angular 21",
                "backend", "Spring Boot Java 21"
        );
    }
}
