package com.wheelshare.springboot.Services;

import java.util.concurrent.ExecutionException;

import com.wheelshare.springboot.Models.User;

public interface UserService {
  String createUser(String email, String password, String name, int age, String gender, double height,
      double weight, String type_wc, String wheel_type, String tire_mat, double wc_height, double wc_width)
      throws InterruptedException, ExecutionException;

  User retrieveUserByEmail(String email) throws InterruptedException, ExecutionException;
  
  String retrieveUserIdByEmail(String email) throws InterruptedException, ExecutionException;
  
  String updateUserByEmail(String oldEmail, String newEmail, String password, String name, int age,
      String gender, double height, double weight, String type_wc, String wheel_type, String tire_mat,
      double wc_height, double wc_width) throws InterruptedException, ExecutionException;
  
  String deleteUserByEmail(String email) throws InterruptedException, ExecutionException;

  boolean emailValidation(String emailAddress);
}
