package com.wheelshare.springboot.Models;

public enum Errors {
  // Errors for the UserService
  DUPLICATE_EMAIL("This email is duplicated!"),
  EMPTY_EMAIL("This email does not exist!"),
  INVALID_EMAIL("Invalid Email!"),
  // Errors for the MapBuilderService
  DUPLICATE_NODE_ID("Duplicate node id: "),
  TYPE_UNAVAILABLE("Type unavailable: "),
  TAG_UNAVAILABLE("Tag unavailable: "),
  INVALID_INCLINE_FORMAT("Incline does not exist or is not a number: "),
  BUILD_ERROR("Build Error"),
  // Errors for the RouteService
  NODE_OUT_OF_BOUND("This coordinate is out of predefined bound"),
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
