package com.mitravisual.absensisiswasdnegeri105270.Guru;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.mitravisual.absensisiswasdnegeri105270.Guru.Adapter.ItemAdapterDataAbsensiSiswa;
import com.mitravisual.absensisiswasdnegeri105270.Guru.Entity.DataQr;
import com.mitravisual.absensisiswasdnegeri105270.Guru.Entity.DataQrRekap;
import com.mitravisual.absensisiswasdnegeri105270.Login.Login;
import com.mitravisual.absensisiswasdnegeri105270.R;
import com.mitravisual.absensisiswasdnegeri105270.preferences;

import java.text.ParseException;
import java.text.SimpleDateFormat;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

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

    private String Nisn, Nama, Kelas, user, keterangan;
    private String namaGuru, guruKelas, tipeAbsen, nipGuru;
    private int totalHadir, isGenap;

    private String tanggal, bulan, bulanInt, tahun, hariTanggal, semester;
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
                nipGuru = dataSnapshot.child(user).child("nip").getValue(String.class);
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

        tanggal = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        bulan = new SimpleDateFormat("MMMM", new Locale("id")).format(new Date());
        bulanInt = new SimpleDateFormat("MM", new Locale("id")).format(new Date());
        tahun = new SimpleDateFormat("yyyy").format(new Date());
        hariTanggal = new SimpleDateFormat("EEEE, dd-MMM-yyyy", new Locale("id")).format(new Date());

        // Mengatur tindakan saat tombol "Cetak PDF Harian" diklik
        btnCetakHarian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipeAbsen = "harian";
                totalHadir = 0;

                databaseReference.child(user).child("Absensi-Siswa").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataQr> qrDataList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            // Melakukan iterasi lebih lanjut pada item di dalam blok yang sama.
                            if(item.hasChildren()){
                                for (DataSnapshot nestedItem : item.getChildren()) {
                                    // Lakukan sesuatu dengan nestedItem di sini.
                                    String nestedItemKey = nestedItem.getKey();
                                    // Memeriksa apakah kunci nestedItem sesuai dengan tanggal hari ini
                                    if (nestedItemKey.equals(tanggal)) {
                                        // Jika sesuai, tambahkan nestedItem ke daftar yang difilter
                                        try {
                                            totalHadir = nestedItem.child("keterangan").getValue().toString().equalsIgnoreCase("hadir")? totalHadir + 1 : totalHadir;
                                        } catch (Exception e){
                                            Log.d("LogTAG", String.valueOf(e.getMessage()));
                                        }

                                        Log.d("LogTAG", String.valueOf(totalHadir));
                                        DataQr qrData = nestedItem.getValue(DataQr.class);
                                        qrDataList.add(qrData);
                                    }
                                }
                            }
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

                tipeAbsen = "bulanan";
                databaseReference.child(user).child("Absensi-Siswa").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataQrRekap> qrDataList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            DataQrRekap dataQrRekap = new DataQrRekap();

                            // data sementara rekap sakit, izin, dan alpha perminggu 1 2 3 4
                            ArrayList<Integer> sakit = new ArrayList<>();
                            sakit.addAll(Arrays.asList(0, 0, 0, 0));
                            ArrayList<Integer> izin = new ArrayList<>();
                            izin.addAll(Arrays.asList(0, 0, 0, 0));
                            ArrayList<Integer> alpha = new ArrayList<>();
                            alpha.addAll(Arrays.asList(0, 0, 0, 0));

                            // Melakukan iterasi lebih lanjut pada item di dalam blok yang sama.
                            if(item.hasChildren()) {
                                for (DataSnapshot nestedItem : item.getChildren()) {
                                    dataQrRekap.setNama(nestedItem.child("nama").getValue(String.class));
                                    dataQrRekap.setNo(nestedItem.child("no").getValue(Integer.class));

                                    int yearData = 0;
                                    int monthData = 0;
                                    int dayOfMonth = 0;
                                    try {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        Date date = sdf.parse(nestedItem.getKey());

                                        // Mengambil bulan, tahun, dan tanggal sebagai integer
                                        yearData = date.getYear() + 1900;
                                        monthData = date.getMonth() + 1;
                                        dayOfMonth = date.getDate();
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (yearData == Integer.parseInt(tahun) && monthData == Integer.parseInt(bulanInt)) {
                                        if(dayOfMonth >= 1 && dayOfMonth <= 7) {
                                            if(nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")){
                                                sakit.set(0, sakit.get(0) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                izin.set(0, izin.get(0) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                alpha.set(0, alpha.get(0) + 1);
                                            }
                                        }
                                        else if (dayOfMonth >= 8 && dayOfMonth <= 14) {
                                            if(nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")){
                                                sakit.set(1, sakit.get(1) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                izin.set(1, izin.get(1) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                alpha.set(1, alpha.get(1) + 1);
                                            }
                                        }
                                        else if (dayOfMonth >= 15 && dayOfMonth <= 21) {
                                            if(nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")){
                                                sakit.set(2, sakit.get(2) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                izin.set(2, izin.get(2) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                alpha.set(2, alpha.get(2) + 1);
                                            }
                                        }
                                        else if (dayOfMonth >= 22) {

                                            if(nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")){
                                                sakit.set(3, sakit.get(3) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                izin.set(3, izin.get(3) + 1);
                                            } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                alpha.set(3, alpha.get(3) + 1);
                                            }
                                        }
                                    }
                                }
                            }

                            dataQrRekap.setSakit(sakit);
                            dataQrRekap.setIzin(izin);
                            dataQrRekap.setAlpha(alpha);
                            qrDataList.add(dataQrRekap);

                        }
                        try {
                            // Menghapus data di Firebase
                            databaseReference.child("users").child("Absensi-Siswa").setValue(null);
                            createPdfBulanan(qrDataList);
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

                tipeAbsen = "semester";


                Date currentDate = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(currentDate);
                int month = calendar.get(Calendar.MONTH) + 1;
                semester = (month > 6) ? "Ganjil" : "Genap";
                isGenap = (semester.equalsIgnoreCase("ganjil"))? 6 : 0;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                databaseReference.child(user).child("Absensi-Siswa").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        ArrayList<DataQrRekap> qrDataList = new ArrayList<>();
                        for (DataSnapshot item : snapshot.getChildren()) {
                            DataQrRekap dataQrRekap = new DataQrRekap();

                            // data sementara rekap sakit, izin, dan alpha per bulan
                            ArrayList<Integer> sakit = new ArrayList<>();
                            sakit.addAll(Arrays.asList(0, 0, 0, 0, 0, 0));
                            ArrayList<Integer> izin = new ArrayList<>();
                            izin.addAll(Arrays.asList(0, 0, 0, 0, 0, 0));
                            ArrayList<Integer> alpha = new ArrayList<>();
                            alpha.addAll(Arrays.asList(0, 0, 0, 0, 0, 0));

                            // Melakukan iterasi lebih lanjut pada key Tanggal dari NISN siswa
                            if(item.hasChildren()) {
                                for (DataSnapshot nestedItem : item.getChildren()) {
                                    dataQrRekap.setNama(nestedItem.child("nama").getValue(String.class));
                                    dataQrRekap.setNo(nestedItem.child("no").getValue(Integer.class));

                                    int yearData = 0;
                                    int monthData = 0;
                                    try {
                                        Date date = sdf.parse(nestedItem.getKey());

                                        // Mengambil bulan, tahun, dan tanggal sebagai integer
                                        yearData = date.getYear() + 1900;
                                        monthData = date.getMonth() + 1;
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (yearData == Integer.parseInt(tahun)) {
                                        try {
                                            if (monthData == 1 + isGenap) {
                                                if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")) {
                                                    sakit.set(0, sakit.get(0) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                    izin.set(0, izin.get(0) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                    alpha.set(0, alpha.get(0) + 1);
                                                }
                                            } else if (monthData == 2 + isGenap) {
                                                if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")) {
                                                    sakit.set(1, sakit.get(1) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                    izin.set(1, izin.get(1) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                    alpha.set(1, alpha.get(1) + 1);
                                                }
                                            } else if (monthData == 3 + isGenap) {
                                                if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")) {
                                                    sakit.set(2, sakit.get(2) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                    izin.set(2, izin.get(2) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                    alpha.set(2, alpha.get(2) + 1);
                                                }
                                            } else if (monthData == 4 + isGenap) {

                                                if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")) {
                                                    sakit.set(3, sakit.get(3) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                    izin.set(3, izin.get(3) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                    alpha.set(3, alpha.get(3) + 1);
                                                }
                                            } else if (monthData == 5 + isGenap) {

                                                if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")) {
                                                    sakit.set(4, sakit.get(4) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                    izin.set(4, izin.get(4) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                    alpha.set(4, alpha.get(4) + 1);
                                                }
                                            } else if (monthData == 6 + isGenap) {
                                                if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("sakit")) {
                                                    sakit.set(5, sakit.get(5) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("izin")) {
                                                    izin.set(5, izin.get(5) + 1);
                                                } else if (nestedItem.child("keterangan").getValue(String.class).equalsIgnoreCase("alpha")) {
                                                    alpha.set(5, alpha.get(5) + 1);
                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            dataQrRekap.setSakit(sakit);
                            dataQrRekap.setIzin(izin);
                            dataQrRekap.setAlpha(alpha);
                            qrDataList.add(dataQrRekap);

                        }
                        try {
                            // Menghapus data di Firebase
                            databaseReference.child("users").child("Absensi-Siswa").setValue(null);
                            createPdfSemester(qrDataList);
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
        File file = new File(pdfPath, "Absensi "+ tipeAbsen + " Siswa SD NEGERI 105270 "+namaGuru+"(Kelas "+ guruKelas+")"+".pdf");

        try {
            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            // Judul
            Paragraph judul = new Paragraph("Absensi Siswa SD NEGERI 105270")
                    .setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(judul);

            // sub Judul
            Paragraph subJudul = new Paragraph("Nama Guru \t \t \t : " + namaGuru +
                    "\n Kelas \t \t \t \t \t : " + guruKelas +
//                    "\n Jumlah Kehadiran \t : " + autoIncrement.getTotal() +
                    "\n Jumlah Kehadiran \t : " + totalHadir +
                    "\n Jumlah Siswa \t\t\t: " + arrNisn.size() + "\n")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(subJudul);

            // Tabel
            float[] columnWidths = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));
            table.setFontSize(10);
            table.setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.setTextAlignment(TextAlignment.CENTER);

            // Header Tabel
            table.addHeaderCell(new Cell(1, 1).add(new Paragraph("No")));
            table.addHeaderCell(new Cell(1, 3).add(new Paragraph("NISN")));
            table.addHeaderCell(new Cell(1, 4).add(new Paragraph("Nama")));
            table.addHeaderCell(new Cell(1, 2).add(new Paragraph("Jam")));
            table.addHeaderCell(new Cell(1, 3).add(new Paragraph("Tanggal")));
            table.addHeaderCell(new Cell(1, 2).add(new Paragraph("Keterangan")));

            Comparator<DataQr> comparator = new Comparator<DataQr>() {
                @Override
                public int compare(DataQr qr1, DataQr qr2) {
                    return Integer.compare(qr1.getNo(), qr2.getNo());
                }
            };
            Collections.sort(allQrData, comparator);

            // Data Tabel
            for (DataQr qr : allQrData) {
                table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(qr.getNo()))));
                table.addCell(new Cell(1, 3).add(new Paragraph(qr.getCode())));
                table.addCell(new Cell(1, 4).add(new Paragraph(qr.getNama())));
                table.addCell(new Cell(1, 2).add(new Paragraph(String.valueOf(qr.getJam()))));
                table.addCell(new Cell(1, 3).add(new Paragraph(String.valueOf(qr.getTanggal()))));
                table.addCell(new Cell(1, 2).add(new Paragraph(String.valueOf(qr.getKeterangan()))));
                table.startNewRow();
            }

            table.addCell(new Cell(1, 11).setBorder(Border.NO_BORDER));

            table.addCell(new Cell(1, 4)
                    .add(new Paragraph(String.format("\n\nMengetahui\nWali Kelas\n\n\n\n\n %s \nNIP. %s", namaGuru, nipGuru)))
                    .setBorder(Border.NO_BORDER));
            table.startNewRow();

            document.add(table);
            document.close();

            Toast.makeText(this, "PDF berhasil dibuat: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuat PDF: File tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdfBulanan(ArrayList<DataQrRekap> allQrData) throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Absensi "+ tipeAbsen + " Siswa SD NEGERI 105270 "+namaGuru+"(Kelas "+ guruKelas+")"+".pdf");

        try {
            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            // Judul
            Paragraph judul = new Paragraph("Absensi Siswa Kelas " + guruKelas + " " + bulan + " "+ tahun + " SD NEGERI 105270")
                    .setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(judul);

            // sub Judul
            Paragraph subJudul = new Paragraph("Nama Guru \t \t \t : " + namaGuru +
                    "\n Kelas \t \t \t \t \t : " + guruKelas +
//                    "\n Jumlah Kehadiran \t : " + autoIncrement.getTotal() +
                    "\n Jumlah Siswa \t\t\t: " + arrNisn.size() +
                    "\n Tanggal Cetak\t\t\t: " + hariTanggal + "\n")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(subJudul);

            float[] columnWidths = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));
            table.setFontSize(10);
            table.setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.setTextAlignment(TextAlignment.CENTER);

            // Baris 1
            Cell cell11 = new Cell(3, 1).add(new Paragraph("\nNo"));
            Cell cell12 = new Cell(3, 3).add(new Paragraph("\nNama Siswa"));
            Cell cell13 = new Cell(1, 12).add(new Paragraph("Bulan " + bulan));
            Cell cell14 = new Cell(2, 3).add(new Paragraph("\nTotal"));

            // Tambahkan sel-sel Baris 2 ke dalam tabel
            table.addCell(cell11);
            table.addCell(cell12);
            table.addCell(cell13);
            table.addCell(cell14);

            table.startNewRow();

            // Baris 2
            Cell cell21 = new Cell(1, 3).add(new Paragraph("Week 1"));
            Cell cell22 = new Cell(1, 3).add(new Paragraph("Week 2"));
            Cell cell23 = new Cell(1, 3).add(new Paragraph("Week 3"));
            Cell cell24 = new Cell(1, 3).add(new Paragraph("Week 4"));

            // Tambahkan sel-sel Baris 2 ke dalam tabel
            table.addCell(cell21);
            table.addCell(cell22);
            table.addCell(cell23);
            table.addCell(cell24);

            table.startNewRow();

            for (int i = 0; i < 5; i++) {
                Cell cell31 = new Cell(1, 1).add(new Paragraph("S"));
                Cell cell32 = new Cell(1, 1).add(new Paragraph("I"));
                Cell cell33 = new Cell(1, 1).add(new Paragraph("A"));
                table.addCell(cell31);
                table.addCell(cell32);
                table.addCell(cell33);
            }
            table.startNewRow();

            Comparator<DataQrRekap> comparator = new Comparator<DataQrRekap>() {
                @Override
                public int compare(DataQrRekap qr1, DataQrRekap qr2) {
                    return Integer.compare(qr1.getNo(), qr2.getNo());
                }
            };
            Collections.sort(allQrData, comparator);

            // Data Tabel
            for (DataQrRekap qr : allQrData) {
                table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(qr.getNo()))));
                table.addCell(new Cell(1, 3).add(new Paragraph(String.valueOf(qr.getNama()))));

                int sakit = 0, izin = 0, alpha = 0;
                for (int i = 0; i < 4; i++) {
                    table.addCell(String.valueOf(qr.getSakit().get(i)));
                    table.addCell(String.valueOf(qr.getIzin().get(i)));
                    table.addCell(String.valueOf(qr.getAlpha().get(i)));

                    sakit = sakit + qr.getSakit().get(i);
                    izin = izin + qr.getIzin().get(i);
                    alpha = alpha + qr.getAlpha().get(i);
                }
                table.addCell(String.valueOf(sakit));
                table.addCell(String.valueOf(izin));
                table.addCell(String.valueOf(alpha));
                table.startNewRow();
            }

            table.addCell(new Cell(1, 12).setBorder(Border.NO_BORDER));

            table.addCell(new Cell(1, 8)
                    .add(new Paragraph(String.format("\n\nMengetahui\nWali Kelas\n\n\n\n\n %s \nNIP. %s", namaGuru, nipGuru)))
                    .setBorder(Border.NO_BORDER));
            table.startNewRow();

            document.add(table);
            document.close();

            Toast.makeText(this, "PDF berhasil dibuat: " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal membuat PDF: File tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }

    private void createPdfSemester(ArrayList<DataQrRekap> allQrData) throws FileNotFoundException {
        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "Absensi "+ tipeAbsen + " Siswa SD NEGERI 105270 "+namaGuru+"(Kelas "+ guruKelas+")"+".pdf");

        try {
            OutputStream outputStream = new FileOutputStream(file);
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            pdfDocument.setDefaultPageSize(PageSize.A4);

            // Judul
            Paragraph judul = new Paragraph("Absensi Siswa Kelas " + guruKelas + " Semester " + semester + " "+ tahun + " SD NEGERI 105270")
                    .setBold().setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(judul);

            // sub Judul
            Paragraph subJudul = new Paragraph("Nama Guru \t \t \t : " + namaGuru +
                    "\n Kelas \t \t \t \t \t : " + guruKelas +
                    "\n Jumlah Siswa \t\t\t: " + arrNisn.size() +
                    "\n Tanggal Cetak\t\t\t: " + hariTanggal + "\n")
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.LEFT);
            document.add(subJudul);

            float[] columnWidths = {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
            Table table = new Table(columnWidths);
            table.setWidth(UnitValue.createPercentValue(100));
            table.setFontSize(10);
            table.setVerticalAlignment(VerticalAlignment.MIDDLE);
            table.setTextAlignment(TextAlignment.CENTER);

            // Baris 1
            Cell cell11 = new Cell(3, 1).add(new Paragraph("\nNo"));
            Cell cell12 = new Cell(3, 5).add(new Paragraph("\nNama Siswa"));
            Cell cell13 = new Cell(1, 18).add(new Paragraph("Semester " + semester + " Tahun " + tahun));
            Cell cell14 = new Cell(2, 3).add(new Paragraph("\nTotal"));

            // Tambahkan sel-sel Baris 2 ke dalam tabel
            table.addCell(cell11);
            table.addCell(cell12);
            table.addCell(cell13);
            table.addCell(cell14);
            table.startNewRow();

            Calendar calendar = Calendar.getInstance();

            // Membuat objek SimpleDateFormat untuk mendapatkan nama bulan
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM", new Locale("id"));
            for (int i = 0; i <= 5; i++) {
                calendar.set(Calendar.MONTH, i+isGenap);

                // Mengambil nama bulan dalam format string
                String namaBulan = dateFormat.format(calendar.getTime());
                table.addCell(new Cell(1, 3).add(new Paragraph(namaBulan)));
            }

            table.startNewRow();

            for (int i = 0; i < 7; i++) {
                table.addCell(new Cell(1, 1).add(new Paragraph("S")));
                table.addCell(new Cell(1, 1).add(new Paragraph("I")));
                table.addCell(new Cell(1, 1).add(new Paragraph("A")));
            }
            table.startNewRow();

            Comparator<DataQrRekap> comparator = new Comparator<DataQrRekap>() {
                @Override
                public int compare(DataQrRekap qr1, DataQrRekap qr2) {
                    return Integer.compare(qr1.getNo(), qr2.getNo());
                }
            };
            Collections.sort(allQrData, comparator);

            // Data Tabel
            for (DataQrRekap qr : allQrData) {
                table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(qr.getNo()))));
                table.addCell(new Cell(1, 5).add(new Paragraph(String.valueOf(qr.getNama()))));

                int sakit = 0, izin = 0, alpha = 0;
                for (int i = 0; i < 6; i++) {
                    table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(qr.getSakit().get(i)))));
                    table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(qr.getIzin().get(i)))));
                    table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(qr.getAlpha().get(i)))));

                    sakit = sakit + qr.getSakit().get(i);
                    izin = izin + qr.getIzin().get(i);
                    alpha = alpha + qr.getAlpha().get(i);
                }
                table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(sakit))));
                table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(izin))));
                table.addCell(new Cell(1, 1).add(new Paragraph(String.valueOf(alpha))));
                table.startNewRow();
            }

            table.addCell(new Cell(1, 19).setBorder(Border.NO_BORDER));
            table.addCell(new Cell(1, 8)
                    .add(new Paragraph(String.format("\n\nMengetahui\nWali Kelas\n\n\n\n\n %s \nNIP. %s", namaGuru, nipGuru)))
                    .setBorder(Border.NO_BORDER));
            table.startNewRow();

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

                DataQr qrData = new DataQr(no, Nisn, Nama, Kelas, tanggal, jam, keterangan);

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
