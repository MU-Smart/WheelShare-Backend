package com.example.springboot.Models;

public enum Errors {
  // Errors for the UserService
  DUPLICATE_EMAIL("This email is duplicated!"),
  EMPTY_EMAIL("This email does not exist!"),
  INVALID_EMAIL("Invalid Email!"),
  // Errors for the MapBuilderService
  DUPLICATE_NODE_ID("Duplicate node id: "),
  TAG_UNAVAILABLE("Tag unavailable: "),
  INCLINE_NOT_NUMBER("Incline is not a number: "),
  BUILD_ERROR("Build Error"),
  // Errors for the RouteService
  INVALID_DECIMAL_VALUE("Decimal places should be greater than 0"),
  INVALID_PATH("Path containing only 1 node!");

  private final String message;

  Errors(String message) {
      this.message = message;
  }

  public String getMessage() {
      return message;
  }
}
