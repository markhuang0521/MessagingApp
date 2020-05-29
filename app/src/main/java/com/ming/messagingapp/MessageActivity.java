package com.ming.messagingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ming.messagingapp.chat.Chat;
import com.ming.messagingapp.chat.ChatListAdapter;
import com.ming.messagingapp.chat.Message;
import com.ming.messagingapp.chat.MessageAdapter;
import com.ming.messagingapp.media.MediaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MessageActivity extends AppCompatActivity implements MessageAdapter.MessageAdapterClickListener {
    private FirebaseAuth firebaseAuth;
    private StorageReference firebaseStorage;
    private RecyclerView recyclerMessageList, recyclerMediaList;
    private MessageAdapter messageAdapter;
    private MediaAdapter mediaAdapter;
    private List<Message> messageList;
    private List<String> mediaList;
    private DatabaseReference messageDb;
    private Button btnSendMessage, btnSendMedia;
    private EditText etMessage;
    private static String chatId;
    public static final int IMAGE_INTENT_REQUEST_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.containsKey(UserDetailActivity.CHAT_BUNDLE_ID)) {
                chatId = bundle.getString(UserDetailActivity.CHAT_BUNDLE_ID);
                messageDb = FirebaseDatabase.getInstance().getReference().child("chats").child(chatId).push();


            }
        }

        etMessage = findViewById(R.id.et_message_info);
        btnSendMessage = findViewById(R.id.btn_send_message);
        btnSendMedia = findViewById(R.id.btn_send_media);
        btnSendMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        initialRecyclerView();
        initialMediRecyclerView();
        getMessages();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(intent, IMAGE_INTENT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_INTENT_REQUEST_CODE:
                    if (data.getClipData() == null)
                        mediaList.add(data.getData().toString());
                    else {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            mediaList.add(data.getClipData().getItemAt(i).toString());

                        }
                    }
                    mediaAdapter.notifyDataSetChanged();
                    break;

            }
        }

    }

    private void getMessages() {
        messageDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String message = "";
                    String senderId = "";
                    if (dataSnapshot.child("sender").exists()) {
                        senderId = dataSnapshot.child("sender").getValue().toString();
                    }
                    if (dataSnapshot.child("message").exists()) {
                        message = dataSnapshot.child("message").getValue().toString();
                    }

                    Message newMessage = new Message(dataSnapshot.getKey(), message, senderId);
                    messageList.add(newMessage);
                    recyclerMessageList.scrollToPosition(messageList.size() - 1);
                    messageAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    int count = 0;
     List<String> mediaIdList = new ArrayList<>();

    private void sendMessage() {

        String message = etMessage.getText().toString();
        String messageId = messageDb.push().getKey();
        final DatabaseReference newMessageDb = messageDb.child(messageId);
        final Map<String, Object> map = new HashMap<>();
        map.put("sender", firebaseAuth.getUid());

        if (!TextUtils.isEmpty(message)) {
            map.put("message", message);
        }


        if (!mediaList.isEmpty()) {
            for (String media : mediaList) {
                String mediaId = newMessageDb.push().getKey();
                mediaIdList.add(mediaId);
                firebaseStorage = FirebaseStorage.getInstance().getReference().child("media").child(chatId).child(messageId).child(mediaId);
                UploadTask uploadTask = firebaseStorage.putFile(Uri.parse(media));
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        firebaseStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                map.put("/media/" + mediaIdList.get(count) + "/", uri.toString());
                                count++;
                                if (count == mediaList.size()) {
                                    updateFirebaseWithNewMessage(newMessageDb, map);
                                }

                            }
                        });
                    }
                });
            }
        } else {
            if (!TextUtils.isEmpty(message)) {
                updateFirebaseWithNewMessage(newMessageDb, map);
            }
        }

    }

    private void updateFirebaseWithNewMessage(DatabaseReference db, Map message) {
        db.updateChildren(message);
        etMessage.setText(null);
        mediaList.clear();
        mediaIdList.clear();

        mediaAdapter.notifyDataSetChanged();
        count = 0;


    }

    private void initialRecyclerView() {
        messageList = new ArrayList<>();
        recyclerMessageList = findViewById(R.id.recycler_message_list);
        recyclerMessageList.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessageList.setHasFixedSize(true);

        messageAdapter = new MessageAdapter(messageList, this);
        recyclerMessageList.setAdapter(messageAdapter);
    }

    private void initialMediRecyclerView() {
        mediaList = new ArrayList<>();
        recyclerMediaList = findViewById(R.id.recycler_media_list);
//        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerMediaList.setLayoutManager(new LinearLayoutManager(this));
        recyclerMediaList.setHasFixedSize(true);

        mediaAdapter = new MediaAdapter(mediaList, this);
        recyclerMediaList.setAdapter(mediaAdapter);
    }

    @Override
    public void onClick(Message message) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }


        return super.onOptionsItemSelected(item);
    }
}
