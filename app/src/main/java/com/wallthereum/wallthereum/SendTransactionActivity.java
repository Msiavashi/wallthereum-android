package com.wallthereum.wallthereum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

public class SendTransactionActivity extends AppCompatActivity {
    private static Context mContext;
    private HashMap<String, BigDecimal> mGasSpinnerData;
    private final String TAG = "SendTransactionActivity";
    private EditText mGasLimitTextEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_transaction);
        this.mContext = SendTransactionActivity.this;
        this.mGasLimitTextEdit = findViewById(R.id.gas_limit_text_input);
        findViewById(R.id.transaction_loading).setVisibility(View.VISIBLE);
        setGasLimitTextChangeListener();
        initToolbar();
        initGasPrice();
    }

    private void setGasLimitTextChangeListener() {
        this.mGasLimitTextEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()){
                    mGasLimitTextEdit.setError(getResources().getString(R.string.gas_limit_error));
                } else if (new BigInteger(s.toString()).compareTo(BigInteger.valueOf(21000)) < 0){
                    mGasLimitTextEdit.setError(getResources().getString(R.string.gas_limit_error));
                }else {
                    updateTxnGasInformation();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public static Context getContext(){
        return mContext;
    }

    private void initGasPrice() {
        Spinner spinner = findViewById(R.id.gas_price_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                // TODO Auto-generated method stub
                String item=spinner.getSelectedItem().toString();
                updateTxnGasInformation();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String response = Network.getNetwork().retrieveGasPrice();
                    JSONObject gasObject = new JSONObject(response);
                    updateSpinnerOnGasPriceReceived(gasObject);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    Log.d(TAG, "json convertion error");
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateTxnGasInformation() {
        /*update gas price text view*/
        Spinner spinner = findViewById(R.id.gas_price_spinner);
        String selected = (String) spinner.getSelectedItem();
        BigDecimal price = this.mGasSpinnerData.get(selected);
        TextView textView = findViewById(R.id.gas_price_text_view);
        textView.setText(getResources().getString(R.string.gas_price )+ ": " + price + " (Gwei)");

        /*update transaction fee text view*/
        textView = findViewById(R.id.transaction_fee_text_view);
        EditText editText = findViewById(R.id.gas_limit_text_input);
        BigInteger limit = new BigInteger(editText.getText().toString());
        BigDecimal dFee = new BigDecimal("0");
        if(!editText.getText().toString().isEmpty()){
            dFee = price.multiply(new BigDecimal(limit));
        }
        BigDecimal feeWei = Convert.toWei(dFee, Convert.Unit.GWEI);
        BigDecimal fee = Convert.fromWei(feeWei, Convert.Unit.ETHER);
        if(fee.compareTo(BigDecimal.ZERO) == 0){
            textView.setText("___");
        }else {
            textView.setText(getResources().getString(R.string.transaction_fee) + ": " + fee + " Ether");
        }
    }

    private void updateSpinnerOnGasPriceReceived(JSONObject jsonObject) {
        this.mGasSpinnerData = new HashMap<>();
        try {
            this.mGasSpinnerData.put("fastest", new BigDecimal(jsonObject.getString("fastest")).divide(new BigDecimal("10")));
            this.mGasSpinnerData.put("fast", new BigDecimal(jsonObject.getString("fast")).divide(new BigDecimal("10")));
            this.mGasSpinnerData.put("average", new BigDecimal(jsonObject.getString("average")).divide(new BigDecimal("10")));
            this.mGasSpinnerData.put("safe low", new BigDecimal(jsonObject.getString("safeLow")).divide(new BigDecimal("10")));
            String[] spinnerData = (String[]) this.mGasSpinnerData.keySet().toArray(new String[0]);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, spinnerData);
            adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Spinner spinner = findViewById(R.id.gas_price_spinner);
                    spinner.setAdapter(adapter);
                    findViewById(R.id.transaction_loading).setVisibility(View.GONE);
                    updateTxnGasInformation();
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

    public void onCreateTransactionClicked(View view) {

    }
}
