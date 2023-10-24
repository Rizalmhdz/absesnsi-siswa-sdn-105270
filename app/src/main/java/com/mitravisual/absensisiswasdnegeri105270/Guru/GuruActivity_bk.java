package com.mitravisual.absensisiswasdnegeri105270.Guru;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.mitravisual.absensisiswasdnegeri105270.Guru.Adapter.ItemAdapterDataAbsensiSiswa;
import com.mitravisual.absensisiswasdnegeri105270.Guru.Entity.DataQr;
import com.mitravisual.absensisiswasdnegeri105270.Login.Login;
import com.mitravisual.absensisiswasdnegeri105270.R;
import com.mitravisual.absensisiswasdnegeri105270.preferences;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

public class GuruActivity_bk extends AppCompatActivity {


    //shared pref
    SharedPreferences sharedPreferences;
    public static final String SHARED_PREF_NAME = "myPref";
    private static final String KEY_NAME = "name";

    //adapter
    private ItemAdapterDataAbsensiSiswa adapter;
    private RecyclerView recyclerView;
    private ArrayList<DataQr> arrayList;

    private FloatingActionButton fabAdd;

    private DatabaseReference databaseReference;

    private String Nisn, Nama, Kelas, user;
    private String namaGuru, guruKelas;
    private IntentResult result;
    AutoIncrement autoIncrement = new AutoIncrement();
    private ArrayList<String> arrNisn;
    private ArrayList<String> arrNama;
    private ArrayList<String> arrKelas;
    private TextView tvNama,tvJabatan, tvLogout;
    private ImageView ivLogout;
    private Button btnCetak;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guru);

        ivLogout = findViewById(R.id.ivLogout);
        tvNama = findViewById(R.id.tvNama);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GuruActivity_bk.this, Login.class));
                preferences.clearData(GuruActivity_bk.this);
                finish();
            }
        });

        tvNama = findViewById(R.id.tvNama);
        tvJabatan = findViewById(R.id.tvJabatan);
        tvLogout = findViewById(R.id.tvLogout);
        ivLogout = findViewById(R.id.ivLogout);
        fabAdd = findViewById(R.id.btnAdd);
        btnCetak = findViewById(R.id.btnCetak);

        //sharedPreferences
        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME,MODE_PRIVATE);
        user = sharedPreferences.getString(KEY_NAME,null);

        //initial reyclerview
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        arrNisn = new ArrayList<>();
        arrNama = new ArrayList<>();
        arrKelas = new ArrayList<>();

        arrayList = new ArrayList<>();
        recyclerView = findViewById(R.id.rvList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(GuruActivity_bk.this);
        recyclerView.setLayoutManager(mLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tampildata();

        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.setBeepEnabled(true);
        intentIntegrator.setCameraId(0);
        intentIntegrator.setOrientationLocked(true);

        //ketika btnAdd ditekan
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentIntegrator.initiateScan();
            }
        });

        //loguout
        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuruActivity_bk.this, Login.class));
                preferences.clearData(GuruActivity_bk.this);
                finish();

            }
        });

        //ambil nama dosen
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                namaGuru = dataSnapshot.child(user).child("nama").getValue(String.class);
                guruKelas = dataSnapshot.child(user).child("guruKelas").getValue(String.class);

                tvNama.setText(namaGuru);
                tvJabatan.setText("Guru Kelas " + guruKelas);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Error terjadi saat mengambil data, tampilkan pesan error di sini
            }
        });

        //ambil data Siswa Dari Admin
        ambilNisn();
        ambilNama();
        ambilKelas();


        //cetak pdf
        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (arrayList.size() > 0) {
                    try {
                        databaseReference.child(user).child("Absensi-Siswa").setValue(null);
                        createPdfFromQrList(arrayList);
                        autoIncrement.resetCounter();
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Tidak ada data untuk dicetak", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void createPdfFromQrList(ArrayList<DataQr> allQrData) throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Absensi Siswa SD NEGERI 105270 "+namaGuru+"(Kelas "+ guruKelas+")"+".pdf");

        try {
            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            // Judul
            Paragraph judul = new Paragraph("Absensi Siswa SD NEGERI 105270")
                    .setBold().setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(judul);

            // sub Judul
            Paragraph subJudul = new Paragraph("Nama Guru : " + namaGuru +
                    "\n Kelas : " + guruKelas +
                    "\n Jumlah Kehadiran : " + autoIncrement.getTotal() +
                    "\n Jumlah Siswa : " + arrNisn.size())
                    .setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(subJudul);

            // Tabel
            float[] columnWidths = {1,1, 3, 2, 2};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));

            // Header Tabel
            table.addHeaderCell("No");
            table.addHeaderCell("NISN");
            table.addHeaderCell("Nama");
            table.addHeaderCell("Jam");
            table.addHeaderCell("Tanggal");

            Comparator<DataQr> comparator = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                comparator = Comparator.comparingInt(DataQr::getNo);
            }
            Collections.sort(allQrData, comparator);

            // Data Tabel
            for (DataQr qr : allQrData) {
                table.addCell(String.valueOf(qr.getNo()));
                table.addCell(qr.getCode());
                table.addCell(qr.getNama());
                table.addCell(String.valueOf(qr.getJam()));
                table.addCell(String.valueOf(qr.getTanggal()));
            }

            document.add(table);
            document.close();

            Toast.makeText(this, "PDF berhasil dibuat: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuat PDF: File tidak ditemukan", Toast.LENGTH_SHORT).show();
        }


    }

    private void ambilKelas() {
        databaseReference.child("196306261988031020").child("Data-Siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrKelas.clear();

                for (DataSnapshot item : snapshot.getChildren()){
                    arrKelas.add(item.child("kelas").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ambilNama() {
        databaseReference.child("196306261988031020").child("Data-Siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrNama.clear();

                for (DataSnapshot item : snapshot.getChildren()){
                    arrNama.add(item.child("nama").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void ambilNisn() {
        databaseReference.child("196306261988031020").child("Data-Siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrNisn.clear();

                for (DataSnapshot item : snapshot.getChildren()){
                    arrNisn.add(item.child("nisn").getValue(String.class));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void tampildata() {
        databaseReference.child(user).child("Absensi-Siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                arrayList.clear();

                for (DataSnapshot item : snapshot.getChildren()){

                    DataQr isi = new DataQr();
                    isi.setNo(item.child("no").getValue(int.class));
                    isi.setCode(item.child("nisn").getValue(String.class));
                    isi.setNama(item.child("nama").getValue(String.class));
                    isi.setKelas(item.child("kelas").getValue(String.class));
                    isi.setTanggal(item.child("tanggal").getValue(String.class));
                    isi.setJam(item.child("jam").getValue(String.class));
                    arrayList.add(isi);

                }

                adapter = new ItemAdapterDataAbsensiSiswa(arrayList, getApplicationContext());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        result = IntentIntegrator.parseActivityResult(requestCode, resultCode,data);

        if (result != null){

            boolean isFound = false;
            for (int i = 0; i < arrNisn.size(); i++) {
                if (result.getContents().equals(arrNisn.get(i))) {
                    Nisn = arrNisn.get(i);
                    Nama = arrNama.get(i);
                    Kelas = arrKelas.get(i);
                    isFound = true;
                    break;
                }
            }

            if (!isFound) {
                Toast.makeText(getApplicationContext(), "Data Siswa Tidak Terdaftar Di Database", Toast.LENGTH_SHORT).show();
            }

            if (result.getContents() == null){
                Toast.makeText(getApplicationContext(), "Hasil Tidak Di Temukan", Toast.LENGTH_SHORT).show();
            } else if (result.getContents().equals(Nisn)){

                Calendar calendar = Calendar.getInstance();

                //tanggal hari ini
                SimpleDateFormat tanggal = new SimpleDateFormat("EEEE, dd MMM yyyy");
                String Tanggal = tanggal.format(calendar.getTime());

                //waktu hari ini
                SimpleDateFormat waktu = new SimpleDateFormat("HH:mm:ss");
                String Waktu = waktu.format(calendar.getTime());

                databaseReference.child(user).child("Absensi-Siswa").child(Nisn).child("no").setValue(autoIncrement.getNextNo());
                databaseReference.child(user).child("Absensi-Siswa").child(Nisn).child("nisn").setValue(Nisn);
                databaseReference.child(user).child("Absensi-Siswa").child(Nisn).child("nama").setValue(Nama);
                databaseReference.child(user).child("Absensi-Siswa").child(Nisn).child("kelas").setValue(Kelas);
                databaseReference.child(user).child("Absensi-Siswa").child(Nisn).child("tanggal").setValue(Tanggal);
                databaseReference.child(user).child("Absensi-Siswa").child(Nisn).child("jam").setValue(Waktu);
            }else{
                Toast.makeText(getApplicationContext(), "Data Siswa Tidak Ditemukan Di Database", Toast.LENGTH_SHORT).show();
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, requestCode);
        }
    }
}