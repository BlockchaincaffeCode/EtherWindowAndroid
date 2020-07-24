package org.blockchaincaffe.etherwindow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

public class ChoiceActivity extends AppCompatActivity {
    public static String words ="";
    Button makeNewAddress;
    Button restoreAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice);



        makeNewAddress=findViewById(R.id.makeNewAddress);
        restoreAddress=findViewById(R.id.restoreAddress);

        makeNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this,MakeAdrActivity.class);
                startActivity(intent);

            }
        });

        restoreAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChoiceActivity.this,RestoreAddressActivity.class);
                startActivity(intent);

            }
        });

        InputStream is;
        try {
            is =getAssets().open("wordsJ.txt");
            int size =is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            words=new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed(){

        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);





    }
}