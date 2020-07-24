package org.blockchaincaffe.etherwindow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.bitcoinj.crypto.ChildNumber;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.HDUtils;
import org.bitcoinj.wallet.DeterministicKeyChain;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.WalletUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

public class RestoreAddressActivity extends AppCompatActivity {
    EditText walletPass1;
    EditText walletPass2;
    EditText passPhrase;

    public static EditText enterWords;

    Button nextButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restore_address);

        walletPass1=findViewById(R.id.walletPass1);
        walletPass1.requestFocus();
        walletPass2=findViewById(R.id.walletPass2);
        passPhrase=findViewById(R.id.passPhrase);
        enterWords=findViewById(R.id.enterWords);
        enterWords.setOnLongClickListener(new View.OnLongClickListener() {

            public boolean onLongClick(View arg0) {
                Intent intent = new Intent(RestoreAddressActivity.this,QRseed.class);
                startActivity(intent);

                return true;
            }

        });

        nextButton=findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setEnabled(false);
                restoreAddress();
            }
        });

    }

    public void restoreAddress() {
        String seed =enterWords.getText().toString().trim();
        String password=walletPass1.getText().toString();
        String password1=walletPass2.getText().toString();
        String passphrase=passPhrase.getText().toString();
        String niz[] = seed.split(" ");
        String filePath = getApplicationContext().getFilesDir().toString();
        if ((niz.length ==24 || niz.length ==12) && seed.length()>0 && password.length()>0 && password.equals(password1)) {
            String words = ChoiceActivity.words;
            boolean move = true;
            for (int i = 0; i < niz.length; i++) {
                if(words.contains("\""+niz[i]+"\"")){ }else{move=false;}
            }
            if (move) {

                try {


                    String fileName = "";
                    try {

                        DeterministicSeed seed2 = new DeterministicSeed(seed, null, passphrase, 1409478661L);
                        DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed2).build();
                        List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
                        DeterministicKey key2 = chain.getKeyByPath(keyPath, true);
                        BigInteger privKey = key2.getPrivKey();

                        //web3j
                        Credentials credentials = Credentials.create(privKey.toString(16));
                        ECKeyPair ecKeyPair = credentials.getEcKeyPair();

                        fileName = WalletUtils.generateWalletFile(password, ecKeyPair, getApplicationContext().getFilesDir(), false);

                    } catch (UnreadableWalletException e) {
                        e.printStackTrace();
                    }


                    FileOutputStream pas = this.openFileOutput("walletPass.txt", Context.MODE_PRIVATE);
                    pas.write(password.getBytes());
                    pas.close();

                    FileOutputStream fOut = this.openFileOutput("seedWords.txt", Context.MODE_PRIVATE);
                    fOut.write(seed.getBytes());
                    fOut.close();

                    FileOutputStream passf = this.openFileOutput("passPhrase.txt", Context.MODE_PRIVATE);
                    passf.write(passphrase.getBytes());
                    passf.close();

                    File ff = new File(filePath + "/" + fileName);

                    File newfile = new File(filePath + "/" + "address.json");

                    if (ff.renameTo(newfile)) {


                        nextButton.setEnabled(true);
                        Intent intent = new Intent(RestoreAddressActivity.this,Wallet.class);
                        startActivity(intent);
                    } else {

                        nextButton.setEnabled(true);
                    }

                } catch (CipherException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Seed or password error",
                            Toast.LENGTH_LONG).show();

                    nextButton.setEnabled(true);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "IO error",
                            Toast.LENGTH_LONG).show();

                    nextButton.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Seed or password error",
                            Toast.LENGTH_LONG).show();

                    nextButton.setEnabled(true);
                }

            }else{
                Toast.makeText(getApplicationContext(), "Seed error",
                        Toast.LENGTH_LONG).show();

                nextButton.setEnabled(true);

            }


        }
        else{
            Toast.makeText(getApplicationContext(), "Seed or password error",
                    Toast.LENGTH_LONG).show();

            nextButton.setEnabled(true);

        }

    }
}