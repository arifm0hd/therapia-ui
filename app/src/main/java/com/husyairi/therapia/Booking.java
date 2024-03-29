package com.husyairi.therapia;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Booking extends AppCompatActivity implements View.OnClickListener{
    private RelativeLayout home_icon_navbar, profile_icon_navbar, activity_icon_navbar;
    RecyclerView recyclerView;
    List<DataClass> dataList = new ArrayList<>();
    DatabaseReference databaseReference;
    ValueEventListener eventListener;

    TextView emptyText;
    FirebaseUser user;
    FirebaseAuth auth;

    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);
        getSupportActionBar().hide();

        home_icon_navbar = findViewById(R.id.home_layout);
        home_icon_navbar.setOnClickListener(this);

        profile_icon_navbar = findViewById(R.id.profile_layout);
        profile_icon_navbar.setOnClickListener(this);

        activity_icon_navbar = findViewById(R.id.activity_layout);
        activity_icon_navbar.setOnClickListener(this);

        fab = findViewById(R.id.addpost);
        recyclerView = findViewById(R.id.recyclerView);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(Booking.this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        AlertDialog.Builder builder = new AlertDialog.Builder(Booking.this);
        builder.setCancelable(false);
        builder.setView(R.layout.progress_layout);
        AlertDialog dialog = builder.create();
        dialog.show();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        String sanitizedEmail = user.getEmail().replace('.',',');

        MyAdapter adapter = new MyAdapter(Booking.this, dataList);
        recyclerView.setAdapter(adapter);
        databaseReference = FirebaseDatabase.getInstance().getReference(sanitizedEmail);
        dialog.show();

        eventListener = databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                for(DataSnapshot itemSnapshot: snapshot.getChildren()){
                    DataClass dataClass = itemSnapshot.getValue(DataClass.class);
                    if ("null".equals(dataClass.getJobAccepted())) {
                        dataList.add(dataClass);
                    }
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                dialog.dismiss();
            }


        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Booking.this, UploadPosting.class);
                startActivity(intent);
            }
        });

        emptyText = findViewById(R.id.emptyText);

        recyclerView.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();

                if (recyclerView.getAdapter().getItemCount() > 0) {
                    emptyText.setText(""); // Hide the empty text message
                } else {
                    emptyText.setText("Nothing to show.."); // Show the empty text message
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        Intent intent;

        switch(view.getId()){

            case R.id.home_layout:
                intent = new Intent(this, PatientHomepage.class);
                break;

            case R.id.profile_layout:
                intent = new Intent(getApplicationContext(), PatientProfile.class);
                break;

            case R.id.activity_layout:
                intent = new Intent(getApplicationContext(), PatientActivity.class);
                break;

            default:
                intent = null;
        }

        if(intent != null){
            startActivity(intent);
            finish();
        }
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}