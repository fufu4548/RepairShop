package com.example.repairshop;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

public class EditItemActivity extends AppCompatActivity {

    private EditText editName, editInfo, editPrice, editImageUrl;
    private Button buttonSave, buttonDelete;

    private FirebaseFirestore db;

    String itemId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        editName = findViewById(R.id.edit_name);
        editInfo = findViewById(R.id.edit_info);
        editPrice = findViewById(R.id.edit_price);
        buttonSave = findViewById(R.id.btn_mod);
         buttonDelete = findViewById(R.id.btn_del);

        db = FirebaseFirestore.getInstance();

        itemId = getIntent().getStringExtra("ITEM_ID");
        if (itemId == null) {
            Toast.makeText(this, "Item ID hiányzik", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadItemData();

        buttonSave.setOnClickListener(v -> saveChanges());
        buttonDelete.setOnClickListener(v -> confirmDelete());
    }

    private void loadItemData() {
        db.collection("items").document(itemId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        RepairItem item = documentSnapshot.toObject(RepairItem.class);
                        if (item != null) {
                            editName.setText(item.getName());
                            editInfo.setText(item.getInfo());
                            editPrice.setText(item.getPrice());
                        }
                    } else {
                        Toast.makeText(this, "Elem nem található", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Az elemet nem lehetett betölteni", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void saveChanges() {
        String name = editName.getText().toString().trim();
        String info = editInfo.getText().toString().trim();
        String price = editPrice.getText().toString().trim();
        String image = null;

        if (name.isEmpty() || price.isEmpty() ||info.isEmpty()) {
            Toast.makeText(this, "A mezők kitöltése kötelező", Toast.LENGTH_SHORT).show();
            return;
        }

        RepairItem updatedItem = new RepairItem(name, info, price, image);

        db.collection("items").document(itemId)
                .set(updatedItem)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Elem frissítve", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Elem frissítése sikertelen", Toast.LENGTH_SHORT).show();
                });
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Elem törlése")
                .setMessage("Biztos vagy benne, hogy szeretnéd törölni?")
                .setPositiveButton("Igen", (dialog, which) -> deleteItem())
                .setNegativeButton("Mégse", null)
                .show();
    }
    private void deleteItem() {
        db.collection("items").document(itemId)
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Elem törölve", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Sikertelen törlés", Toast.LENGTH_SHORT).show();
                    Log.e("FIRESTORE", "Delete failed", e);
                });
    }

}
