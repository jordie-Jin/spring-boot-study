package com.sesac.ai.backend.controller;

import com.sesac.ai.backend.service.GreetingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GreetingController {
    private final GreetingService greetingService;
    @GetMapping("/greeting")
    public Map<String, Object> greeting(
            @RequestParam(defaultValue = "World") String name) {
        return Map.of("message", greetingService.hello(name));
    }
}
