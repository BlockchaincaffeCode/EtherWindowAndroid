package org.blockchaincaffe.etherwindow;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.zxing.Result;

import org.web3j.crypto.WalletUtils;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.Manifest.permission.CAMERA;

public class QR extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final int REQUEST_CAMERA = 1;
    private ZXingScannerView scannerView;
    private static int camId = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scannerView = new ZXingScannerView(this);

        int currentApiVersion = Build.VERSION.SDK_INT;

        if(currentApiVersion >=  Build.VERSION_CODES.M)
        {
            if(checkPermission())
            {

            }
            else
            {
                requestPermission();
            }
        }
        setContentView(scannerView);
    }

    private boolean checkPermission()
    {
        return (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission()
    {
        ActivityCompat.requestPermissions(this, new String[]{CAMERA}, REQUEST_CAMERA);
    }

    @Override
    public void onResume() {
        super.onResume();

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= android.os.Build.VERSION_CODES.M) {
            if (checkPermission()) {
                if(scannerView == null) {
                    scannerView = new ZXingScannerView(this);
                    setContentView(scannerView);
                }
                scannerView.setResultHandler(this);
                scannerView.startCamera();
            } else {
                requestPermission();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        scannerView.stopCamera();
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CAMERA:
                if (grantResults.length > 0) {

                    boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (cameraAccepted){
                        Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access camera", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and camera", Toast.LENGTH_LONG).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(CAMERA)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(new String[]{CAMERA},
                                                            REQUEST_CAMERA);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(QR.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    public void handleResult(Result result) {

        // nonce/receiver/amount/gasprice/gaslimit;
        String fill=result.getText();
        if(WalletUtils.isValidAddress(result.getText())){
            Wallet.receiver.setText(fill);
            Wallet.nonce.setText("");
            Wallet.sum.setText("");
            Wallet.seekBar.setProgress(1);
            Wallet.gasLimit.setText("");
        }
        else
        if(result.getText().contains(":")){
            String list[]=result.getText().split(":");
            fill=list[1];
           if(WalletUtils.isValidAddress(fill)){
            Wallet.receiver.setText(fill);
            Wallet.nonce.setText("");
            Wallet.sum.setText("");
            Wallet.seekBar.setProgress(1);
            Wallet.gasLimit.setText("");}
        }else
        if(result.getText().contains("/")){
            String list[]=result.getText().split("/");
            if(list.length==5 && WalletUtils.isValidAddress(list[1])) {
                Wallet.nonce.setText(list[0]);
                Wallet.receiver.setText(list[1]);
                Wallet.sum.setText(list[2]);
                Wallet.seekBar.setProgress(Integer.parseInt(list[3]));
                Wallet.gasLimit.setText(list[4]);
            }
        }else{
            Wallet.receiver.setText("");
            Wallet.nonce.setText("");
            Wallet.sum.setText("");
            Wallet.seekBar.setProgress(1);
            Wallet.gasLimit.setText("");

        }


        super.onBackPressed();

    }
}