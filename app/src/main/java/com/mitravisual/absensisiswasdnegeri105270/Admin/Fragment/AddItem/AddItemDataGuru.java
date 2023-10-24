package com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment.AddItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitravisual.absensisiswasdnegeri105270.Admin.AdminActivity;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataGuru;
import com.mitravisual.absensisiswasdnegeri105270.R;

public class AddItemDataGuru extends AppCompatActivity {

    private TextView tvBack;
    private ImageView ivBack;
    private EditText etNIP, etNama, etGuruKelas;
    private Button btnSave;

    private String terdaftar = "Belum Terdaftar";

    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_data_guru);

        ivBack = findViewById(R.id.ivBack);
        tvBack = findViewById(R.id.tvBack);

        etNIP = findViewById(R.id.etNIP);
        etNama = findViewById(R.id.etNama);
        etGuruKelas = findViewById(R.id.etKelas);
        btnSave = findViewById(R.id.btnSave);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(back);
            }
        });

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(back);
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String NIP = etNIP.getText().toString();
                String Nama = etNama.getText().toString();
                String GuruKelas = etGuruKelas.getText().toString();

                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("as").setValue("guru");
                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("terdaftar").setValue(terdaftar);
                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("nip").setValue(NIP);
                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("nama").setValue(Nama);
                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("password").setValue("Belum Daftar Ulang");
                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("email").setValue("Belum Daftar Ulang");
                databaseReference.child("196306261988031020").child("Data-Guru").child(NIP).child("guruKelas").setValue(GuruKelas);

                Toast.makeText(getApplicationContext(), "Data Berhasil Di Simpan", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                startActivity(intent);
            }
        });

    }
}