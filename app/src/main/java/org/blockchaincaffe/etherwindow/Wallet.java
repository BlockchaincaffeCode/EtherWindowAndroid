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
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ceylonlabs.imageviewpopup.ImagePopup;
import com.google.zxing.WriterException;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.WalletUtils;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;

public class Wallet extends AppCompatActivity {
    static Credentials credentials = null;
    String password= "";
    String addr= "";
    TextView address;
    TextView showGas;
    public static EditText receiver;
    public static EditText nonce;
    public static EditText gasLimit;
    public static EditText sum;
    public static SeekBar seekBar;
    Button send;
    Button scan;
    ImageView qrcode;
    ImageView info;
    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        address=findViewById(R.id.address);
        qrcode=(ImageView)findViewById(R.id.qrcode);
        info=(ImageView)findViewById(R.id.info);
        receiver=findViewById(R.id.receiver);
        nonce=findViewById(R.id.nonce);
        gasLimit=findViewById(R.id.gasLimit);
        sum=findViewById(R.id.sum);
        seekBar=findViewById(R.id.seekBar);
        send=findViewById(R.id.send);
        scan=findViewById(R.id.scan);
        showGas=findViewById(R.id.showGas);

        final ClipboardManager clipboard = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        address.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String copy = address
                        .getText().toString();
                final ClipData clip = ClipData.newPlainText("simple text", copy);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getBaseContext(),"Address copied",Toast.LENGTH_SHORT).show();

            }
        });

        ImagePopup imagePopup = new ImagePopup(this);
        ImageView imagePop = new ImageView(this);
        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bitmap=BitmapHelper.TextToImageEncode(addr);

                    imagePop.setImageBitmap(bitmap);
                    imagePopup.initiatePopup(imagePop.getDrawable());
                    imagePopup.viewPopup();

                } catch (WriterException e) {

                }

            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(Wallet.this);
                View view = li.inflate(R.layout.info, null);

                final EditText walletPassword = view.findViewById(R.id.walletPassword);


                new AlertDialog.Builder(Wallet.this, R.style.dialogStyle)
                        .setTitle("Ether Window")
                        .setMessage("The wallet is completely offline. You do not need internet, bluetooth or cable. The wallet communicates via qr codes and is therefore the most secure in the world. You do not need the internet to generate a signed transaction, and you can send it via QR code on: \n\n www.etherwindow.com.\n\nTake your old phone, turn off the internet and have the most secure hardware wallet in the world.")
                        .setView(view)
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {

                                File walletPass = new File(getApplicationContext().getFilesDir(),"walletPass.txt");
                                File seedWords = new File(getApplicationContext().getFilesDir(),"seedWords.txt");
                                File passPhrase = new File(getApplicationContext().getFilesDir(),"passPhrase.txt");
                                String psw="";
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(walletPass));
                                    psw= br.readLine();
                                    br.close();
                                }
                                catch (IOException e) {

                                }

                                String phr="";
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(passPhrase));
                                    phr= br.readLine();
                                    br.close();
                                }
                                catch (IOException e) {

                                }


                                String seed="";
                                try {
                                    BufferedReader br = new BufferedReader(new FileReader(seedWords));
                                    seed= br.readLine();
                                    br.close();
                                }
                                catch (IOException e) {

                                }
                                seed=seed.trim();

                                if(psw.equals(walletPassword.getText().toString())){


                                    LayoutInflater li = LayoutInflater.from(Wallet.this);
                                    View view2 = li.inflate(R.layout.infoprivate, null);
                                    final ImageView seedQr = view2.findViewById(R.id.seedQr);
                                    try {
                                        Bitmap bitmap=BitmapHelper.TextToImageEncode(seed);
                                        seedQr.setImageBitmap(bitmap);
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }
                                    final TextView seedText = view2.findViewById(R.id.seedText);
                                    seedText.setText(seed);
                                    final ClipboardManager clipboard = (ClipboardManager)
                                            getSystemService(Context.CLIPBOARD_SERVICE);
                                    seedText.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String copy = address
                                                    .getText().toString();
                                            final ClipData clip = ClipData.newPlainText("simple text", copy);
                                            clipboard.setPrimaryClip(clip);
                                            Toast.makeText(getBaseContext(),"Mnemonic copied",Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    final TextView privateKey = view2.findViewById(R.id.privateKey);
                                    privateKey.setText(credentials.getEcKeyPair().getPrivateKey().toString(16));

                                    privateKey.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                            String copy = address
                                                    .getText().toString();
                                            final ClipData clip = ClipData.newPlainText("simple text", copy);
                                            clipboard.setPrimaryClip(clip);
                                            Toast.makeText(getBaseContext(),"Private key copied",Toast.LENGTH_SHORT).show();

                                        }
                                    });

                                    new AlertDialog.Builder(Wallet.this, R.style.dialogStyle)
                                            .setTitle("Your recovery MNEMONIC")

                                            .setView(view2)
                                            .setCancelable(false)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {

                                                }
                                            })
                                            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                }
                                            })
                                            .show();


                                }


                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        })
                        .show();

            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
        @Override
         public void onClick(View v) {
            Intent intent = new Intent(Wallet.this,QR.class);
            startActivity(intent);

            }
         });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rawTransaction();

            }
        });


        try {
            FileInputStream in = openFileInput("walletPass.txt");
            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            password= br.readLine().trim();
            cred();

        } catch (Exception e) {

        }



        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBar.setMax(150);



    }


    SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            String ee="";
            try {
                DecimalFormat df = new DecimalFormat("0.000000");
                ee=df.format(progress * 0.000021);
                showGas.setText("Fee  "+progress+" Gwei    "+ee+" ETH");
            }catch(Exception e){
                try {
                    showGas.setText("Fee  " + progress + " Gwei    " + progress * 0.000021 + " ETH");
                }catch (Exception de){}
            }
            ;

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    public void cred(){

        try {
            credentials = WalletUtils.loadCredentials(
                    password,
                    getApplicationContext().getFilesDir().toString()+"/"+"address.json");
            address.setText(credentials.getAddress().toString());
            addr=credentials.getAddress().toString().trim();

        } catch (IOException e) {

        } catch (CipherException e) {

        }

    }

    public void rawTransaction(){

        if(seekBar.getProgress()==0){
            seekBar.setProgress(1);
        }

        String rec=receiver.getText().toString().trim();

        if(WalletUtils.isValidAddress(rec) && !nonce.getText().toString().equals("") && !gasLimit.getText().toString().equals("") && !sum.getText().toString().equals("")){
                AlertDialog.Builder builder=new AlertDialog.Builder(Wallet.this, R.style.dialogStyle);
                builder.setTitle("Sign transaction?");
                builder.setMessage("Send "+sum.getText().toString()+" eth to address: "+rec);
                builder.setCancelable(false);
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        try {
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);}
                        catch (Exception e) {
                        }

                        if (sum.getText().toString().length()>0) {

                            Long feeLong = seekBar.getProgress() * 1000000000L;
                            //Long feeLong = seekBar.getProgress() * 100000L;
                            BigInteger fee = BigInteger.valueOf(feeLong.longValue());

                            BigDecimal df = Convert.toWei(new BigDecimal(sum.getText().toString()), Convert.Unit.ETHER);
                            BigInteger sendSum = df.toBigInteger();
                            BigInteger noncet = new BigInteger(nonce.getText().toString());
                            BigInteger gasLimitt=BigInteger.valueOf(Integer.parseInt(gasLimit.getText().toString()));
                            RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                                    noncet, fee, gasLimitt, rec, sendSum);

                            byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
                            String hexValue = Numeric.toHexString(signedMessage);

                            ImagePopup imagePopup = new ImagePopup(Wallet.this);
                            ImageView imagePop = new ImageView(Wallet.this);
                            Bitmap bitmap= null;
                            try {
                                bitmap = BitmapHelper.TextToImageEncode(hexValue);
                            } catch (WriterException e) {
                                e.printStackTrace();
                            }

                            imagePop.setImageBitmap(bitmap);
                            imagePopup.initiatePopup(imagePop.getDrawable());
                            imagePopup.viewPopup();

                            nonce.setText("");
                            receiver.setText("");
                            sum.setText("");
                            gasLimit.setText("");
                            seekBar.setProgress(1);
                        }


                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });



                AlertDialog alertDialog=builder.create();
                alertDialog.show();
            } else{Toast.makeText(Wallet.this, "Transaction input error", Toast.LENGTH_SHORT).show();}




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