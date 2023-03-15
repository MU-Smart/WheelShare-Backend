package com.wheelshare.springboot;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.wheelshare.springboot.Services.UserService;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class EmailValidationTest {
  @Autowired
  private UserService userService;

  @Test
  public void emailSuccessValidationTest() {
    assertTrue(userService.emailValidation("alex.walker84@hotmail.com"));
    assertTrue(userService.emailValidation("emily.martin91@yahoo.com"));
    assertTrue(userService.emailValidation("jason.white67@gmail.com"));
    assertTrue(userService.emailValidation("sarah.brown22@outlook.com"));
    assertTrue(userService.emailValidation("matthew.jones99@gmail.com"));
    assertTrue(userService.emailValidation("laura.thomas11@yahoo.com"));
    assertTrue(userService.emailValidation("kevin.jackson55@hotmail.com"));
    assertTrue(userService.emailValidation("kelly.davis77@outlook.com"));
    assertTrue(userService.emailValidation("justin.roberts44@gmail.com"));
    assertTrue(userService.emailValidation("megan.harris33@yahoo.com"));
  } 

  public void emailFailureValidationTest()  {
    assertFalse(userService.emailValidation("john.doe@example")); // missing top-level domain
    assertFalse(userService.emailValidation("jane.doe@.com")); // missing domain name
    assertFalse(userService.emailValidation("foobar.com")); // missing @ symbol
    assertFalse(userService.emailValidation("jimmy@john@doe.com")); // multiple @ symbols
    assertFalse(userService.emailValidation("peter.smith@example.com.")); // trailing dot in domain name
    assertFalse(userService.emailValidation("katie@example..com")); // double dot in domain name
    assertFalse(userService.emailValidation("joe@example_com")); // underscore instead of dot
    assertFalse(userService.emailValidation("marc@example.123")); // numeric characters in top-level domain
    assertFalse(userService.emailValidation("lisa@.example.com")); // missing username
    assertFalse(userService.emailValidation("@example.com")); // missing username and @ symbol
  }
}
