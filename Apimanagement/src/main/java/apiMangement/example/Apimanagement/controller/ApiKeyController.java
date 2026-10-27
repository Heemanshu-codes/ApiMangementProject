package apiMangement.example.Apimanagement.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apiMangement.example.Apimanagement.model.ApiKeyModel;
import apiMangement.example.Apimanagement.service.ApiKeyService;

@RestController
@RequestMapping("/api-keys")
public class ApiKeyController {

    private final ApiKeyService service;

    public ApiKeyController(ApiKeyService service) {
        this.service = service;
    }

    @PostMapping
    public ApiKeyModel createKey(@RequestBody ApiKeyModel model) {
        return service.createKey(model);
    }
    @GetMapping("/validate")
public String validateKey(@RequestHeader("X-API-KEY") String key) {

    if (service.validateKey(key)) {
        return "Valid API Key";
    }

    return "Invalid API Key";
}
}