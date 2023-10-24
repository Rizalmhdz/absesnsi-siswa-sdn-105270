package com.mitravisual.absensisiswasdnegeri105270.Admin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitravisual.absensisiswasdnegeri105270.Admin.AdminActivity;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataGuru;
import com.mitravisual.absensisiswasdnegeri105270.R;

import java.util.List;

public class ItemAdapterDataGuru extends RecyclerView.Adapter<ItemAdapterDataGuru.MyViewHolder>{

    List<DataGuru> mList;
    Context context;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    public ItemAdapterDataGuru(List<DataGuru> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdapterDataGuru.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_data_guru, parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapterDataGuru.MyViewHolder holder, int position) {
        final DataGuru item = mList.get(position);

        holder.tvNIP.setText(item.getNIP());
        holder.tvNama.setText("Nama : " + item.getNama());
        holder.tvPassword.setText("Password : " + item.getPassword());
        holder.tvEmail.setText("Email : " + item.getEmail());
        holder.tvGuruKelas.setText("Guru Kelas : " + item.getGuruKelas());
        if(item.getTerdafar().equals("Belum Terdaftar")){
            holder.btnTerdaftar.setText(item.getTerdafar());
            holder.btnTerdaftar.setBackgroundColor(Color.parseColor("#F44336"));
        }else {
            holder.btnTerdaftar.setText(item.getTerdafar());
            holder.btnTerdaftar.setBackgroundColor(Color.GREEN);
        }

        holder.btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Hapus Data");
                builder.setCancelable(true);
                builder.setMessage("Anda Yakin Ingin Menghapus Data?")  ;
                builder.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //hapus di admin
                        databaseReference.child("196306261988031020").child("Data-Guru").child(item.getNIP()).setValue(null);

                        //hapus di user
                        databaseReference.child(item.getNIP()).setValue(null);

                        Intent Masuk = new Intent(context, AdminActivity.class);
                        context.startActivity(Masuk);
                        dialog.dismiss();
                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNIP, tvNama, tvPassword, tvEmail, tvGuruKelas;
        private Button btnTerdaftar, btnHapus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNIP = itemView.findViewById(R.id.tvNIP);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvPassword = itemView.findViewById(R.id.tvPassword);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvGuruKelas = itemView.findViewById(R.id.tvKelas);
            btnTerdaftar = itemView.findViewById(R.id.btnTerdaftar);
            btnHapus = itemView.findViewById(R.id.btnHapus);

        }
    }
}
