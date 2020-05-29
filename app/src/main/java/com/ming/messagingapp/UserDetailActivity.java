package com.ming.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ming.messagingapp.chat.Chat;
import com.ming.messagingapp.chat.ChatListAdapter;
import com.ming.messagingapp.user.User;
import com.ming.messagingapp.user.UserListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserDetailActivity extends AppCompatActivity implements ChatListAdapter.ChatAdapterClickListener {
    private FirebaseAuth firebaseAuth;
    private RecyclerView recycleruserList;
    private ChatListAdapter chatListAdapter;
    private List<Chat> chatList;
    private DatabaseReference chatDb;
    public static final String CHAT_BUNDLE_ID = "chat id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_detail);
        Fresco.initialize(this);

        chatList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        chatDb = FirebaseDatabase.getInstance().getReference().child("users").child(firebaseAuth.getUid()).child("chats");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        getPermission();
        initialRecyclerView();
        getUserChat();
    }

    private void getUserChat() {
        final Map<String, String> chatStringMap = new HashMap<>();
        chatDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Chat chat = new Chat(snapshot.getKey());
                        chatList.add(chat);
                        chatListAdapter.notifyDataSetChanged();

//
//                        chatStringMap.put(chat.getId(), "1");
//                        if (!chatStringMap.containsKey(chat.getId())) {
//                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void initialRecyclerView() {
        recycleruserList = findViewById(R.id.recycler_detail_list);
        recycleruserList.setLayoutManager(new LinearLayoutManager(this));
        recycleruserList.setHasFixedSize(true);

        chatListAdapter = new ChatListAdapter(chatList, this);
        recycleruserList.setAdapter(chatListAdapter);
    }


    private void getPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }

    private void logout() {
        firebaseAuth.signOut();
        Intent intent = new Intent(UserDetailActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_page_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_log_out:
                logout();
                break;

            case R.id.menu_search_user:
                startActivity(new Intent(UserDetailActivity.this, FindUserActivity.class));
                break;
            case android.R.id.home:
                finish();
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(Chat chat) {
        Intent intent = new Intent(UserDetailActivity.this, MessageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(CHAT_BUNDLE_ID, chat.getId());
        intent.putExtras(bundle);
        startActivity(intent);


    }
}
