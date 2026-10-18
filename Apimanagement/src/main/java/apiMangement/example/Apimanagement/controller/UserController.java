package apiMangement.example.Apimanagement.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import  org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import apiMangement.example.Apimanagement.model.UserModel;
import apiMangement.example.Apimanagement.service.UserService;
@RestController
@RequestMapping("/Users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public UserModel register(@RequestBody UserModel userModel) {
        userService.userRegister(userModel);
        return userModel;
    }

    @GetMapping("/getuser")
    public List<UserModel> getAllUser(){
        return userService.getAllusers();
    }
}