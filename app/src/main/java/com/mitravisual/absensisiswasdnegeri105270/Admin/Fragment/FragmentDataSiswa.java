package com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Adapter.ItemAdapterDataSiswa;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment.AddItem.AddItemDataSiswa;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataSiswa;
import com.mitravisual.absensisiswasdnegeri105270.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDataSiswa#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDataSiswa extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FloatingActionButton fab;

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private ItemAdapterDataSiswa adapter;
    private ArrayList<DataSiswa> arrayList;

    public FragmentDataSiswa() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDataSiswa.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDataSiswa newInstance(String param1, String param2) {
        FragmentDataSiswa fragment = new FragmentDataSiswa();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_data_siswa, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayout = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayout);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tampildata();

        fab = rootView.findViewById(R.id.fabAdd);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddItemDataSiswa.class);
                startActivity(intent);
            }
        });

        return rootView;


    }

    private void tampildata() {
        databaseReference.child("196306261988031020").child("Data-Siswa").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()){

                    DataSiswa isi = new DataSiswa();
                    isi.setNISN(item.child("nisn").getValue(String.class));
                    isi.setNama(item.child("nama").getValue(String.class));
                    isi.setKelas(item.child("kelas").getValue(String.class));
                    isi.setAlamat(item.child("alamat").getValue(String.class));
                    isi.setKey(item.child("key").getValue(String.class));
                    isi.setUrl(item.child("url").getValue(String.class));
                    arrayList.add(isi);

                }

                adapter = new ItemAdapterDataSiswa(arrayList, getActivity());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}