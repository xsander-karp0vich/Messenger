package com.example.messengertest1.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengertest1.R;
import com.example.messengertest1.model.pojo.User;
import com.example.messengertest1.ui.adapters.UsersAdapter;
import com.example.messengertest1.ui.viewmodel.UsersViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class UsersActivity extends AppCompatActivity {

    private static final String EXTRA_CURRENT_USER_ID = "current_id";
    private UsersViewModel viewModel;
    private TextView logoutTextView;
    private RecyclerView usersRecycleView;
    private UsersAdapter usersAdapter;
    private String currentUserId;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); //объявляем бд в юзерс активити
    private DatabaseReference databaseReference = firebaseDatabase.getReference("Users"); //получаем ссылку на databaseReference
    //используется для чтения и записи данных в этот узел. В данном случае, когда мы вызываем firebaseDatabase.getReference(),
    // мы получаем корневую ссылку на базу данных

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
        initView();

        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);

        viewModel = new ViewModelProvider(this).get(UsersViewModel.class);
        observeViewModel();
        clickListeners();




        //databaseReference.push().setValue("Hello, Wold!"); //чтобы добавлять разные значения по одному ключу нужно вызвать метод push() перед setValue()
        usersAdapter.setOnUserClickListener(new UsersAdapter.OnUserClickListener() { //устанавливаем клик листенер на users recycle view
            @Override
            public void onUserClick(User user) {
                Intent intent = ChatActivity.newIntent(UsersActivity.this,currentUserId,user.getId());
                startActivity(intent);
            }
        });
    }



    void initView(){
        logoutTextView = findViewById(R.id.logoutTextView);
        usersRecycleView = findViewById(R.id.usersRecycleView);
        usersAdapter = new UsersAdapter();
        usersRecycleView.setAdapter(usersAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        viewModel.setUserOnline(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        viewModel.setUserOnline(false);
    }

    public void observeViewModel(){
        viewModel.getUser().observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                if (firebaseUser == null){
                    Intent intent = LoginActivity.newIntent(UsersActivity.this);
                    startActivity(intent);
                    finish();
                }
            }
        });
        viewModel.getUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                usersAdapter.setUsers(users);
            }
        });
    }
    public void clickListeners(){
        logoutTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                viewModel.logout();

            }
        });
    }

    public static Intent newIntent(Context context, String currentUserId){
        Intent intent = new Intent(context, UsersActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID, currentUserId);
        return intent;
    }
}