package com.example.client.admin;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.client.R;
import com.example.client.models.Match;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AdminAddEditMatchActivity extends AppCompatActivity {

    private EditText edtHome, edtAway, edtDate, edtTime, edtStadium;
    private EditText edtVip, edtA, edtB, edtC, edtD;
    private TextView tvTitle;

    private String matchId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_edit_match);

        // HEADER
        tvTitle = findViewById(R.id.tvTitle);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // INFO
        edtHome = findViewById(R.id.edtHome);
        edtAway = findViewById(R.id.edtAway);
        edtDate = findViewById(R.id.edtDate);
        edtTime = findViewById(R.id.edtTime);
        edtStadium = findViewById(R.id.edtStadium);

        // ✅ Khóa nhập tay cho Date/Time + bấm để chọn
        edtDate.setKeyListener(null);
        edtTime.setKeyListener(null);

        edtDate.setOnClickListener(v -> showDatePicker());
        edtTime.setOnClickListener(v -> showTimePicker());

        // PRICE
        edtVip = findViewById(R.id.edtVip);
        edtA = findViewById(R.id.edtA);
        edtB = findViewById(R.id.edtB);
        edtC = findViewById(R.id.edtC);
        edtD = findViewById(R.id.edtD);

        // FORMAT VNĐ
        addCurrencyWatcher(edtVip);
        addCurrencyWatcher(edtA);
        addCurrencyWatcher(edtB);
        addCurrencyWatcher(edtC);
        addCurrencyWatcher(edtD);

        // EDIT MODE
        matchId = getIntent().getStringExtra("matchId");
        if (matchId != null && !matchId.isEmpty()) {
            tvTitle.setText("Sửa trận đấu");

            edtHome.setText(getIntent().getStringExtra("homeTeam"));
            edtAway.setText(getIntent().getStringExtra("awayTeam"));
            edtDate.setText(getIntent().getStringExtra("date"));
            edtTime.setText(getIntent().getStringExtra("time"));
            edtStadium.setText(getIntent().getStringExtra("stadium"));

            setPrice(edtVip, getIntent().getIntExtra("vip", 0));
            setPrice(edtA, getIntent().getIntExtra("A", 0));
            setPrice(edtB, getIntent().getIntExtra("B", 0));
            setPrice(edtC, getIntent().getIntExtra("C", 0));
            setPrice(edtD, getIntent().getIntExtra("D", 0));

        } else {
            tvTitle.setText("Thêm trận đấu");
        }

        findViewById(R.id.btnSave).setOnClickListener(v -> save());
    }

    // ================= DATE / TIME PICKER =================

    private void showDatePicker() {
        Calendar cal = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String date = String.format("%02d/%02d/%d", dayOfMonth, (month + 1), year);
                    edtDate.setText(date);
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {
                    String time = String.format("%02d:%02d", hourOfDay, minute);
                    edtTime.setText(time);
                },
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true // 24h
        );

        dialog.show();
    }

    // ================= FORMAT VNĐ =================

    private void addCurrencyWatcher(EditText edt) {
        edt.addTextChangedListener(new TextWatcher() {
            boolean editing;

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (editing) return;
                editing = true;

                String raw = s.toString().replace(".", "");
                if (!raw.isEmpty()) {
                    try {
                        long value = Long.parseLong(raw);
                        String formatted = String.format("%,d", value).replace(",", ".");
                        edt.setText(formatted);
                        edt.setSelection(formatted.length());
                    } catch (Exception ignored) {}
                }

                editing = false;
            }
        });
    }

    private void setPrice(EditText edt, int value) {
        if (value > 0) {
            String formatted = String.format("%,d", value).replace(",", ".");
            edt.setText(formatted);
        }
    }

    // ================= SAVE =================

    private void save() {
        String home = edtHome.getText().toString().trim();
        String away = edtAway.getText().toString().trim();
        String date = edtDate.getText().toString().trim();
        String time = edtTime.getText().toString().trim();

        if (home.isEmpty() || away.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đội nhà và đội khách", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ Validate thêm cho ngày/giờ (để không bị rỗng)
        if (date.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn ngày", Toast.LENGTH_SHORT).show();
            return;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn giờ", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Integer> prices = new HashMap<>();
        prices.put("vip", parsePrice(edtVip));
        prices.put("A", parsePrice(edtA));
        prices.put("B", parsePrice(edtB));
        prices.put("C", parsePrice(edtC));
        prices.put("D", parsePrice(edtD));

        Match match = new Match(
                home,
                away,
                date,
                time,
                edtStadium.getText().toString().trim(),
                prices
        );

        if (matchId == null || matchId.isEmpty()) {
            FirebaseDatabase.getInstance().getReference("matches")
                    .push()
                    .setValue(match);
        } else {
            FirebaseDatabase.getInstance().getReference("matches")
                    .child(matchId)
                    .setValue(match);
        }

        Toast.makeText(this, "Đã lưu", Toast.LENGTH_SHORT).show();
        finish();
    }

    private int parsePrice(EditText edt) {
        String s = edt.getText().toString().replace(".", "").trim();
        if (s.isEmpty()) return 0;
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            return 0;
        }
    }
}
