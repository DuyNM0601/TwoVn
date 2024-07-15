package com.example.twovn;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.twovn.adapter.ChatAdapter;
import com.example.twovn.model.ChatMessage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    String receiverId, recerverName, senderRoom, receiverRoom, userId;
    DatabaseReference databaseReferenceSender, databaseReferenceReceiver, userRefernece;
    ImageButton sendButton;
    EditText messageEditText;
    RecyclerView recyclerView;
    ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbarchat);
        setSupportActionBar(toolbar);

        userRefernece = FirebaseDatabase.getInstance().getReference("users");
        receiverId = getIntent().getStringExtra("id");
        recerverName = getIntent().getStringExtra("name");

        getSupportActionBar().setTitle(recerverName);
        if (receiverId != null) {
            senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
            receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();
        }
        else {
            // If receiverId is null, it's a chat with admin
            receiverId = "URSrrWSw8OfiBR29HwIbPlmNZ4m2"; // Admin's UID
            recerverName = "admin";
            senderRoom = FirebaseAuth.getInstance().getUid() + receiverId;
            receiverRoom = receiverId + FirebaseAuth.getInstance().getUid();
        }

        sendButton = findViewById(R.id.sendButton);
        chatAdapter = new ChatAdapter(this);
        recyclerView = findViewById(R.id.messageRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);

        recyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Ensure the newest messages appear at the bottom
        recyclerView.setLayoutManager(layoutManager);

        databaseReferenceSender = FirebaseDatabase.getInstance().getReference("chats").child(senderRoom);
        databaseReferenceReceiver = FirebaseDatabase.getInstance().getReference("chats").child(receiverRoom);

        databaseReferenceSender.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMessage> chatMessages = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                    chatMessages.add(message);
                }
                Collections.sort(chatMessages, new Comparator<ChatMessage>() {
                    @Override
                    public int compare(ChatMessage m1, ChatMessage m2) {
                        return Long.compare(m1.getTimestamp(), m2.getTimestamp());
                    }
                });
                chatAdapter.clear();
                for (ChatMessage chatMessage : chatMessages) {
                    chatAdapter.add(chatMessage);
                }
                recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = messageEditText.getText().toString();
                if (message.trim().length() > 0) {
                    SendMessage(message);
                } else {
                    Toast.makeText(ChatActivity.this, "Vui lòng nhập tin nhắn", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void SendMessage(String message) {
        String messageId = UUID.randomUUID().toString();
        ChatMessage chatMessage = new ChatMessage(messageId, FirebaseAuth.getInstance().getUid(), message);
        chatAdapter.add(chatMessage);

        databaseReferenceSender.child(messageId).setValue(chatMessage)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ChatActivity.this, "Lỗi khi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                    }
                });
        databaseReferenceReceiver.child(messageId).setValue(chatMessage);
        recyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
        messageEditText.setText("");
    }

}
