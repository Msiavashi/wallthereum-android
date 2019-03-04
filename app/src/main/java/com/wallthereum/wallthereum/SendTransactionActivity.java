package com.wallthereum.wallthereum;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SendTransactionActivity extends AppCompatActivity {
    private static Context mContext;
    private JSONObject mGasObject;
    private final String TAG = "SendTransactionActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_transaction);
        this.mContext = SendTransactionActivity.this;
        findViewById(R.id.transaction_loading).setVisibility(View.VISIBLE);
        initToolbar();
        initGasPrice();
    }

    public static Context getContext(){
        return mContext;
    }

    private void initGasPrice() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = Network.getNetwork().retrieveGasPrice();
                    JSONObject jsonObject = new JSONObject(response);
                    updateSpinnerOnGasPriceReceived(jsonObject);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.d(TAG, "json convertion error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateSpinnerOnGasPriceReceived(JSONObject jsonObject) {
        this.mGasObject = jsonObject;
        try {
            String[] prices = new String[] {
                    "fastest (" +  Double.toString((Double.parseDouble(jsonObject.getString("fastest")) / 10.0)) + " Gwei)",
                    "fast (" +  Double.toString((Double.parseDouble(jsonObject.getString("fast")) / 10.0)) + " Gwei)",
                    "average (" +  Double.toString((Double.parseDouble(jsonObject.getString("average")) / 10.0)) + " Gwei)",
                    "safeLow (" +  Double.toString((Double.parseDouble(jsonObject.getString("safeLow")) / 10.0)) + " Gwei)",
            };
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, prices);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Spinner spinner = (Spinner)  findViewById(R.id.gas_price_spinner);
                    spinner.setAdapter(adapter);
                    findViewById(R.id.transaction_loading).setVisibility(View.GONE);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initToolbar(){
        Toolbar mToolbar = findViewById(R.id.transaction_boolbar);
        mToolbar.setTitle(R.string.balance);
        try {
            mToolbar.setSubtitle(Wallet.getWallet().getBalance().toString() + " ETHER(s)");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

}
