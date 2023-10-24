package com.mitravisual.absensisiswasdnegeri105270.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitravisual.absensisiswasdnegeri105270.R;

public class Register extends BottomSheetDialogFragment {

    private EditText etNIP, etPass, etEmail;
    private Button btnDaftar;
    private String as, nipGuru, namaGuru, guruKelas;
    private String terdaftar = "Terdaftar";

    private DatabaseReference firebaseDatabase;

    @SuppressLint("MissingInflatedId")

    public static final String TAG = "Register";

    public static Register newInstance(){
        return new Register();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.register , container , false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etNIP = view.findViewById(R.id.etNIP);
        etPass = view.findViewById(R.id.etPassword);
        etEmail = view.findViewById(R.id.etEmail);
        btnDaftar = view.findViewById(R.id.btnRegister);

        firebaseDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://absensi-siswa-sdn-105270-default-rtdb.firebaseio.com/");

        btnDaftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                as = "guru";

                String NIP = etNIP.getText().toString();
                String Pass = etPass.getText().toString();
                String Email = etEmail.getText().toString();

                String emailAdmin = "elizasitorus08@gmail.com";

                String text = "Username : " + nipGuru +
                        "\nPassword : " + Pass;

                if (!(NIP.isEmpty() && Pass.isEmpty() && Email.isEmpty())){

                    DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference("users");
                    databaseRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            nipGuru = dataSnapshot.child("196306261988031020").child("Data-Guru").child(NIP).child("nip").getValue(String.class);
                            namaGuru = dataSnapshot.child("196306261988031020").child("Data-Guru").child(NIP).child("nama").getValue(String.class);
                            guruKelas = dataSnapshot.child("196306261988031020").child("Data-Guru").child(NIP).child("guruKelas").getValue(String.class);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getActivity(), "Data Gagal", Toast.LENGTH_SHORT).show();
                        }
                    });

                    if (NIP.equals(nipGuru)){
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("as").setValue(as);
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("terdaftar").setValue(terdaftar);
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("nip").setValue(NIP);
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("nama").setValue(namaGuru);
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("password").setValue(Pass);
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("email").setValue(Email);
                        firebaseDatabase.child("users").child("196306261988031020").child("Data-Guru").child(NIP).child("guruKelas").setValue(guruKelas);

                        firebaseDatabase.child("users").child(NIP).child("as").setValue(as);
                        firebaseDatabase.child("users").child(NIP).child("terdaftar").setValue(terdaftar);
                        firebaseDatabase.child("users").child(NIP).child("nip").setValue(NIP);
                        firebaseDatabase.child("users").child(NIP).child("nama").setValue(namaGuru);
                        firebaseDatabase.child("users").child(NIP).child("password").setValue(Pass);
                        firebaseDatabase.child("users").child(NIP).child("email").setValue(Email);
                        firebaseDatabase.child("users").child(NIP).child("guruKelas").setValue(guruKelas);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Kirim Data");
                        builder.setMessage("Data Registrasi Anda Akan Di Kirimkan Melalui Email Kepada Admin");
                        builder.setPositiveButton("Kirim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                String[] penerima = emailAdmin.split(",");

                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_EMAIL,penerima);
                                intent.putExtra(Intent.EXTRA_SUBJECT, "Data Register Aplikasi ABSENSI SISWA SD NEGERI 105270");
                                intent.putExtra(Intent.EXTRA_TEXT, text);

                                intent.setType("message/rfc822");
                                startActivity(Intent.createChooser(intent, "Pilih Gmail"));
                                dialog.dismiss();
                                dismiss();

                                Toast.makeText(getActivity(), "Daftar Berhasil", Toast.LENGTH_SHORT).show();

                            }
                        });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }else{
                        Toast.makeText(getActivity(), "Daftar Gagal, Silahkan Coba Lagi", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(getActivity(), "Daftar Gagal", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = getActivity();
        if (activity instanceof OnDialogCloseListner){
            ((OnDialogCloseListner)activity).onDialogClose(dialog);
        }
    }
}
