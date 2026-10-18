package apiMangement.example.Apimanagement.repository;

import apiMangement.example.Apimanagement.model.UserModel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Integer> {
}