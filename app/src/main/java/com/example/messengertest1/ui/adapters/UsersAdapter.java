package com.example.messengertest1.ui.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.messengertest1.R;
import com.example.messengertest1.model.pojo.User;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users = new ArrayList<>();

    private OnUserClickListener onUserClickListener;

    public void setOnUserClickListener(OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        String userInfo = String.format("%s %s, %s", user.getName(), user.getLastName(), user.getAge());
        holder.userInfoTextView.setText(userInfo);

        int bgResId;

        if (user.isOnline()){
            bgResId = R.drawable.green_circle;
        } else{
            bgResId = R.drawable.red_circle;
        }
        Drawable background = ContextCompat.getDrawable(holder.itemView.getContext(),bgResId);
        holder.onlineView.setBackground(background);

        //устанавливаем слушатель клика:
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserClickListener!= null){
                    onUserClickListener.onUserClick(user);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    static class UserViewHolder extends RecyclerView.ViewHolder{
        private TextView userInfoTextView;
        private View onlineView;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            userInfoTextView = itemView.findViewById(R.id.userInfoTextView);
            onlineView = itemView.findViewById(R.id.onlineView);
        }
    }
}
