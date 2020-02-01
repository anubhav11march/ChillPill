package com.jyotishapp.chillpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.TimePicker;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

public class FrontActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private static final String TAG = "TAG";
    long cu, cene;
    int counter = 1;
    static TextView txt ;

    TextView signOut;
    FloatingActionButton fab, fab1, fab2, fab3;
    boolean hidden = true;

    LottieAnimationView load;

    RecyclerView recycler_view;
    FirebaseAuth auth;
    DatabaseReference my_ref;
    int REQUEST_LOCATION_PERMISSION = 1;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front);
        getSupportActionBar().hide();

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Patient").child(FirebaseAuth.getInstance().getUid()).child("Medicines");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt.setText(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

        txt = findViewById(R.id.text_view) ;
        SharedPreferences preferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
        boolean is_prev = preferences.getBoolean("is_prev", false);
        if (!is_prev) {
            startActivity(new Intent(this, DetailsActivity.class));
        }


//        recycler_view = findViewById(R.id.recycler_view);

        load = findViewById(R.id.load);
        fab = findViewById(R.id.fab);
        fab1 = findViewById(R.id.fab1);
        fab2 = findViewById(R.id.fab2);
        fab3 = findViewById(R.id.fab3);
        fab.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotate));

        fab3.setVisibility(View.INVISIBLE);
        fab2.setVisibility(View.INVISIBLE);
        fab1.setVisibility(View.INVISIBLE);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hidden) {
                    fab1.setVisibility(View.VISIBLE);
                    fab2.setVisibility(View.VISIBLE);
                    fab3.setVisibility(View.VISIBLE);

                    fab1.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
                    fab2.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));
                    fab3.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate));

                }
                else {
                    fab1.setVisibility(View.INVISIBLE);
                    fab2.setVisibility(View.INVISIBLE);
                    fab3.setVisibility(View.INVISIBLE);
                }
                hidden = !hidden;
            }
        });

        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseAuth.getInstance().getUid());

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            //qr activity
            Intent i = new Intent(FrontActivity.this, CameraActivity.class);
            i.putExtra("IS_QR", true);
            startActivity(i);
        }});
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //qr activity
                Intent i = new Intent(FrontActivity.this, CameraActivity.class);
                i.putExtra("IS_QR", false);
                startActivity(i);
            }});
        fab3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(FrontActivity.this, LoginActivity.class));
            finish();
        }});

        ConstraintLayout layout = findViewById(R.id.layout);
        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cu = System.currentTimeMillis();
                if (counter == 5) {
                    startActivity(new Intent(getApplicationContext(), EmergencyActivity.class));
                    counter = 1;
                } else if (counter == 1 || cu < cene + 500) {
                    Log.e("Emergency Counter ", counter + " ");
                    counter++;
                } else {
                    counter = 1;
                }
                cene = cu;
            }});

        FirebaseAuth auth = FirebaseAuth.getInstance();
        my_ref = FirebaseDatabase.getInstance().getReference().child("Patient")
                .child(auth.getCurrentUser().getUid()).child("Medicines");
    }

    void setAlarm(int hour){

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        startActivity(intent);
    }
}
