package com.example.springboot.Services;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.springboot.Models.User;
import com.example.springboot.Models.Errors;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class UserServiceImpl implements UserService {
  /**
   * Create the user object and store it in the Firestore database
   * 
   * @param email
   * @param password
   * @param name
   * @param age
   * @param gender
   * @param height
   * @param weight
   * @param type_wc
   * @param wheel_type
   * @param tire_mat
   * @param wc_height
   * @param wc_width
   */
  public String createUser(String email, String password, String name, int age, String gender, double height,
      double weight, String type_wc, String wheel_type, String tire_mat, double wc_height, double wc_width)
      throws InterruptedException, ExecutionException {
    // Init the Firestore database
    Firestore dbFirestore = FirestoreClient.getFirestore();

    // Handle exception when the email has already existed in the database
    // Asynchronously retrieve multiple documents
    ApiFuture<QuerySnapshot> future = dbFirestore.collection("user").whereEqualTo("email", email).get();
    // future.get() blocks on response
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // email has already existed
    if (!documents.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.DUPLICATE_EMAIL.getMessage());
    }

    // the email is not valid
    if (!emailValidation(email)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.INVALID_EMAIL.getMessage());
    }

    // if not, create a new User object
    User newUser = new User(email, password, name, age, gender, height, weight, type_wc, wheel_type, tire_mat,
        wc_height, wc_width);
    // add it to the firestore database
    ApiFuture<DocumentReference> addedDocRef = dbFirestore.collection("user").add(newUser);
    return addedDocRef.get().getId();
  }

  /**
   * Retrieve the user object using the email
   * 
   * @param email
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public User retrieveUserByEmail(String email) throws InterruptedException, ExecutionException {
    // Init the Firestore database
    Firestore dbFirestore = FirestoreClient.getFirestore();

    // asynchronously retrieve multiple documents
    ApiFuture<QuerySnapshot> future = dbFirestore.collection("user").whereEqualTo("email", email).get();
    // future.get() blocks on response
    // this list should only be length 1 as each email must be unique in our
    // database
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // the queried list is empty, throw an error
    if (documents.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMPTY_EMAIL.getMessage());
    }

    // return the user info
    return documents.get(0).toObject(User.class);
  }

  /**
   * Retrieve the user id using the email
   * 
   * @param email
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public String retrieveUserIdByEmail(String email) throws InterruptedException, ExecutionException {
    // Init the Firestore database
    Firestore dbFirestore = FirestoreClient.getFirestore();

    // asynchronously retrieve multiple documents
    ApiFuture<QuerySnapshot> future = dbFirestore.collection("user").whereEqualTo("email", email).get();
    // future.get() blocks on response
    // this list should only be length 1 as each email must be unique in our
    // database
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // the queried list is empty, throw an error
    if (documents.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMPTY_EMAIL.getMessage());
    }

    // return the user info
    return documents.get(0).getId();
  }

  /**
   * Update the user's information.
   * This function allows updating the email address but it will keep the key id
   * constant.
   * It searches for the onbject using the email - oldEmail
   * 
   * @param oldEmail   old email used to look up the User object
   * @param newEmail
   * @param password
   * @param name
   * @param age
   * @param gender
   * @param height
   * @param weight
   * @param type_wc
   * @param wheel_type
   * @param tire_mat
   * @param wc_height
   * @param wc_width
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */

  public String updateUserByEmail(String oldEmail, String newEmail, String password, String name, int age,
      String gender, double height, double weight, String type_wc, String wheel_type, String tire_mat,
      double wc_height, double wc_width) throws InterruptedException, ExecutionException {
    // Init the Firestore database
    Firestore dbFirestore = FirestoreClient.getFirestore();

    // asynchronously retrieve multiple documents
    ApiFuture<QuerySnapshot> oldFuture = dbFirestore.collection("user").whereEqualTo("email", oldEmail).get();
    ApiFuture<QuerySnapshot> newFuture = dbFirestore.collection("user").whereEqualTo("email", newEmail).get();
    // future.get() blocks on response
    // this list should only be length 1 as each email must be unique in our
    // database
    List<QueryDocumentSnapshot> oldDocuments = oldFuture.get().getDocuments();
    List<QueryDocumentSnapshot> newDocuments = newFuture.get().getDocuments();

    // the queried list is empty, throw an error
    if (oldDocuments.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMPTY_EMAIL.getMessage());
    }

    // new email has already existed, 2 scenarios:
    // 1. The new email is the same as the old email -> Okay
    // 2. The new email belongs to another account -> Error
    if (!newDocuments.isEmpty()) {
      // the id associating with the new and old email
      String oldEmailId = retrieveUserIdByEmail(oldEmail);
      String newEmailId = retrieveUserIdByEmail(newEmail);

      // the id of the 2 objects are different -> new email is used by a different
      // account.
      if (!oldEmailId.equals(newEmailId)) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.DUPLICATE_EMAIL.getMessage());
      }
    }

    // the email is not valid
    if (!emailValidation(newEmail)) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.INVALID_EMAIL.getMessage());
    }

    // return the user info
    String userId = retrieveUserIdByEmail(oldEmail);
    User newUser = new User(newEmail, password, name, age, gender, height, weight, type_wc, wheel_type, tire_mat,
        wc_height, wc_width);

    // update the user
    ApiFuture<WriteResult> writeResult = dbFirestore.collection("user").document(userId).set(newUser);

    // return the query execution time
    return writeResult.get().getUpdateTime().toString();
  }

  /**
   * Delete a user by using the email
   * 
   * @param email
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public String deleteUserByEmail(String email) throws InterruptedException, ExecutionException {
    // Init the Firestore database
    Firestore dbFirestore = FirestoreClient.getFirestore();

    // asynchronously retrieve multiple documents
    ApiFuture<QuerySnapshot> future = dbFirestore.collection("user").whereEqualTo("email", email).get();
    // future.get() blocks on response
    // this list should only be length 1 as each email must be unique in our
    // database
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // the queried list is empty, throw an error
    if (documents.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Errors.EMPTY_EMAIL.getMessage());
    }

    // delete the user
    ApiFuture<WriteResult> writeResult = dbFirestore.collection("user").document(documents.get(0).getId()).delete();

    // return the query execution time
    return writeResult.get().getUpdateTime().toString();
  }

  /**
   * Validate if the email address is valid or not using regular expression
   * Here is the explanation for the email address matching regex pattern:
   * + It allows numeric values from 0 to 9.
   * + Both uppercase and lowercase letters from a to z are allowed.
   * + Allowed are underscore “_”, hyphen “-“, and dot “.”
   * + Dot isn't allowed at the start and end of the local part.
   * + Consecutive dots aren't allowed.
   * + For the local part, a maximum of 64 characters are allowed.
   * 
   * 
   * @param email
   * @return
   * @throws InterruptedException
   * @throws ExecutionException
   */
  public boolean emailValidation(String emailAddress) {
    String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    return Pattern.compile(regexPattern)
        .matcher(emailAddress)
        .matches();
  }
}
