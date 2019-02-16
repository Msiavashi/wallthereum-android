package com.wallthereum.wallthereum;

import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickNewWallet(View view) {
        TextInputEditText text = (TextInputEditText) findViewById(R.id.main_password_field);
        String password = text.getText().toString();

        if (password.isEmpty()){
            Toast.makeText(this, "password empty", Toast.LENGTH_SHORT).show();
            return;
        }

        final Wallet newWallet = new Wallet(password);
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
                                dialog.dismiss();
                                ProgressBar progressBar = findViewById(R.id.loading);
                                progressBar.setVisibility(View.VISIBLE);

                                if(isInternetConnected()){
                                    try {
                                        newWallet.create();
                                        Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                                        progressBar.setVisibility(View.GONE);
                                        startActivity(intent);
                                        Toast.makeText(MainActivity.this, "Created", Toast.LENGTH_SHORT).show();
                                    } catch (ConnectionException e) {
                                        Toast.makeText(MainActivity.this, "Network Problem", Toast.LENGTH_SHORT).show();
                                    }
                                }else {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                                }
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
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            return true;
        }
        else
            return false;
    }
}
