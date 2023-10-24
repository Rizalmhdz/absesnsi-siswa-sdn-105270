package com.mitravisual.absensisiswasdnegeri105270.Guru;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.mitravisual.absensisiswasdnegeri105270.Admin.Adapter.ItemAdapterDataSiswa;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataSiswa;
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
import java.util.List;

public class GuruActivity extends AppCompatActivity {

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
    private LinearLayout modalCetakPdf;
    private Button btnCetakHarian;
    private Button btnCetakBulanan;
    private Button btnCetakPersemester;
    private Button btnCancel;

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
                startActivity(new Intent(GuruActivity.this, Login.class));
                preferences.clearData(GuruActivity.this);
                finish();
            }
        });

        tvNama = findViewById(R.id.tvNama);
        tvJabatan = findViewById(R.id.tvJabatan);
        tvLogout = findViewById(R.id.tvLogout);
        ivLogout = findViewById(R.id.ivLogout);
        fabAdd = findViewById(R.id.btnAdd);
        btnCetak = findViewById(R.id.btnCetak);
        btnCetakHarian = findViewById(R.id.btnCetakHarian);

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
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(GuruActivity.this);
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
                startActivity(new Intent(GuruActivity.this, Login.class));
                preferences.clearData(GuruActivity.this);
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
                modalCetakPdf.setVisibility(View.VISIBLE);
            }
        });

        // Inisialisasi elemen modal cetak PDF
        modalCetakPdf = findViewById(R.id.modalCetakPdf);
        btnCetakHarian = findViewById(R.id.btnCetakHarian);
        btnCetakBulanan = findViewById(R.id.btnCetakBulanan);
        btnCetakPersemester = findViewById(R.id.btnCetakPersemester);
        btnCancel = findViewById(R.id.btnCancel);

        // Mengatur tindakan saat tombol "Cetak PDF Harian" diklik
        btnCetakHarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child("Absensi-Siswa").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataQr> qrDataList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            DataQr qrData = item.getValue(DataQr.class);
                            qrDataList.add(qrData);
                        }

                        try {
                            // Menghapus data di Firebase
                            databaseReference.child("users").child("Absensi-Siswa").setValue(null);
                            ArrayList<DataQr> filteredList = filterDataByHarian(qrDataList);
                            createPdfFromQrList(filteredList);
                            autoIncrement.resetCounter();
                            modalCetakPdf.setVisibility(View.GONE);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error terjadi saat mengambil data dari Firebase
                    }
                });
            }
        });

        // Mengatur tindakan saat tombol "Cetak PDF Bulanan" diklik
        btnCetakBulanan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child("Absensi-Siswa").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataQr> qrDataList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            DataQr qrData = item.getValue(DataQr.class);
                            qrDataList.add(qrData);
                        }

                        try {
                            // Menghapus data di Firebase
                            databaseReference.child("users").child("Absensi-Siswa").setValue(null);
                            ArrayList<DataQr> filteredList = filterDataByBulanan(qrDataList);
                            createPdfFromQrList(filteredList);
                            autoIncrement.resetCounter();
                            modalCetakPdf.setVisibility(View.GONE);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error terjadi saat mengambil data dari Firebase
                    }
                });
            }
        });

        // Mengatur tindakan saat tombol "Cetak PDF Per Semester" diklik
        btnCetakPersemester.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.child("users").child("Absensi-Siswa").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataQr> qrDataList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            DataQr qrData = item.getValue(DataQr.class);
                            qrDataList.add(qrData);
                        }

                        try {
                            // Menghapus data di Firebase
                            databaseReference.child("users").child("Absensi-Siswa").setValue(null);
                            ArrayList<DataQr> filteredList = filterDataByPersemester(qrDataList);
                            createPdfFromQrList(filteredList);
                            autoIncrement.resetCounter();
                            modalCetakPdf.setVisibility(View.GONE);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Error terjadi saat mengambil data dari Firebase
                    }
                });
            }
        });

        // Mengatur tindakan saat tombol "Batal" diklik
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modalCetakPdf.setVisibility(View.GONE);
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

            Comparator<DataQr> comparator = new Comparator<DataQr>() {
                @Override
                public int compare(DataQr qr1, DataQr qr2) {
                    return Integer.compare(qr1.getNo(), qr2.getNo());
                }
            };
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

    private ArrayList<DataQr> filterDataByHarian(ArrayList<DataQr> qrDataList) {
        ArrayList<DataQr> filteredList = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");

        for (DataQr qrData : qrDataList) {
            Calendar qrDate = Calendar.getInstance();
            try {
                qrDate.setTime(dateFormat.parse(qrData.getTanggal()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (qrDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    qrDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                    qrDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH)) {
                filteredList.add(qrData);
            }
        }

        return filteredList;
    }

    private ArrayList<DataQr> filterDataByBulanan(ArrayList<DataQr> qrDataList) {
        ArrayList<DataQr> filteredList = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");

        for (DataQr qrData : qrDataList) {
            Calendar qrDate = Calendar.getInstance();
            try {
                qrDate.setTime(dateFormat.parse(qrData.getTanggal()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (qrDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                    qrDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)) {
                filteredList.add(qrData);
            }
        }

        return filteredList;
    }

    private ArrayList<DataQr> filterDataByPersemester(ArrayList<DataQr> qrDataList) {
        ArrayList<DataQr> filteredList = new ArrayList<>();
        Calendar today = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd MMM yyyy");

        for (DataQr qrData : qrDataList) {
            Calendar qrDate = Calendar.getInstance();
            try {
                qrDate.setTime(dateFormat.parse(qrData.getTanggal()));
            } catch (Exception e) {
                e.printStackTrace();
            }

            int qrMonth = qrDate.get(Calendar.MONTH);
            int currentMonth = today.get(Calendar.MONTH);
            if ((qrMonth >= Calendar.JANUARY && qrMonth <= Calendar.JUNE) && (currentMonth >= Calendar.JANUARY && currentMonth <= Calendar.JUNE)) {
                // Semester 1 (Januari - Juni)
                filteredList.add(qrData);
            } else if ((qrMonth >= Calendar.JULY && qrMonth <= Calendar.DECEMBER) && (currentMonth >= Calendar.JULY && currentMonth <= Calendar.DECEMBER)) {
                // Semester 2 (Juli - Desember)
                filteredList.add(qrData);
            }
        }

        return filteredList;
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
        databaseReference.child("users").child("Absensi-Siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList.clear();

                for (DataSnapshot item : snapshot.getChildren()){
                    DataQr isi = new DataQr();
                    isi.setNo(item.child("no").getValue(Integer.class));
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
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
                String tanggal = simpleDateFormat.format(calendar.getTime());

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                String jam = sdf.format(calendar.getTime());

                int no = autoIncrement.getCounter();
                autoIncrement.increment();

                DataQr qrData = new DataQr(no, Nisn, Nama, Kelas, tanggal, jam);

                databaseReference.child("users").child("Absensi-Siswa").child(String.valueOf(no)).setValue(qrData);

                Toast.makeText(getApplicationContext(), "Data Absensi Tersimpan", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Gagal", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
