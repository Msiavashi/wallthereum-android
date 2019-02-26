package com.wallthereum.wallthereum;

import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.web3j.crypto.CipherException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickNewWallet(View view) {
        TextInputEditText text = (TextInputEditText) findViewById(R.id.main_password_field);
        final String password = text.getText().toString();

        if (password.isEmpty()){
            Toast.makeText(this, "password empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Wallet wallet = Wallet.getWallet();
        DialogPlus dialog = DialogPlus.newDialog(this)
                .setGravity(Gravity.CENTER)
                .setContentHolder(new ViewHolder(R.layout.new_wallet_alert))
                .setCancelable(true)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {

                        switch (view.getId()){
                            case R.id.reject_button:
                                dialog.dismiss();
                                break;
                            case R.id.accept_button:
                                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                                dialog.dismiss();

                                AsyncTask.execute(new Runnable() {
                                   @Override
                                   public void run() {
                                      //TODO your background code
                                       if(isInternetConnected()){
                                           try {
                                               String filename = wallet.create(password);
                                               wallet.unlockWallet(filename, password);
                                               Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                                               startActivity(intent);

                                               runOnUiThread(new Runnable() {
                                                   @Override
                                                   public void run() {
                                                       findViewById(R.id.loading).setVisibility(View.GONE);
                                                       Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
                                                   }
                                               });

                                           } catch (ConnectionException e) {
                                               Toast.makeText(MainActivity.this, "Network Problem", Toast.LENGTH_SHORT).show();
                                           } catch (IOException e) {
                                               e.printStackTrace();
                                           } catch (NoSuchAlgorithmException e) {
                                               e.printStackTrace();
                                           } catch (InvalidAlgorithmParameterException e) {
                                               e.printStackTrace();
                                           } catch (NoSuchProviderException e) {
                                               e.printStackTrace();
                                           } catch (CipherException e) {
                                               e.printStackTrace();
                                           }
                                       }else {
                                           runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                   findViewById(R.id.loading).setVisibility(View.GONE);
                                                   Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                               }
                                           });
                                       }
                                   }
                                });
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private boolean isInternetConnected(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED){
            return true;
        }
        if( connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            return true;
        }
        else
            return false;
    }
}
