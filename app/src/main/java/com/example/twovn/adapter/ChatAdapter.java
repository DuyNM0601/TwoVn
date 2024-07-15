package com.example.twovn.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.twovn.ChatActivity;
import com.example.twovn.R;
import com.example.twovn.model.ChatMessage;
import com.example.twovn.model.UserModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {
    private Context context;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private List<ChatMessage> chatMessageList;

    public ChatAdapter(Context context) {
        this.context = context;
        this.chatMessageList = new ArrayList<>();
    }

    public void add(ChatMessage chatMessage){
        chatMessageList.add(chatMessage);
        notifyDataSetChanged();
    }

    public void clear(){
        chatMessageList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if(viewType == VIEW_TYPE_SENT){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_sent, parent, false);
            return new MyViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_row_received, parent, false);
            return new MyViewHolder(view);
        }


    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.MyViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessageList.get(position);

        if(chatMessage.getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            holder.textViewSentMessage.setText(chatMessage.getMessage());
        }
        else{
            holder.textViewReceivedMessage.setText(chatMessage.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return chatMessageList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewSentMessage, textViewReceivedMessage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewSentMessage = itemView.findViewById(R.id.textViewSentMessage);
            textViewReceivedMessage = itemView.findViewById(R.id.textViewRecivedMessage);

        }
    }

    public List<ChatMessage> getChatMessageList(){
        return chatMessageList;
    }
    @Override
    public int getItemViewType(int position) {
        if(chatMessageList.get(position).getSenderId().equals(FirebaseAuth.getInstance().getUid())){
            return VIEW_TYPE_SENT;
        }
        else{
            return VIEW_TYPE_RECEIVED;
        }
    }
}


