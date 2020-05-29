package com.ming.messagingapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ming.messagingapp.R;
import com.ming.messagingapp.user.User;

import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {
    private List<Chat> chatList;
    private ChatAdapterClickListener clickListener;

    public interface ChatAdapterClickListener {
        void onClick(Chat chat);
    }

    public ChatListAdapter(List<Chat> chatList, ChatAdapterClickListener clickListener) {
        this.chatList = chatList;
        this.clickListener = clickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_row, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String title = chatList.get(position).getId();
        holder.title.setText(title);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView title;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            title = itemView.findViewById(R.id.tv_chat_title);
        }

        @Override
        public void onClick(View view) {
            Chat chat = chatList.get(getAdapterPosition());
            clickListener.onClick(chat);
        }
    }
}
