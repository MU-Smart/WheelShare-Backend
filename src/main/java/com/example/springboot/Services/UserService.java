package com.example.springboot.Services;

import com.example.springboot.Models.User;

import java.util.concurrent.ExecutionException;

public interface UserService {
  String createUser(String email, String password, String name, int age, String gender, double height,
      double weight, String type_wc, String wheel_type, String tire_mat, double wc_height, double wc_width)
      throws InterruptedException, ExecutionException;

  User retrieveUserByEmail(String email) throws InterruptedException, ExecutionException;

}
