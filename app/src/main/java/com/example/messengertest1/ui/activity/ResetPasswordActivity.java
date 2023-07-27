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
import com.example.messengertest1.ui.viewmodel.ResetPasswordViewModel;

public class ResetPasswordActivity extends AppCompatActivity {
    private EditText editTextEmail;
    private Button buttonResetPassword;
    private static final String EXTRA_EMAIL = "email"; //Передаем через интент email, для удобства юзера
    ResetPasswordViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initView();

        viewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        String email = getIntent().getStringExtra(EXTRA_EMAIL); //получаем текст email из интента
        editTextEmail.setText(email);

        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                viewModel.resetPassword(email);
            }
        });
        observeViewModel();
    }

    private void initView(){
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonResetPassword = findViewById(R.id.buttonResetPassword);
    }
    private void observeViewModel(){
        viewModel.getSuccess().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean success) {
                if (success){
                    Toast.makeText(ResetPasswordActivity.this, "Check your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error!=null){
                    Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static Intent newIntent(Context context, String email){
        Intent intent = new Intent(context, ResetPasswordActivity.class);
        intent.putExtra(EXTRA_EMAIL,email);
        return intent;
    }
}