package com.mobile.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;

public class MainActivity extends AppCompatActivity {
    private CodeScanner mCodeScanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!hasCameraPermissions(this)) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);
        }

        setContentView(R.layout.activity_main);
        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        TextView labelText = findViewById(R.id.labelText);

        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            String scannedText = result.getText();
            if (scannedText.length() > 100){
                Log.d("CODE TEXT", scannedText);
                labelText.setText(mapText(scannedText));
                Toast.makeText(MainActivity.this, "Código escaneado correctamente",
                        Toast.LENGTH_SHORT).show();
            }else{
                mCodeScanner.startPreview();
            }
        }));
        scannerView.setOnClickListener(view -> {
            labelText.setText("");
            mCodeScanner.startPreview();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private String mapText(String rawText){
        rawText = rawText.replaceAll("\u0000", " ");

        String text = "";
        text = text.concat("CC: " + rawText.substring(48,58));
        text = text.concat("\nPRIMER APELLIDO: " + rawText.substring(58,80).trim());
        text = text.concat("\nSEGUNDO APELLIDO: " + rawText.substring(81,103).trim());
        text = text.concat("\nPRIMER NOMBRE: " + rawText.substring(104, 126).trim());
        text = text.concat("\nSEGUNDO NOMBRE: " + rawText.substring(127, 149).trim());
        text = text.concat("\nSEXO: " + rawText.substring(151, 152).trim());
        text = text.concat("\nFECHA NACIMIENTO: " + rawText.substring(158, 160) + "/" +
                rawText.substring(156, 158) + "/" + rawText.substring(152, 156));
        text = text.concat("\nTIPO DE SANGRE: " + rawText.substring(166, 168));

        return text;
    }

    private boolean hasCameraPermissions(Context context){
        if (context.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            Log.e("Take Photo", "PERMISSION DENIED");
            return false;
        }
        return true;
    }
}