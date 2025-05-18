package com.example.repairshop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import android.Manifest;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddItemActivity extends AppCompatActivity {
    private static final int NOTIFICATION_PERMISSION_CODE = 100;

    private EditText editName, editInfo, editPrice;
    private Button btnSave;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        editName = findViewById(R.id.edit_name);
        editInfo = findViewById(R.id.edit_info);
        editPrice = findViewById(R.id.edit_price);
        btnSave = findViewById(R.id.btn_save);
        db = FirebaseFirestore.getInstance();

        btnSave.setOnClickListener(v -> saveItem());


    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            }
    }
    }

    private void showItemAddedNotification() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "item_channel";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Item Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("Elem létrehozva!")
                .setContentText("Sikeresen létre lett hozva a hirdetés")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(1, builder.build());
    }



    private void saveItem() {
        String name = editName.getText().toString().trim();
        String info = editInfo.getText().toString().trim();
        String priceText = editPrice.getText().toString().trim();

        if (name.isEmpty() || info.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(this, "Tölts ki minden mezőt.", Toast.LENGTH_SHORT).show();
            return;
        }




        Map<String, Object> item = new HashMap<>();
        item.put("name", name);
        item.put("info", info);
        item.put("price", priceText);

        db.collection("items")
                .add(item)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Elem létrehozva", Toast.LENGTH_SHORT).show();
                    checkNotificationPermission();
                    showItemAddedNotification();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sikertelen az elem létrehozása", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE", "Add failed", e);
                });
    }
}
