package com.ming.messagingapp.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ming.messagingapp.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private List<Message> messageList;
    private MessageAdapterClickListener clickListener;


    public interface MessageAdapterClickListener {
        void onClick(Message message);
    }

    public MessageAdapter(List<Message> chatList, MessageAdapterClickListener clickListener) {
        this.messageList = chatList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message message = messageList.get(position);
        holder.sender.setText(message.getSenderId());
        holder.message.setText(message.getMessage());

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView message, sender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            message = itemView.findViewById(R.id.tv_message_chat);
            sender = itemView.findViewById(R.id.tv_message_sender);
        }

        @Override
        public void onClick(View view) {
            Message message = messageList.get(getAdapterPosition());
            clickListener.onClick(message);
        }
    }
}
