package com.mitravisual.absensisiswasdnegeri105270.Login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitravisual.absensisiswasdnegeri105270.Admin.AdminActivity;
import com.mitravisual.absensisiswasdnegeri105270.Guru.GuruActivity;
import com.mitravisual.absensisiswasdnegeri105270.R;
import com.mitravisual.absensisiswasdnegeri105270.preferences;

public class Login extends AppCompatActivity {

    //shared pref
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_NAME = "name";

    private EditText etUser, etPassword;
    private Button btnLogin;
    private TextView tvRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUser = findViewById(R.id.etUser);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        //sharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Register.newInstance().show(getSupportFragmentManager(), Register.TAG);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String username = etUser.getText().toString();
                        String password = etPassword.getText().toString();

                        if (!(username.isEmpty() && password.isEmpty())){

                            if (dataSnapshot.child(username).exists()) {

                                if (dataSnapshot.child(username).child("password").getValue(String.class).equals(password)) {

                                    if (dataSnapshot.child(username).child("as").getValue(String.class).equals("guru") && dataSnapshot.child(username).child("terdaftar").getValue(String.class).equals("Terdaftar")) {
                                        preferences.setDataLogin(Login.this, true);
                                        preferences.setDataAs(Login.this, "guru");

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(KEY_NAME,username);
                                        editor.apply();

                                        Intent masuk = new Intent(Login.this, GuruActivity.class);
                                        startActivity(masuk);
                                    } else if (dataSnapshot.child(username).child("as").getValue(String.class).equals("admin")){
                                        preferences.setDataLogin(Login.this, true);
                                        preferences.setDataAs(Login.this, "admin");

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString(KEY_NAME,username);
                                        editor.apply();

                                        Intent masuk = new Intent(Login.this, AdminActivity.class);
                                        startActivity(masuk);
                                    } else{
                                        Toast.makeText(Login.this, "Gagal Masuk", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(Login.this, "Kata sandi salah", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(Login.this, "Data belum terdaftar", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            Toast.makeText(Login.this, "Username Atau Password Salah", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (preferences.getDataLogin(this)) {
            if (preferences.getDataAs(this).equals("guru")) {
                startActivity(new Intent(this, GuruActivity.class));
                finish();
            }else{
                startActivity(new Intent(this, AdminActivity.class));
                finish();
            }
        }
    }
}