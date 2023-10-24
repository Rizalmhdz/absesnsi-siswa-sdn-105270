package com.mitravisual.absensisiswasdnegeri105270.Admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitravisual.absensisiswasdnegeri105270.Adapter.MyPagerAdapter;
import com.mitravisual.absensisiswasdnegeri105270.Login.Login;
import com.mitravisual.absensisiswasdnegeri105270.R;
import com.mitravisual.absensisiswasdnegeri105270.preferences;

public class AdminActivity extends AppCompatActivity {

    //shared pref
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_NAME = "name";

    private String user;
    private ImageView ivLogout;
    private TextView tvNama;

    TabLayout tabLayout;
    ViewPager viewPager;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        ivLogout = findViewById(R.id.ivLogout);
        tvNama = findViewById(R.id.tvNama);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AdminActivity.this, Login.class));
                preferences.clearData(AdminActivity.this);
                finish();
            }
        });

        //sharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        user = sharedPreferences.getString(KEY_NAME,null);

        // Inisialisasi TabLayout
        tabLayout = findViewById(R.id.tabLayout);

        // Inisialisasi ViewPager
        viewPager = findViewById(R.id.viewPager);

        // Buat adapter untuk ViewPager
        MyPagerAdapter adapter = new MyPagerAdapter(getSupportFragmentManager());

        // Set adapter untuk ViewPager
        viewPager.setAdapter(adapter);

        // Hubungkan TabLayout dengan ViewPager
        tabLayout.setupWithViewPager(viewPager);

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child(user).child("nama").getValue(String.class);

                tvNama.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error terjadi saat mengambil data, tampilkan pesan error di sini
            }
        });

    }
}