package com.example.springboot.Services;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.springboot.Models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.database.DatabaseException;

@Service
public class UserServiceImpl implements UserService {
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
      throw new DatabaseException("This email has already existed.Please use another email.");
    }

    // if not, create a new User object
    User newUser = new User(email, password, name, age, gender, height, weight, type_wc, wheel_type, tire_mat,
        wc_height, wc_width);
    // add it to the firestore database
    ApiFuture<DocumentReference> addedDocRef = dbFirestore.collection("user").add(newUser);
    return addedDocRef.get().getId();
  }

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
      throw new DatabaseException("Email does not exist.");
    }

    // return the user info
    return documents.get(0).toObject(User.class);
  }

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
      throw new DatabaseException("Email does not exist.");
    }

    // return the user info
    return documents.get(0).getId();
  }

  public String updateUserByEmail(String oldEmail, String newEmail, String password, String name, int age,
      String gender, double height, double weight, String type_wc, String wheel_type, String tire_mat, 
      double wc_height, double wc_width) throws InterruptedException, ExecutionException {
    // Init the Firestore database
    Firestore dbFirestore = FirestoreClient.getFirestore();

    // asynchronously retrieve multiple documents
    ApiFuture<QuerySnapshot> future = dbFirestore.collection("user").whereEqualTo("email", oldEmail).get();
    // future.get() blocks on response
    // this list should only be length 1 as each email must be unique in our
    // database
    List<QueryDocumentSnapshot> documents = future.get().getDocuments();

    // the queried list is empty, throw an error
    if (documents.isEmpty()) {
      throw new DatabaseException("Email does not exist.");
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
      throw new DatabaseException("Email does not exist.");
    }

    // delete the user
    ApiFuture<WriteResult> writeResult = dbFirestore.collection("user").document(documents.get(0).getId()).delete();

    // return the query execution time
    return writeResult.get().getUpdateTime().toString();
  }
}
