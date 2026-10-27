package apiMangement.example.Apimanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import apiMangement.example.Apimanagement.model.TrafficLogModel;

public interface TrafficLogRepository
        extends JpaRepository<TrafficLogModel, Integer> {

    long countByStatusCodeGreaterThanEqual(int statusCode);

    long countByStatusCodeBetween(int start, int end);
    List<TrafficLogModel> findByHttpMethod(String httpMethod);
    List<TrafficLogModel> findByStatusCode(int statusCode);
    List<TrafficLogModel> findByRequestTimeBetween(
        java.time.LocalDateTime start,
        java.time.LocalDateTime end
);
}