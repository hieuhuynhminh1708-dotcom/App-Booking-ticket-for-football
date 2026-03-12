package com.example.client.admin;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class TicketManagerActivity extends AppCompatActivity {

    private EditText edtVip, edtA, edtB, edtC, edtD;
    private DatabaseReference ref;
    private String matchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ticket_manager);

        // ================= INIT VIEW =================
        edtVip = findViewById(R.id.edtVip);
        edtA = findViewById(R.id.edtA);
        edtB = findViewById(R.id.edtB);
        edtC = findViewById(R.id.edtC);
        edtD = findViewById(R.id.edtD);

        Button btnSave = findViewById(R.id.btnSave);
        Button btnBack = findViewById(R.id.btnBack);

        // ================= GET matchId =================
        matchId = getIntent().getStringExtra("matchId");

        if (matchId == null || matchId.isEmpty()) {
            Toast.makeText(this, "Thiếu matchId", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // ================= FIREBASE REF =================
        ref = FirebaseDatabase.getInstance()
                .getReference("matches")
                .child(matchId)
                .child("ticketPrices");

        // 🔥 LOAD GIÁ VÉ HIỆN TẠI (NẾU CÓ)
        loadCurrentPrices();

        btnSave.setOnClickListener(v -> savePrices());
        btnBack.setOnClickListener(v -> finish());
    }

    // ================= LOAD GIÁ CŨ =================
    private void loadCurrentPrices() {
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (!snapshot.exists()) return;

                setIfExist(snapshot, "vip", edtVip);
                setIfExist(snapshot, "A", edtA);
                setIfExist(snapshot, "B", edtB);
                setIfExist(snapshot, "C", edtC);
                setIfExist(snapshot, "D", edtD);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TicketManagerActivity.this,
                        "Không tải được giá vé",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setIfExist(DataSnapshot s, String key, EditText edt) {
        Integer value = s.child(key).getValue(Integer.class);
        if (value != null) {
            edt.setText(String.valueOf(value));
        }
    }

    // ================= SAVE GIÁ =================
    private void savePrices() {

        Map<String, Integer> prices = new HashMap<>();

        prices.put("vip", parse(edtVip));
        prices.put("A", parse(edtA));
        prices.put("B", parse(edtB));
        prices.put("C", parse(edtC));
        prices.put("D", parse(edtD));

        ref.setValue(prices)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this,
                                "Đã lưu giá vé",
                                Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Lỗi: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    private int parse(EditText edt) {
        String s = edt.getText().toString().trim();
        return s.isEmpty() ? 0 : Integer.parseInt(s);
    }
}
