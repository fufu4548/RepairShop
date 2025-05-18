package com.example.repairshop;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.SystemClock;

public class RepairListActivity extends AppCompatActivity {

    private FirebaseUser user;
    private static final String LOG_TAG = RepairListActivity.class.getName();
    private LinearLayout menuContainer;
    private final List<RepairItem> repairItems = new ArrayList<>();
    private LayoutInflater inflater;

    @SuppressLint("ScheduleExactAlarm")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repair_list);
        user = FirebaseAuth.getInstance().getCurrentUser();
        menuContainer = findViewById(R.id.menu_container);
        inflater = LayoutInflater.from(this);

        Button btnAdd = findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(v -> {
            Intent intent = new Intent(RepairListActivity.this, AddItemActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
        });


        if(user != null){
           Log.d(LOG_TAG,"Authenticated");
           scheduleAlarm();
        }else{
            Log.d(LOG_TAG,"Not authenticated");
            finish();
        }
    }



    private void loadItemsFromFirestore() {
        menuContainer.removeAllViews();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("items").get()
                .addOnSuccessListener(query -> {
                    for (DocumentSnapshot doc : query) try {
                        RepairItem item = doc.toObject(RepairItem.class);
                        if (item != null) {
                            View itemView = inflater.inflate(R.layout.item_listing, menuContainer, false);
                            ((TextView) itemView.findViewById(R.id.item_name)).setText(item.getName());
                            ((TextView) itemView.findViewById(R.id.item_info)).setText(item.getInfo());
                            ((TextView) itemView.findViewById(R.id.item_price)).setText(item.getPrice());

                            ImageView itemImage = itemView.findViewById(R.id.item_image);

                            String imageUrl = item.getImageResource();
                            if (imageUrl == null || imageUrl.isEmpty() || imageUrl.equals("placeholder")) {
                                // Show local placeholder image
                                itemImage.setImageResource(R.drawable.placeholder);
                            } else {
                                // Load image from URL using Glide (add Glide dependency first)
                                Glide.with(this)
                                        .load(imageUrl)
                                        .placeholder(R.drawable.placeholder) // while loading
                                        .error(R.drawable.placeholder) // if failed
                                        .into(itemImage);
                            }

                            Button editButton = itemView.findViewById(R.id.buy_button);
                            editButton.setOnClickListener(v -> {
                                Intent intent = new Intent(RepairListActivity.this, EditItemActivity.class);
                                intent.putExtra("ITEM_ID", doc.getId());
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            });

                            menuContainer.addView(itemView);
                        }
                    } catch (Exception e) {
                        Log.e("FIRESTORE", "Error processing item", e);
                    }
                    Log.d("FIRESTORE", "Success loading items");
                }).addOnFailureListener(e -> {
                    Log.d("FIRESTORE", "Error loading items");
                    Toast.makeText(this, "Nem sikerült betölteni az elemeket.", Toast.LENGTH_SHORT).show();
                });
    }

    @SuppressLint("ScheduleExactAlarm")
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    public void scheduleAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        // Trigger after 18 seconds from now (adjust as needed)
        long triggerAtMillis = SystemClock.elapsedRealtime() + 18000;

        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);

    }


    @Override
    protected void onStart() {
        super.onStart();
        loadItemsFromFirestore();
    }


}









