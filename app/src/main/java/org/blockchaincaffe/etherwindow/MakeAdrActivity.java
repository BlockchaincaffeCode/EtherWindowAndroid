package org.blockchaincaffe.etherwindow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import java.security.SecureRandom;
import java.util.List;

import io.github.novacrypto.bip39.MnemonicGenerator;
import io.github.novacrypto.bip39.Words;
import io.github.novacrypto.bip39.wordlists.English;

public class MakeAdrActivity extends AppCompatActivity {
    EditText walletPass1;
    EditText walletPass2;
    EditText passPhrase;
    TextView showWords;
    TextView message;
    Button nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_adr);
       walletPass1=findViewById(R.id.walletPass1);
       walletPass2=findViewById(R.id.walletPass2);
        passPhrase=findViewById(R.id.passPhrase);
        showWords=findViewById(R.id.showWords);
        message=findViewById(R.id.message);
        nextButton=findViewById(R.id.nextButton);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextButton.setEnabled(false);
                makeAddress();

            }
        });

    }

    public void makeAddress() {

       String walletPass1S = walletPass1.getText().toString();
       String walletPass2S = walletPass2.getText().toString();
        String passPhraseS=passPhrase.getText().toString();

        if (!walletPass1.getText().toString().equals("") && walletPass1.getText().toString().equals(walletPass2.getText().toString())) {


            try {
                FileOutputStream fOut = this.openFileOutput("walletPass.txt", Context.MODE_PRIVATE);
                fOut.write(walletPass1S.getBytes());
                fOut.close();

                String filePath = getApplicationContext().getFilesDir().toString();


                //novacrypto 39
                StringBuilder sb = new StringBuilder();
                byte[] entropy = new byte[Words.TWENTY_FOUR.byteLength()];
                new SecureRandom().nextBytes(entropy);
                new MnemonicGenerator(English.INSTANCE)
                        .createMnemonic(entropy, sb::append);


                String fileName="";
                try {

                    DeterministicSeed seed2 =  new DeterministicSeed(sb.toString(), null, passPhraseS, 1409478661L);
                    DeterministicKeyChain chain = DeterministicKeyChain.builder().seed(seed2).build();
                    List<ChildNumber> keyPath = HDUtils.parsePath("M/44H/60H/0H/0/0");
                    DeterministicKey key2 = chain.getKeyByPath(keyPath, true);
                    BigInteger privKey = key2.getPrivKey();


                    //web3j
                    Credentials credentials = Credentials.create(privKey.toString(16));
                    ECKeyPair ecKeyPair =credentials.getEcKeyPair();

                    fileName= WalletUtils.generateWalletFile(walletPass1S,ecKeyPair,getApplicationContext().getFilesDir(),false);

                } catch (UnreadableWalletException e) {
                    e.printStackTrace();
                    nextButton.setEnabled(true);
                }


                File ff = new File(filePath + "/" + fileName);

                File newfile = new File(filePath + "/" + "address.json");

                if (ff.renameTo(newfile)) {

                } else {

                }


                FileOutputStream seed = this.openFileOutput("seedWords.txt", Context.MODE_PRIVATE);
                seed.write(sb.toString().getBytes());
                seed.close();

                FileOutputStream passf= this.openFileOutput("passPhrase.txt", Context.MODE_PRIVATE);
                passf.write(passPhraseS.getBytes());
                passf.close();

                //showSeed(sb.toString(),passPhraseS);
                showWords.setText(sb.toString());
                message.setText("Please write down your recovery MNEMONIC!");
                nextButton.setEnabled(true);
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MakeAdrActivity.this,Wallet.class);
                        startActivity(intent);


                    }
                });


            } catch (IOException e1) {
                nextButton.setEnabled(true);
                e1.printStackTrace();

            } catch (CipherException e) {
                nextButton.setEnabled(true);
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this.getApplicationContext(), "Input Error",
                    Toast.LENGTH_LONG).show();
            nextButton.setEnabled(true);

        }
    }
}
