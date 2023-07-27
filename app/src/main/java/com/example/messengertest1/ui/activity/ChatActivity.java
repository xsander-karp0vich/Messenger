package com.example.messengertest1.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengertest1.R;
import com.example.messengertest1.model.pojo.Message;
import com.example.messengertest1.model.pojo.User;
import com.example.messengertest1.ui.adapters.MessagesAdapter;
import com.example.messengertest1.ui.viewmodel.ChatViewModel;
import com.example.messengertest1.ui.viewmodelfactory.ChatViewModelFactory;

import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static final String EXTRA_CURRENT_USER_ID = "current_id";
    private static final String EXTRA_OTHER_USER_ID = "other_id";
    private TextView textViewTitle;
    private View onlineStatusView;
    private RecyclerView messageRecycleView;
    private EditText editTextMessage;
    private ImageView imageViewSendMessage;

    private MessagesAdapter messagesAdapter;
    private String currentUserId;
    private String otherUserId;
    private ChatViewModel viewModel;
    private ChatViewModelFactory viewModelFactory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initView();

        currentUserId = getIntent().getStringExtra(EXTRA_CURRENT_USER_ID);
        otherUserId = getIntent().getStringExtra(EXTRA_OTHER_USER_ID);
        viewModelFactory = new ChatViewModelFactory(currentUserId,otherUserId);
        viewModel = new ViewModelProvider(this,viewModelFactory).get(ChatViewModel.class);
        messagesAdapter = new MessagesAdapter(currentUserId);
        messageRecycleView.setAdapter(messagesAdapter);

        observeViewModel();
        clickListener();
    }
    private void initView(){
        textViewTitle = findViewById(R.id.textViewTitle);
        onlineStatusView = findViewById(R.id.onlineStatusView);
        messageRecycleView = findViewById(R.id.messageRecycleView);
        editTextMessage = findViewById(R.id.editTextMessage);
        imageViewSendMessage = findViewById(R.id.imageViewSendMessage);
    }
    private void clickListener(){
        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageText = editTextMessage.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    Message message = new Message(
                            messageText,
                            currentUserId,
                            otherUserId
                    );
                    viewModel.sendMessage(message);
                }
            }
        });
    }


    private void observeViewModel(){
        viewModel.getMessages().observe(this, new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messages) {
                messagesAdapter.setMessages(messages);
            }
        });
        viewModel.getError().observe(this, new Observer<String>() {
            @Override
            public void onChanged(String error) {
                if (error!=null){
                    Toast.makeText(ChatActivity.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
        viewModel.getMessageSent().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean sent) {
                if (sent){
                    editTextMessage.setText("");
                }
            }
        });
        viewModel.getOtherUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                String userInfo = String.format("%s %s",user.getName(), user.getLastName());
                textViewTitle.setText(userInfo);

                int bgResId;

                if (user.isOnline()){
                    bgResId = R.drawable.green_circle;
                } else{
                    bgResId = R.drawable.red_circle;
                }
                Drawable background = ContextCompat.getDrawable(ChatActivity.this,bgResId);
                onlineStatusView.setBackground(background);
            }
        });
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

    public static Intent newIntent(Context context, String currentUserId, String otherUserId){
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EXTRA_CURRENT_USER_ID,currentUserId);
        intent.putExtra(EXTRA_OTHER_USER_ID,otherUserId);
        return intent;
    }
}