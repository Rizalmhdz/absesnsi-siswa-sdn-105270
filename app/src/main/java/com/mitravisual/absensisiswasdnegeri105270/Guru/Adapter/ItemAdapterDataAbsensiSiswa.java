package com.mitravisual.absensisiswasdnegeri105270.Guru.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataGuru;
import com.mitravisual.absensisiswasdnegeri105270.Guru.Entity.DataQr;
import com.mitravisual.absensisiswasdnegeri105270.R;

import java.util.List;

public class ItemAdapterDataAbsensiSiswa extends RecyclerView.Adapter<ItemAdapterDataAbsensiSiswa.MyViewHolder>{

    List<DataQr> mList;
    Context context;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users");

    public ItemAdapterDataAbsensiSiswa(List<DataQr> mList, Context context) {
        this.mList = mList;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemAdapterDataAbsensiSiswa.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_absensi_siswa, parent , false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemAdapterDataAbsensiSiswa.MyViewHolder holder, int position) {
        final DataQr item = mList.get(position);

        holder.tvNama.setText(item.getNama() + " Kelas " +item.getKelas());
        holder.tvJam.setText(item.getJam() + " / " +item.getTanggal());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView tvNama, tvJam;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNama = itemView.findViewById(R.id.tvNama);
            tvJam = itemView.findViewById(R.id.tvJam);

        }
    }

    public Context getContext(){
        return context;
    }
}
