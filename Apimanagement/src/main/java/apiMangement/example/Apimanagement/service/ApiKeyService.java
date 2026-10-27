package apiMangement.example.Apimanagement.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import apiMangement.example.Apimanagement.model.ApiKeyModel;
import apiMangement.example.Apimanagement.repository.ApiKeyRepository;

@Service
public class ApiKeyService {

    private final ApiKeyRepository repository;

    public ApiKeyService(ApiKeyRepository repository) {
        this.repository = repository;
    }

    public ApiKeyModel createKey(ApiKeyModel model) {
        model.setApiKey(UUID.randomUUID().toString());
        return repository.save(model);
    }
    public boolean validateKey(String key) {
    return repository.findByApiKey(key)
            .map(apiKey -> apiKey.isActive())
            .orElse(false);
     }
}