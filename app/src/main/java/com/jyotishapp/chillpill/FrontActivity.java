package com.jyotishapp.chillpill;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class FrontActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener{
    private static final String TAG = "TAG";
    long cu, cene;
    int counter = 1;
    static TextView txt ;
    int BARCODE_READER = 1;
    ScrollView rvContainer;
    ImageView menuOpen;

    TextView signOut, med_left, appointment;
    static int version ;
    RecyclerView medList;

    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyqqDhNk:APA91bFzMXH66vRN_SU41gEsDVgnNkIu6zq3hgY9SljqoRtf1D3oOSRS28BijHf829jq-y0wOCnzpPEpO7MvzLTB6NIgW5mFLG65RLg6irMYeA-Hi6SzGMbxRMPsnJMpmq39t9RT3UqO";
    final private String contentType = "application/json";
    String mess ;
    String TOPIC ;

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
        med_left = (TextView) findViewById(R.id.med_left);
        appointment = (TextView) findViewById(R.id.appointment);
        medList = (RecyclerView) findViewById(R.id.med_list);
        medList.setLayoutManager(new LinearLayoutManager(this));
        rvContainer = (ScrollView) findViewById(R.id.rvContainer);
        menuOpen = (ImageView) findViewById(R.id.menuOpen);

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Patient").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Medicines");

        inflateRecyclerView(query);

        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child("Patient").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        Toast.makeText(FrontActivity.this, FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber(), Toast.LENGTH_SHORT).show();
        mess = FirebaseAuth.getInstance().getUid() ;
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Appointments").child("Date").getValue() != null )
                    appointment.setText(dataSnapshot.child("Appointments").child("Date").getValue().toString());
                else
                    appointment.setText("None");

                med_left.setText(dataSnapshot.child("Medicines").getChildrenCount()+"");
//                txt.setText(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                    int x=0;
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
            Intent i = new Intent(FrontActivity.this, BarcodeCaptureActivity.class);
            startActivityForResult(i, BARCODE_READER);

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

        rvContainer.setOnClickListener(new View.OnClickListener() {
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
            }
        });

        menuOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(FrontActivity.this, view);
                popupMenu.getMenuInflater().inflate(R.menu.doc_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.doc:
                                startActivity(new Intent(FrontActivity.this, DocActivity.class));
                                break;
                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

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

    public void inflateRecyclerView(Query query){
        FirebaseRecyclerOptions<Medicines> options = new FirebaseRecyclerOptions.Builder<Medicines>()
                .setQuery(query, Medicines.class)
                .build();

        FirebaseRecyclerAdapter FBRA = new FirebaseRecyclerAdapter<Medicines, MedicinesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull MedicinesViewHolder holder, int position, @NonNull Medicines model) {
                String data = model.getMedicine();
                String routine = (char)data.charAt(0) +""+ (char)data.charAt(2) +"" +(char) data.charAt(4) +"";
                String medName = data.substring(6);
                holder.setRoutine(routine);
                holder.setMedName(medName);
                load.setVisibility(View.GONE);
            }

            @NonNull
            @Override
            public MedicinesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MedicinesViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.med_card, parent, false));
            }
        };
        FBRA.startListening();
        medList.setAdapter(FBRA);
    }

    class MedicinesViewHolder extends RecyclerView.ViewHolder{
        View mView;
        MedicinesViewHolder(@NonNull View itemView){
            super(itemView);
            mView = itemView;
        }

        public void setMedName(String medName){
            TextView medNameT = (TextView) mView.findViewById(R.id.medName);
            medNameT.setText(medName);
        }

        public void setRoutine(String routine){
            TextView mornT = (TextView) mView.findViewById(R.id.morn);
            TextView noonT = (TextView) mView.findViewById(R.id.noon);
            TextView nightT = (TextView) mView.findViewById(R.id.night);
            if (routine.charAt(0) == 'X')
                mornT.setText("Morning");
            else
                mornT.setText("     ");
            if (routine.charAt(1) == 'X')
                noonT.setText("Afternoon");
            else
                noonT.setText("     ");
            if (routine.charAt(2) == 'X')
                nightT.setText("Night");
            else
                nightT.setText("     ");
        }

    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {

        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM);
        intent.putExtra(AlarmClock.EXTRA_HOUR, hour);
        intent.putExtra(AlarmClock.EXTRA_MINUTES, minute);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            if(requestCode == BARCODE_READER){
                if(resultCode == CommonStatusCodes.SUCCESS){
                    if(data != null){
                        Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                        Point[] p =barcode.cornerPoints;
                        Toast.makeText(FrontActivity.this, barcode.displayValue, Toast.LENGTH_SHORT).show();
                        TOPIC = barcode.displayValue ; //topic must match with what the receiver subscribed to
                        Log.e("TOPIC", TOPIC );
                        Log.e("DBREF", TOPIC);
                        String NOTIFICATION_TITLE = "Notification Trial";

                        String NOTIFICATION_MESSAGE = mess ;
                        Toast.makeText(getApplicationContext() ,"Notification Sent",Toast.LENGTH_LONG).show();
                        JSONObject notification = new JSONObject();
                        JSONObject notifcationBody = new JSONObject();
                        try {
                            notifcationBody.put("title", NOTIFICATION_TITLE);
                            notifcationBody.put("message", NOTIFICATION_MESSAGE);

                            notification.put("to", TOPIC);
                            notification.put("data", notifcationBody);
                        } catch (JSONException e) {
                            Log.e("FCM", "onCreate: " + e.getMessage() );
                        }
                        Log.e("Hello", notification.toString() );
                        sendNotification(notification);
                    }
                    else {
                        Toast.makeText(FrontActivity.this, "Barcode Scan Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    Log.v("AAA", CommonStatusCodes.getStatusCodeString(resultCode));
            }
            else {
                super.onActivityResult(requestCode, resultCode, data);
            }
    }

    private void sendNotification(JSONObject notification) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(FCM_API, notification,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("FCM", "onResponse: " + response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext() , "Request error", Toast.LENGTH_LONG).show();
                        Log.i("FCM", "onErrorResponse: Didn't work");
                    }
                }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", serverKey);
                params.put("Content-Type", contentType);
                return params;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

}
