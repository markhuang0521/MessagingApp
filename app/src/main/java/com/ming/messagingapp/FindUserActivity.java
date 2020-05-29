package com.ming.messagingapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ming.messagingapp.user.User;
import com.ming.messagingapp.user.UserListAdapter;
import com.ming.messagingapp.utils.CountryToPhonePrefix;

import java.util.ArrayList;
import java.util.List;

public class FindUserActivity extends AppCompatActivity implements UserListAdapter.UserListAdapterOnClickHandler {
    private RecyclerView recyclerList;
    private UserListAdapter userListAdapter;
    private List<User> userList, contactList;
    private DatabaseReference firebase;
    private FirebaseAuth firebaseAuth;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);
        userList = new ArrayList<>();
        contactList = new ArrayList<>();
        firebase = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initialRecyclerView();
        getContactList();
    }


    private void getContactList() {
        String iso = getCountryISO();
        Cursor phoneCursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        assert phoneCursor != null;
        while (phoneCursor.moveToNext()) {
            String name = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("-", "");
            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");
            if (!String.valueOf(phoneNumber.charAt(0)).equals("+")) {
                phoneNumber = iso + phoneNumber;
            }

            User user = new User(name, phoneNumber);
            contactList.add(user);
            getUserDetail(user);

        }
    }


    private void getUserDetail(User user) {
        final DatabaseReference mDb = FirebaseDatabase.getInstance().getReference().child("users");
        Query query = mDb.orderByChild("phone").equalTo(user.getPhoneNumber());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String phone = "";
                    String name = "";
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if (snapshot.child("phone").getValue() != null) {
                            phone = snapshot.child("phone").getValue().toString();
                        }
                        if (snapshot.child("name").getValue() != null) {
                            name = snapshot.child("name").getValue().toString();
                        }
                        User mUser = new User(name, phone, snapshot.getKey());
                        if (name.equals(phone)) {
                            for (User contactUser : contactList) {
                                if (contactUser.getPhoneNumber().equals(mUser.getPhoneNumber())) {
                                    mUser.setName(contactUser.getName());

                                }
                            }
                        }
                        userList.add(mUser);
                        userListAdapter.notifyDataSetChanged();
                        return;
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private String getCountryISO() {
        String iso = null;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso() != null && !telephonyManager.getNetworkCountryIso().isEmpty()) {
            iso = telephonyManager.getNetworkCountryIso().toString();
        }
        return CountryToPhonePrefix.getPhone(iso);

    }


    private void initialRecyclerView() {
        recyclerList = findViewById(R.id.recycler_user_list);
        recyclerList.setLayoutManager(new LinearLayoutManager(this));
        recyclerList.setHasFixedSize(true);

        userListAdapter = new UserListAdapter(userList, this);
        recyclerList.setAdapter(userListAdapter);
    }


    @Override
    public void onClick(User user) {
        String chatKey = firebase.child("chats").push().getKey();
        firebase.child("users").child(firebaseAuth.getUid()).child("chats").child(chatKey).setValue(true);
        firebase.child("users").child(user.getUid()).child("chats").child(chatKey).setValue(true);
        finish();

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
