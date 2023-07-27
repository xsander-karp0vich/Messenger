package com.example.messengertest1.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.messengertest1.R;
import com.example.messengertest1.ui.viewmodel.LoginViewModel;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity { //переименовали в login_activity
    //1. Добавили проект в Firebase
    //2. Регистрируем проект опционально можно добавить SHA-1, если планируется разработка функционала регистрация через номер телефона
    //3. Скачиваем и устанавливаем в проект файл JSON google-services config
    //4. Добавляем нужные зависимости и плагины в gradle на уровне project и module
    //Чтобы добавить возможность регистрации через email/password необходимо добавить данную конфигурацию в Firebase консоли


    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private TextView forgotPasswordTextView;
    private TextView registrationTextView;
    private String email;
    private String password;

    private LoginViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        observeViewModel();
        clickListeners();

    }


    private void initView(){
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogIn = findViewById(R.id.buttonLogIn);
        forgotPasswordTextView = findViewById(R.id.forgotPasswordTextView);
        registrationTextView = findViewById(R.id.registerTextView);
    }
    private void clickListeners() { //выносим все клик листенеры с каллбеками в отдельный метод

        buttonLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = editTextEmail.getText().toString().trim();
                password = editTextPassword.getText().toString().trim();
                if (email != null && password != null && !email.isEmpty() && !password.isEmpty()) {
                    viewModel.login(email, password);
                } else {
                    Toast.makeText(LoginActivity.this, "Enter both email and password!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        registrationTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = RegistrationActivity.newIntent(LoginActivity.this);
                startActivity(intent);

            }
        });
        forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = ResetPasswordActivity.newIntent(LoginActivity.this, editTextEmail.getText().toString().trim());
                startActivity(intent);
            }
        });
    }
    private void observeViewModel(){ //создаем отдельный метод для подписки на лайв даты
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String errorMessage) {
                if (errorMessage!=null){
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }
        });
        //
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser!=null){
                    //Toast.makeText(LoginActivity.this, "Authorized", Toast.LENGTH_SHORT).show();
                    Intent intent = UsersActivity.newIntent(LoginActivity.this, firebaseUser.getUid());
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
    public static Intent newIntent(Context context){
        return new Intent(context, LoginActivity.class);
    }


}
//В контексте разработки на платформе Android, метод getInstance()
// является распространенным шаблоном проектирования, который применяется
// для получения единственного экземпляра определенного класса. Этот метод
// обычно используется для создания и предоставления доступа к этому единственному
// экземпляру, чтобы избежать создания дубликатов объектов и обеспечить глобальный
// доступ к этому экземпляру из разных частей приложения.