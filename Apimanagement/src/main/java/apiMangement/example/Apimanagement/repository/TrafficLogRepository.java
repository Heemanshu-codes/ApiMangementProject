package apiMangement.example.Apimanagement.repository;

import apiMangement.example.Apimanagement.model.TrafficLogModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrafficLogRepository extends JpaRepository<TrafficLogModel, Integer> {
}
