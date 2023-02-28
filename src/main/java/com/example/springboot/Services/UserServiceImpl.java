package com.example.springboot.Services;

import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.example.springboot.Models.User;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;

@Service
public class UserServiceImpl implements UserService{
  public String createUser(String name) throws InterruptedException, ExecutionException {
    Firestore dbFirestore = FirestoreClient.getFirestore();
    User newUser = new User(name);
    ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("crud_user").document(name).set(newUser);
    return collectionsApiFuture.get().getClass().toString();
  }
}
