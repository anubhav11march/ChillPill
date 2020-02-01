package com.jyotishapp.chillpill;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.common.FirebaseMLException;
import com.google.firebase.ml.common.modeldownload.FirebaseLocalModel;
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions;
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager;
import com.google.firebase.ml.common.modeldownload.FirebaseRemoteModel;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabeler;
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceAutoMLImageLabelerOptions;

import java.util.List;
import java.util.Locale;

public class MedicineActivity extends AppCompatActivity {

    private TextToSpeech mTTS ;
    Bitmap bitmap;
    TextView txt;
    FirebaseVisionImageLabeler labeler ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        getSupportActionBar().hide();
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = mTTS.setLanguage(Locale.ENGLISH);
                }
                else
                {
                    Log.e("TTS", "Initialization failed");
                }
            }
        });

        txt = findViewById(R.id.textView);

        bitmap = BitmapFactory.decodeFile(String.valueOf(CameraActivity.file));

        FirebaseModelDownloadConditions conditions = new FirebaseModelDownloadConditions.Builder()
                .requireWifi()
                .build();
        FirebaseRemoteModel remoteModel = new FirebaseRemoteModel.Builder("Med")
                .enableModelUpdates(true)
                .setInitialDownloadConditions(conditions)
                .setUpdatesDownloadConditions(conditions)
                .build();
        FirebaseModelManager.getInstance().registerRemoteModel(remoteModel);

        FirebaseLocalModel localModel = new FirebaseLocalModel.Builder("model")
                .setAssetFilePath("manifest.json")
                .build();
        FirebaseModelManager.getInstance().registerLocalModel(localModel);

        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionOnDeviceAutoMLImageLabelerOptions labelerOptions =
                new FirebaseVisionOnDeviceAutoMLImageLabelerOptions.Builder()
                        .setLocalModelName("model")    // Skip to not use a local model
                        .setRemoteModelName("Med")  // Skip to not use a remote model
                        .setConfidenceThreshold(0)  // Evaluate your model in the Firebase console
                        // to determine an appropriate value.
                        .build();
        try {
            labeler = FirebaseVision.getInstance().getOnDeviceAutoMLImageLabeler(labelerOptions);
        } catch (FirebaseMLException e) {
            e.printStackTrace();
        }
        final String[] text = new String[1];
        labeler.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionImageLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionImageLabel> labels) {
                        text[0] = labels.get(0).getText() ;
                        txt.setText(text[0]);
                        mTTS.setSpeechRate((float) 0.75);
                        mTTS.speak(text[0], TextToSpeech.QUEUE_FLUSH, null);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        e.printStackTrace();
                    }
                });

        txt.setText(text[0]);
    }
}
