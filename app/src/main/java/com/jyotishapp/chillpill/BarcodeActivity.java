package com.jyotishapp.chillpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarcodeActivity extends AppCompatActivity {

    TextView txt ;
    Bitmap bitmap ;
    final private String FCM_API = "https://fcm.googleapis.com/fcm/send";
    final private String serverKey = "key=" + "AAAAyqqDhNk:APA91bFzMXH66vRN_SU41gEsDVgnNkIu6zq3hgY9SljqoRtf1D3oOSRS28BijHf829jq-y0wOCnzpPEpO7MvzLTB6NIgW5mFLG65RLg6irMYeA-Hi6SzGMbxRMPsnJMpmq39t9RT3UqO";
    final private String contentType = "application/json";
    String mess ;
    String TOPIC ;
    public String string = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode);
        mess = FirebaseAuth.getInstance().getUid() ;

        bitmap = BitmapFactory.decodeFile(String.valueOf(CameraActivity.file));
        txt = findViewById(R.id.textView);

        FirebaseVisionBarcodeDetectorOptions options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(
                                FirebaseVisionBarcode.FORMAT_ALL_FORMATS )
                        .build();

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

        Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionBarcode> barcodes) {

                        txt.setText(barcodes.size() + "");
                        for (FirebaseVisionBarcode barcode: barcodes) {
                            Rect bounds = barcode.getBoundingBox();

                            String rawValue = barcode.getRawValue();
                            txt.setText(rawValue);

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace() ;
                    }
                });
//        string = txt.getText().toString() ;
//        txt.setText(string.substring(0 , string.indexOf(':')));
//        TOPIC = string.substring(string.indexOf(':'));
        TOPIC = "fNUOhWucTAHS7YsC94hPFY:APA91bETp00K7WUgNbKS7Gy5RhH4qENcPayldouU4iKrzUHnk15PE-ZLnR2wbwIqGJG2kmLOXbhYF_nWgPc0Rsq3T7BeKYSw3Z1dXrH1xTwIBzG6KelJ-vlq_ysejv0zd7PK7BZxUliG" ; //topic must match with what the receiver subscribed to
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
        BarcodeSingletonClass.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }
}
