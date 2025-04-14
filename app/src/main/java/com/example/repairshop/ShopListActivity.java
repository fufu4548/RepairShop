package com.example.repairshop;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShopListActivity extends AppCompatActivity {

    private FirebaseUser user;
    private static final String LOG_TAG =ShopListActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_list);
        user= FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
           Log.d(LOG_TAG,"Authenticated");
        }else{
            Log.d(LOG_TAG,"Not authenticated");
            finish();
        }
    }
}