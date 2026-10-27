package apiMangement.example.Apimanagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import apiMangement.example.Apimanagement.model.ApiKeyModel;

public interface ApiKeyRepository
        extends JpaRepository<ApiKeyModel, Integer> {

    Optional<ApiKeyModel> findByApiKey(String apiKey);
}