package com.jyotishapp.chillpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class DocActivity extends AppCompatActivity {
    RecyclerView reports;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Test Reports");

        reports = (RecyclerView) findViewById(R.id.reports);

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("Patient").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PastReports");

        inflateRv(query);
    }

    public void inflateRv(Query query){
        FirebaseRecyclerOptions<PastReports> options = new FirebaseRecyclerOptions.Builder<PastReports>()
                .setQuery(query, PastReports.class)
                .build();

        FirebaseRecyclerAdapter FBRA = new FirebaseRecyclerAdapter<PastReports, TestReportViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TestReportViewHolder holder, int position, @NonNull PastReports model) {
                holder.setTitle(model.getName());
//                LinearLayout container = (LinearLayout) holder.mView.findViewById(R.id.container);
//                container.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        //TODO: download
//                    }
//                });
            }

            @NonNull
            @Override
            public TestReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new TestReportViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.report_card, parent, false));
            }
        };
        FBRA.startListening();
        reports.setAdapter(FBRA);
    }

    class TestReportViewHolder extends RecyclerView.ViewHolder{
        View mView;
        TestReportViewHolder(View itemView){
            super(itemView);
            mView = itemView;
        }

        void setTitle(String title){
            TextView titleT = (TextView) mView.findViewById(R.id.reportName);
            titleT.setText(title);
        }

    }

    public void download(View view){

    }
}
