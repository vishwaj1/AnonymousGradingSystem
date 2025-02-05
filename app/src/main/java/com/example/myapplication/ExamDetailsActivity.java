package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.contract.ActivityResultContracts;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;

import com.googlecode.tesseract.android.TessBaseAPI;



public class ExamDetailsActivity extends AppCompatActivity {

    protected Button BarcodeMap, BarcodeScanner;
    protected TextView ExamName;

    private ImageView myImage;
    private static int code =5;

    //Bitmap imageBitmap;
    //String grade;

    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exam_details_activity);

        BarcodeMap = (Button) findViewById(R.id.Barcode);
        BarcodeScanner = (Button) findViewById(R.id.ScanBarcode);
        //myImage = findViewById(R.id.barcodeImage);

        String ExamName_ = getIntent().getStringExtra("ExamName_");

        ExamName = (TextView) findViewById(R.id.ExamName);
        ExamName.setText(ExamName_);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);

        BarcodeScanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                activityResultLauncher.launch(takePictureIntent);
                startBarcodeScanner();


            }
        });

        BarcodeMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent barcodemappers = new Intent(ExamDetailsActivity.this, Barcodemapping.class);
                startActivity(barcodemappers);
            }
        });
    }

    private void startBarcodeScanner() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBeepEnabled(false);
        integrator.initiateScan();

    }
        ActivityResultLauncher<Intent> activityResultLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>() {
                            @Override
                            public void onActivityResult(ActivityResult o) {
                                if(o.getResultCode() == RESULT_OK){
                                    Intent data = o.getData();
                                    Bitmap imageBitmap = (Bitmap) (data.getExtras()).get("data");
                                    myImage.setImageBitmap(imageBitmap);

                                    //grade = processImageForGrade(imageBitmap);

                                    //startBarcodeScanner();

                                }
                            }
                        });
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Handle the result of the barcode scanner
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {


                String grade = extractGradeFromPaper();
                //String grade = processImageForGrade(imageBitmap);
                String scannedBarcode = result.getContents();

                handleBarcodeScannerResult(scannedBarcode, grade);
                displayScannedBarcode(scannedBarcode, grade);
                createViewGradesButton();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private String processImageForGrade(Bitmap imageBitmap) {

        Bitmap grayscaleBitmap = toGrayscale(imageBitmap);

        String extractedText = performOCR(grayscaleBitmap);

        String grade1 = analyzeExtractedText(extractedText);

        return grade1;
    }

    public static Bitmap toGrayscale(Bitmap imageBitmap) {
        int width, height;
        height = imageBitmap.getHeight();
        width = imageBitmap.getWidth();

        Bitmap grayscaleBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(grayscaleBitmap);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvas.drawBitmap(imageBitmap, 0, 0, paint);

        return grayscaleBitmap;
    }

    private String extractGradeFromPaper() {

        return "A";
    }

    public static String performOCR(Bitmap grayscaleBitmap) {
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init("/Users/vishwanthreddyjakka/Downloads/eng.traineddata", "eng"); // Replace DATA_PATH and LANG with your language data path and language code
        tessBaseAPI.setImage(grayscaleBitmap);
        String extractedText = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();

        return extractedText;
    }

    public static String analyzeExtractedText(String extractedText) {

        char firstChar = extractedText.charAt(0);
        if (Character.isLetter(firstChar)) {
            return String.valueOf(firstChar);
        } else {
            return "Unknown";
        }
    }

    private void handleBarcodeScannerResult(String barcode, String grade) {
        // Store the grade associated with the student's barcode
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(barcode + "_grade", grade);
        editor.apply();
    }

    private void storeScannedBarcode(String barcode) {
        SharedPreferences.Editor editor = getSharedPreferences("ScannedBarcode", MODE_PRIVATE).edit();
        editor.putString("barcode", barcode);
        editor.apply();
    }

    private void displayScannedBarcode(String barcode, String grade) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Scanned Barcode");
        builder.setMessage("UMBC Id: " + barcode + "\nGrade: " + grade);
        builder.setPositiveButton("OK", null);

        AlertDialog dialog = builder.create();
        dialog.show();
        //createViewGradesButton();
    }

    private void createViewGradesButton() {
        Button viewGradesButton = new Button(this);
        viewGradesButton.setText("View Grades");
        viewGradesButton.setElevation(2);
        viewGradesButton.setTextSize(16);
        viewGradesButton.setBackgroundResource(R.drawable.button_background);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        LinearLayout layout = findViewById(R.id.exam_details);
        layout.addView(viewGradesButton, layoutParams);

        viewGradesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayStudentGrades();
            }
        });
    }

    private void displayStudentGrades() {
        SharedPreferences sharedPreferences = getSharedPreferences("StudentData", MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPreferences.getAll();

        StringBuilder gradesBuilder = new StringBuilder();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.contains("_grade")) {
                String umbcId = key.substring(0, key.lastIndexOf("_grade"));
                String grade = entry.getValue().toString();
                gradesBuilder.append("UMBC ID: ").append(umbcId).append(", Grade: ").append(grade).append("\n");
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Student Grades");
        builder.setMessage(gradesBuilder.toString());
        builder.setPositiveButton("OK", null);
        builder.show();
    }




}
