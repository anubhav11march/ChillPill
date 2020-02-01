package com.jyotishapp.chillpill;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailsActivity extends AppCompatActivity {

    TextInputLayout name, age;
    TextView male, female;
    public static String IS_MALE = "Male";

    FirebaseDatabase firebaseDatabase;
    DatabaseReference ref;
    FirebaseAuth auth;

    public static final String PATIENT = "Patient";
    public static final String NAME = "Name";
    public static final String AGE = "Age";
    public static final String GENDER = "Gender";

    public static final String PERSONAL_INFO = "Personal_Info";
    public static final String PAST_REP = "Reports";
    public static final String MEDICINES = "Medicines";
    public static final String APPOINTMENTS = "Appointments";

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        firebaseDatabase = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();


        male = findViewById(R.id.male);
        female= findViewById(R.id.female);
        name = findViewById(R.id.name);
        age = findViewById(R.id.age);

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (TextUtils.isEmpty(name.getEditText().getText())){
                    name.setError("Required");
                    return;
                }
                if (TextUtils.isEmpty(age.getEditText().getText())){
                    age.setError("Required");
                    return;
                }

                ref = firebaseDatabase.getReference().child(PATIENT)
                        .child(auth.getCurrentUser().getUid());
//                                            .child(auth.getUid());

                saveData();

                Toast.makeText(DetailsActivity.this, "saved", Toast.LENGTH_SHORT).show();

                SharedPreferences preferences = getSharedPreferences("shared_pref", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                editor.putBoolean("is_prev", true);
                editor.apply();

                finish();

            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                female.setBackground(getResources().getDrawable(R.drawable.save_now_bg));
                male.setBackground(getResources().getDrawable(R.drawable.save_now_bg_2));
                IS_MALE = "Female";
                female.setTextColor(Color.WHITE);
                male.setTextColor(Color.BLACK);
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                male.setBackground(getResources().getDrawable(R.drawable.save_now_bg));
                female.setBackground(getResources().getDrawable(R.drawable.save_now_bg_2));
                IS_MALE = "Male";
                male.setTextColor(Color.WHITE);
                female.setTextColor(Color.BLACK);
            }
        });



    }

    private void saveData() {

        //personal ref
        DatabaseReference personal_ref = ref.child(PERSONAL_INFO);
        personal_ref.child(NAME).setValue(name.getEditText().getText().toString().trim());
        personal_ref.child(AGE).setValue(age.getEditText().getText().toString().trim());

        personal_ref.child(DetailsActivity.GENDER).setValue(IS_MALE);

        //past reports reference
        DatabaseReference past_rep = ref.child(PAST_REP);
        past_rep.child("test 1").setValue("null");

        //medicines ref
        DatabaseReference med_ref = ref.child(MEDICINES);
        med_ref.child("null").setValue("0-0-0-0");

        //appointments reference
        DatabaseReference appoint_ref = ref.child(APPOINTMENTS);
        appoint_ref.child("Date").setValue("null");
        appoint_ref.child("Remarks").setValue("null");

    }
}
