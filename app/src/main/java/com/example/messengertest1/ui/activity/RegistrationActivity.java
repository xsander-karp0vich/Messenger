package com.example.messengertest1.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.messengertest1.R;
import com.example.messengertest1.ui.viewmodel.RegistrationViewModel;
import com.google.firebase.auth.FirebaseUser;

public class RegistrationActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private EditText editTextPassword;
    private EditText editTextName;
    private EditText editTextLastName;
    private EditText editTextYearsOld;
    private Button signUpButton;

    private RegistrationViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initView();

        viewModel = new ViewModelProvider(this).get(RegistrationViewModel.class);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = getTrimmedValue(editTextEmail);
                String password = getTrimmedValue(editTextPassword);
                String name = getTrimmedValue(editTextName);
                String lastName = getTrimmedValue(editTextLastName);
                int age = Integer.parseInt(getTrimmedValue(editTextYearsOld));


                viewModel.signUp(email,password,name,lastName,age);
            }
        });
        observeViewModel();
    }

    private void initView(){
        editTextEmail = findViewById(R.id.editTextEmail);
          editTextPassword = findViewById(R.id.editTextPassword);
          editTextName = findViewById(R.id.editTextName);
          editTextLastName = findViewById(R.id.editTextLastName);
          editTextYearsOld = findViewById(R.id.editTextYearsOld);
          signUpButton = findViewById(R.id.signUpButton);
    }

    private void observeViewModel(){
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error!=null){
                    Toast.makeText(RegistrationActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser!=null){
                    Intent intent = UsersActivity.newIntent(RegistrationActivity.this, firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    private String getTrimmedValue(EditText editText){ //для лаконичности вынесли повторяющийся код в отдельный метод
        return editText.getText().toString().trim();
    }
    public static Intent newIntent(Context context){
        return new Intent(context,RegistrationActivity.class);
    }
}