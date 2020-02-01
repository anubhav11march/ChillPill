package com.jyotishapp.chillpill;

import android.content.Context;

import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.barcode.Barcode;

public class BarcodeTracker extends Tracker<Barcode> {
     interface BarcodeGraphicTrackerCallback{
        void onDetectedQRCode(Barcode barcode);
    }

    private BarcodeGraphicTrackerCallback mListener;

    BarcodeTracker(Context listener){
        mListener =(BarcodeGraphicTrackerCallback)listener;
    }

    @Override
    public void onNewItem(int i, Barcode barcode) {
        if(barcode.displayValue != null){
            mListener.onDetectedQRCode(barcode);
        }
    }
}
