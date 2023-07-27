package com.example.messengertest1.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengertest1.model.pojo.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsersViewModel extends ViewModel {//добавляем View model на UserActivity
    private final String DB_KEY = "Users";

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>();
    private MutableLiveData<List<User>> users = new MutableLiveData<>();




    public UsersViewModel() {
        auth = FirebaseAuth.getInstance();
        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() { //добавляем слушатель на то, если пользователь уже зарегистрирован
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    user.setValue(firebaseAuth.getCurrentUser());
                }
        });
        firebaseDatabase  = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference(DB_KEY);

        getUsersFromDb();
    }
    public void setUserOnline(boolean isOnline){
        FirebaseUser firebaseUser = auth.getCurrentUser();
        if (firebaseUser==null){
            return;
        }
        databaseReference.child(firebaseUser.getUid()).child("online").setValue(isOnline);
    }

    private void getUsersFromDb(){
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser currentUser = auth.getCurrentUser();
                if (currentUser==null){
                    return;
                }

                List<User> usersFromDb = new ArrayList<>();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    if (user== null){
                        return;
                    }
                    if (!user.getId().equals(currentUser.getUid())){
                        usersFromDb.add(user);
                    }

                }
                users.setValue(usersFromDb);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    public LiveData<List<User>> getUsers() {
        return users;
    }
    public MutableLiveData<FirebaseUser> getUser() {
        return user;
    }
    public void logout(){
        setUserOnline(false);
        auth.signOut();
    }
}
