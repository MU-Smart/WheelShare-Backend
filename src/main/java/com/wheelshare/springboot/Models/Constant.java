package com.wheelshare.springboot.Models;

public enum Constant {
    // Errors for the UserService
    MIN_LON(-84.7872),
    MAX_LON(-84.6941),
    MIN_LAT(39.4929),
    MAX_LAT(39.5209);
  
    private final Double constant;
  
    Constant(Double constant) {
        this.constant = constant;
    }
  
    public Double getConstant() {
        return constant;
    }
}
