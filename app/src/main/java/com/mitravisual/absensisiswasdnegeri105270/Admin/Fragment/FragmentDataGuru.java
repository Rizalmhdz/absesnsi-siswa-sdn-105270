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
import com.mitravisual.absensisiswasdnegeri105270.Admin.Adapter.ItemAdapterDataGuru;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Fragment.AddItem.AddItemDataGuru;
import com.mitravisual.absensisiswasdnegeri105270.Admin.Entity.DataGuru;
import com.mitravisual.absensisiswasdnegeri105270.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDataGuru#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDataGuru extends Fragment {

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
    private ItemAdapterDataGuru adapter;
    private ArrayList<DataGuru> arrayList;

    //sharedPreferences
    public FragmentDataGuru() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDataGuru.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDataGuru newInstance(String param1, String param2) {
        FragmentDataGuru fragment = new FragmentDataGuru();
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
        View rootView = inflater.inflate(R.layout.fragment_data_guru, container, false);

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
                Intent intent = new Intent(getActivity(), AddItemDataGuru.class);
                startActivity(intent);
            }
        });

        return rootView;
    }

    private void tampildata() {
        databaseReference.child("196306261988031020").child("Data-Guru").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();

                for (DataSnapshot item : snapshot.getChildren()){

                    DataGuru isi = new DataGuru();
                    isi.setNIP(item.child("nip").getValue(String.class));
                    isi.setNama(item.child("nama").getValue(String.class));
                    isi.setPassword(item.child("password").getValue(String.class));
                    isi.setEmail(item.child("email").getValue(String.class));
                    isi.setGuruKelas(item.child("guruKelas").getValue(String.class));
                    isi.setTerdafar(item.child("terdaftar").getValue(String.class));
                    arrayList.add(isi);

                }

                adapter = new ItemAdapterDataGuru(arrayList, getActivity());
                recyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}