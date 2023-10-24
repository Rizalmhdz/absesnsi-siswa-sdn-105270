package com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment.AddItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mitravisual.absensisiswasdnegeri105270.Admin.AdminActivity;
import com.mitravisual.absensisiswasdnegeri105270.R;

public class AddItemDataSiswa extends AppCompatActivity {

    private TextView tvBack, tvTtd;
    private ImageView ivBack, ivTtd;
    private EditText etNISN, etNama, etKelas, etAlamat;
    private Button btnSave;

    private String NISN, Nama, Kelas, Alamat;

    private Uri uri = null;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item_data_siswa);

        ivBack = findViewById(R.id.ivBack);
        tvBack = findViewById(R.id.tvBack);

        etNISN = findViewById(R.id.etNISN);
        etNama = findViewById(R.id.etNama);
        etKelas = findViewById(R.id.etKelas);
        etAlamat = findViewById(R.id.etAlamat);
        ivTtd = findViewById(R.id.ivTtd);
        tvTtd = findViewById(R.id.tvTtd);
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

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        ivTtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pilihFile();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                NISN = etNISN.getText().toString();
                Nama = etNama.getText().toString();
                Kelas = etKelas.getText().toString();
                Alamat = etAlamat.getText().toString();

                if (!(NISN.isEmpty() && Nama.isEmpty() && Kelas.isEmpty() && Alamat.isEmpty()) && uri != null){
                    databaseReference.child("196306261988031020").child("Data-Siswa").child(NISN).child("nisn").setValue(NISN);
                    databaseReference.child("196306261988031020").child("Data-Siswa").child(NISN).child("nama").setValue(Nama);
                    databaseReference.child("196306261988031020").child("Data-Siswa").child(NISN).child("kelas").setValue(Kelas);
                    databaseReference.child("196306261988031020").child("Data-Siswa").child(NISN).child("alamat").setValue(Alamat);
                    databaseReference.child("196306261988031020").child("Data-Siswa").child(NISN).child("url").setValue(uri.toString());

                    Toast.makeText(getApplicationContext(), "Data Berhasil Di Simpan", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(intent);
                }else {
                    Toast.makeText(getApplicationContext(), "Ada Data Yang Masih Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pilihFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            startActivityForResult(Intent.createChooser(intent, "Pilih File Pdf..."), 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){
            UploadFile(data.getData());
        }
    }

    private void UploadFile(Uri data) {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference reference = storageReference.child("Upload/" + System.currentTimeMillis()+" .pdf");
        reference.putFile(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete());
                        uri = uriTask.getResult();

                        tvTtd.setText("Tanda Tangan.pdf");

                        progressDialog.dismiss();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {

                        double progress = (100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploaded: " + (int)progress+ "%");
                    }
                });
    }
}