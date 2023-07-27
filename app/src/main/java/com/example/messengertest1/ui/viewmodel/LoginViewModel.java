package com.example.messengertest1.ui.viewmodel;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginViewModel extends ViewModel {
    private final String TAG = "VIEWMODELTAG";

    private FirebaseAuth auth;
    private MutableLiveData<String> error = new MutableLiveData<>(); //создаем лайв дату которая вернет текст ошибки во вью
    private MutableLiveData<FirebaseUser> user = new MutableLiveData<>(); //будет возращать авторизован ли юзер или нет



    public LoginViewModel() {
        auth = FirebaseAuth.getInstance();

        auth.addAuthStateListener(new FirebaseAuth.AuthStateListener() { //добавляем слушатель на то, если пользователь уже зарегистрирован
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser()!= null){
                    user.setValue(firebaseAuth.getCurrentUser());
                }
            }
        });
    }

    public LiveData<String> getError() {
        return error;
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return user;
    }

    public void login(String email, String password){
        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) { // добавляем проверку на null
            auth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            //Log.d(TAG, "suck");
                           // user.setValue(authResult.getUser());
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "no");
                            error.setValue(e.getMessage());
                        }
                    });
        }
    }
}
