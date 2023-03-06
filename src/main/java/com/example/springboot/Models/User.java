package com.example.springboot.Models;

import lombok.Data;

@Data
public class User {
  private String email;
  private String password;
  private String name;
  private int age;
  private String gender;
  private double height;
  private double weight;
  private String type_wc;
  private String wheel_type;
  private String tire_mat;
  private double wc_height;
  private double wc_width;

  public User() {}

  public User(String email, String password, String name, int age, String gender, double height, double weight, 
  String type_wc, String wheel_type, String tire_mat, double wc_height, double wc_width) {
    this.email = email;
    this.password = password;
    this.name = name;
    this.age = age;
    this.gender = gender;
    this.height = height;
    this.weight = weight;
    this.type_wc = type_wc;
    this.wheel_type = wheel_type;
    this.tire_mat = tire_mat;
    this.wc_height = wc_height;
    this.wc_width = wc_width;
  }
}