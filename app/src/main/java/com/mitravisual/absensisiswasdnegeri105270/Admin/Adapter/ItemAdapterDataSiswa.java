package com.mitravisual.absensisiswasdnegeri105270.Admin.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitravisual.absensisiswasdnegeri105270.Admin.AdminActivity;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataGuru;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataSiswa;
import com.mitravisual.absensisiswasdnegeri105270.R;

import java.util.List;

public class ItemAdapterDataSiswa extends RecyclerView.Adapter<ItemAdapterDataSiswa.MyViewHolder>{

    List<DataSiswa> mList;
    Context context;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    public ItemAdapterDataSiswa(List<DataSiswa> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdapterDataSiswa.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_data_siswa, parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapterDataSiswa.MyViewHolder holder, int position) {
        final DataSiswa item = mList.get(position);

        holder.tvNISN.setText(item.getNISN());
        holder.tvNama.setText("Nama : " + item.getNama());
        holder.tvKelas.setText("Kelas : " + item.getKelas());
        holder.tvAlamat.setText("Alamat : " + item.getAlamat());

        holder.btnTtd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pdf = new Intent(Intent.ACTION_VIEW);
                pdf.setType("application/pdf");
                pdf.setData(Uri.parse(item.getUrl()));
                context.startActivity(pdf);
            }
        });

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
                        databaseReference.child("196306261988031020").child("Data-Siswa").child(item.getNISN()).setValue(null);

                        //hapus di user
                        databaseReference.child(item.getNISN()).setValue(null);

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

        private TextView tvNISN, tvNama, tvKelas, tvAlamat;
        private Button btnTtd, btnHapus;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNISN = itemView.findViewById(R.id.tvNISN);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvKelas = itemView.findViewById(R.id.tvKelas);
            tvAlamat = itemView.findViewById(R.id.tvAlamat);
            btnTtd = itemView.findViewById(R.id.btnTtd);
            btnHapus = itemView.findViewById(R.id.btnHapus);

        }
    }
}
