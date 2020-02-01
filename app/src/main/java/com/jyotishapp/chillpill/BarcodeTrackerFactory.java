package com.jyotishapp.chillpill;

import android.content.Context;

import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        return new BarcodeTracker(mContext);
    }
    private Context mContext;

    BarcodeTrackerFactory(Context context) {
        mContext = context;
    }

}
