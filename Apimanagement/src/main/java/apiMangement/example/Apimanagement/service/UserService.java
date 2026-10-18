package apiMangement.example.Apimanagement.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apiMangement.example.Apimanagement.model.UserModel;
import apiMangement.example.Apimanagement.repository.*;
import java.util.*;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepo;
     
    public void userRegister(UserModel userModel){
        userRepo.save(userModel);
    }
    public List<UserModel> getAllusers(){
        return  userRepo.findAll();
    }
}
