package org.blockchaincaffe.etherwindow;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File address = new File(getApplicationContext().getFilesDir(),"address.json");
        if(address.exists()){

            LayoutInflater li = LayoutInflater.from(MainActivity.this);
            View view2 = li.inflate(R.layout.pass, null);

            final EditText walletPassword = view2.findViewById(R.id.walletPassword);
            File walletPass = new File(getApplicationContext().getFilesDir(),"walletPass.txt");
            String psw="";
            try {
                BufferedReader br = new BufferedReader(new FileReader(walletPass));
                psw= br.readLine();
                br.close();
            }
            catch (IOException e) {

            }

            String finalPsw = psw;
            new AlertDialog.Builder(MainActivity.this, R.style.dialogStyle)
                    .setTitle("Login to EtherWindow")
                    .setView(view2)
                    .setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            if(finalPsw.equals(walletPassword.getText().toString())){

                                Intent intent = new Intent(MainActivity.this,Wallet.class);
                                startActivity(intent);

                            }else{

                                Toast.makeText(getBaseContext(),"Wrong password",Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }


                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            finishAffinity();
                            System.exit(0);
                        }
                    })
                    .show();

        }
        else{
            Intent intent = new Intent(MainActivity.this,ChoiceActivity.class);
            startActivity(intent);

        }



    }
}
