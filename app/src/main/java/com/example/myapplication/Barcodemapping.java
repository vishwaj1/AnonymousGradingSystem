package com.example.myapplication;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import java.util.Map;

public class Barcodemapping extends AppCompatActivity {

    private LinearLayout barcodeLayout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcodemap);
        barcodeLayout = findViewById(R.id.barcodeLayout);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("StudentInfo", MODE_PRIVATE);

        // Display names and barcodes
        try {
            displayNamesAndBarcodes();
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }
    }

    private void displayNamesAndBarcodes() throws WriterException {
        Map<String, ?> allEntries = sharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String studentName = entry.getValue().toString();
            String umbcId = entry.getKey();
            Bitmap barcodeBitmap = generateBarcode(umbcId);
            addBarcodeAndName(studentName, barcodeBitmap);
        }
    }

    private Bitmap generateBarcode(String umbcId) throws WriterException {
        Bitmap bitmap = null;
        Code128Writer writer = new Code128Writer();
        BitMatrix bitMatrix = writer.encode(umbcId, BarcodeFormat.CODE_128, 600, 200);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

    private void addBarcodeAndName(String studentName, Bitmap barcodeBitmap) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 16);

        LinearLayout itemLayout = new LinearLayout(this);
        itemLayout.setLayoutParams(layoutParams);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);

        TextView textViewName = new TextView(this);
        textViewName.setText(studentName);
        textViewName.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1.0f
        ));
        itemLayout.addView(textViewName);

        ImageView imageViewBarcode = new ImageView(this);
        imageViewBarcode.setImageBitmap(barcodeBitmap);
        imageViewBarcode.setLayoutParams(new LinearLayout.LayoutParams(
                800, // Set a fixed width for the ImageView (adjust as needed)
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        itemLayout.addView(imageViewBarcode);

        barcodeLayout.addView(itemLayout);
    }


}
