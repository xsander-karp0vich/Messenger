package com.example.messengertest1.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.messengertest1.model.pojo.Message;
import com.example.messengertest1.model.pojo.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends ViewModel {

    private MutableLiveData<List<Message>> messages = new MutableLiveData<>(); //эта лайв дата будет хранить все сообщения
    private MutableLiveData<User> otherUser = new MutableLiveData<>();// эта лайв дата будет хранить инфо о пользователе с которым идет переписка
    private MutableLiveData<Boolean> messageSent = new MutableLiveData<>(); //хранит состояние отправки сообщения
    private MutableLiveData<String> error = new MutableLiveData<>(); //будет хранить все ошибки

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(); //добавляем ссылки на firebase db
    private DatabaseReference referenceUsers = firebaseDatabase.getReference("Users");
    private DatabaseReference referenceMessages = firebaseDatabase.getReference("Messages"); //добавляем ссылку на Messages таблицу
    private String currentUserId; // добавляем поля на id current и other
    private String otherUserId;

    public ChatViewModel(String currentUserId, String otherUserId) {
        this.currentUserId = currentUserId;
        this.otherUserId = otherUserId;
        referenceUsers.child(otherUserId).addValueEventListener(new ValueEventListener() { //будем отслеживать юзера с которым ведется переписка, вызываем метод child
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //приведем конкретный экземпляр юзера в к типу User
                User user = snapshot.getValue(User.class);
                otherUser.setValue(user); //устанавливаем это значение в лайвдату
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        referenceMessages.child(currentUserId).child(otherUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Message> messageList = new ArrayList<>();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Message message = dataSnapshot.getValue(Message.class);
                    messageList.add(message);
                }
                messages.setValue(messageList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //добавляем геттеры на все поля
    public LiveData<List<Message>> getMessages() {
        return messages;
    }

    public LiveData<User> getOtherUser() {
        return otherUser;
    }

    public LiveData<Boolean> getMessageSent() {
        return messageSent;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void setUserOnline(boolean isOnline){
        referenceUsers.child(currentUserId).child("online").setValue(isOnline);
    }

    //логика отправки и добавления сообщения
    public void sendMessage(Message message){
        referenceMessages
                .child(message.getSenderId())
                .child(message.getReceiverid())
                .push()
                .setValue(message)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) { //повторяем все те же самые действия внутри onSuccess чтобы сохранить у получателя тоже
                        referenceMessages
                                .child(message.getReceiverid())
                                .child(message.getSenderId())
                                .push()
                                .setValue(message)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        messageSent.setValue(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        error.setValue(e.getMessage());
                    }
                });

    }
}
