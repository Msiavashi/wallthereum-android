package com.wallthereum.wallthereum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionDB;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;
import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.json.JSONException;
import org.json.JSONObject;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class SendTransactionActivity extends AppCompatActivity {
    private static Context mContext;
    private HashMap<String, BigDecimal> mGasSpinnerData;
    private final String TAG = "SendTransactionActivity";
    private EditText mGasLimitTextEdit;
    private BigInteger mGasLimit;
    private BigDecimal mGasPrice;
    private BigDecimal mTransactionFee;
    private String mAmount;
    private String mReceiverAddress;
    private CheckBox mSendAgreementCheckbox;
    private Button mSendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        setContentView(R.layout.activity_send_transaction);
        this.mContext = SendTransactionActivity.this;
        this.mGasLimitTextEdit = findViewById(R.id.gas_limit_text_input);
        findViewById(R.id.transaction_loading).setVisibility(View.VISIBLE);
        setGasLimitTextChangeListener();
        initToolbar();
        initGasPrice();
    }

    private void initSendAgreementCheckListener() {
        this.mSendAgreementCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    mSendButton.setEnabled(true);
                else
                    mSendButton.setEnabled(false);
            }
        });
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
                    e.printStackTrace();
                }
            }
        });
    }

    public static BigDecimal transactionFee(BigDecimal gasPriceGwei, BigInteger gasLimit){
        BigDecimal dFeeGwei = gasPriceGwei.multiply(new BigDecimal(gasLimit));
        BigDecimal feeWei = Convert.toWei(dFeeGwei, Convert.Unit.GWEI);
        BigDecimal feeEther = Convert.fromWei(feeWei, Convert.Unit.ETHER);
        return feeEther;
    }

    private void updateTxnGasInformation() {
        /*update gas price text view*/
        Spinner spinner = findViewById(R.id.gas_price_spinner);
        String selected = (String) spinner.getSelectedItem();
        this.mGasPrice = this.mGasSpinnerData.get(selected);
        TextView textView = findViewById(R.id.gas_price_text_view);
        textView.setText(getResources().getString(R.string.gas_price )+ ": " + this.mGasPrice + " (Gwei)");

        /*update transaction fee text view*/
        textView = findViewById(R.id.transaction_fee_text_view);
        EditText editText = findViewById(R.id.gas_limit_text_input);
        String limit = editText.getText().toString().isEmpty() ? null : editText.getText().toString();
        try {
            this.mGasLimit = new BigInteger(limit);
            this.mTransactionFee = transactionFee(this.mGasPrice, this.mGasLimit);
            textView.setText(getResources().getString(R.string.transaction_fee) + ": " + this.mTransactionFee + " Ether");
        }catch (Exception e){
            textView.setText("___");
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
        Toolbar mToolbar = findViewById(R.id.transaction_toolbar);
        mToolbar.setTitle(R.string.balance);
        try {
            mToolbar.setSubtitle(Convert.fromWei(Wallet.getWallet().getBalance().toString(), Convert.Unit.ETHER) + " ETHER(s)");
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
        EditText receiverAddressEditText = findViewById(R.id.receiver_address);
        if (!WalletUtils.isValidAddress(receiverAddressEditText.getText().toString())){
            Toast.makeText(mContext, R.string.invalid_address, Toast.LENGTH_SHORT).show();
            receiverAddressEditText.setError(getResources().getString(R.string.invalid_address));
            return;
        }
        String transactionReceipt = getResources().getString(R.string.sender) + ": " + Wallet.getWallet().getAddress() + "\n" +
                getResources().getString(R.string.receier) + ": " + receiverAddressEditText.getText().toString() + "\n" +
                getResources().getString(R.string.amount) + ": " + ((EditText) findViewById(R.id.amount_input)).getText().toString() + "Ether" + "\n" +
                getResources().getString(R.string.estimated_fee) + ": " + this.mTransactionFee + "\n";
        this.mAmount = ((EditText)findViewById(R.id.amount_input)).getText().toString();
        this.mReceiverAddress = ((EditText)findViewById(R.id.receiver_address)).getText().toString();
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setCancelable(false)
                .setContentHolder(new ViewHolder(R.layout.send_transaction_warning_dialog))
                .setGravity(Gravity.CENTER)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.send_button:
                                if(!Network.getNetwork().isInternetConnected(mContext)){
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(SendTransactionActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                    return;
                                }
                                findViewById(R.id.transaction_loading).setVisibility(View.VISIBLE);
                                dialog.dismiss();
                                Toast.makeText(SendTransactionActivity.this, R.string.sending_transaction, Toast.LENGTH_LONG).show();
                                sendTransaction();
                                break;
                            case R.id.reject_transaction_button:
                                dialog.dismiss();
                                break;
                            default:
                                break;
                        }
                    }
                }).create();

        dialogPlus.show();
        this.mSendButton = findViewById(R.id.send_button);
        this.mSendAgreementCheckbox = findViewById(R.id.agreement_checkbox);
        initSendAgreementCheckListener();
        TextView textView = findViewById(R.id.receipt_text_view);
        textView.setText(transactionReceipt);
    }

    private void sendTransaction() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    TransactionReceipt transactionReceipt = Network.getNetwork().sendTransaction(Convert.toWei(mAmount, Convert.Unit.ETHER).toBigInteger(),
                            mReceiverAddress,
                            Wallet.getWallet().getAddress(),
                            mGasLimit,
                            mGasPrice.toBigInteger());
                    saveTransactionToDB(transactionReceipt, mAmount, mReceiverAddress, mGasLimit, mGasPrice);
                } catch (ExecutionException e) {
                    onTransactionFailed();
                } catch (InterruptedException e) {
                    onTransactionFailed();
                }
            }
        });
    }

    private void onTransactionSuccessful() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onBackPressed();
                findViewById(R.id.transaction_loading).setVisibility(View.GONE);
                Toast.makeText(SendTransactionActivity.this, R.string.transaction_sent_toast, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void onTransactionFailed(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.transaction_loading).setVisibility(View.GONE);
                Toast.makeText(SendTransactionActivity.this, R.string.transaction_failed_toast, Toast.LENGTH_LONG).show();
            }
        });

    }

    private void saveTransactionToDB(TransactionReceipt transactionReceipt, String amount, String receiverAddress, BigInteger gasLimit, BigDecimal gasPrice) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                TransactionEntity transactionEntity = new TransactionEntity();
                transactionEntity.transactionHash = transactionReceipt.getTransactionHash();
                transactionEntity.createdAt = new Date();
                transactionEntity.amount = amount;
                transactionEntity.receiverAddress = receiverAddress;
                transactionEntity.gasLimit = gasLimit.toString();
                transactionEntity.gasPrice = gasPrice.toString();
                transactionEntity.senderAddress = Wallet.getWallet().getAddress();
                TransactionDB.getTransactionDB(SendTransactionActivity.getContext()).transactionDAO().insertSingleTransaction(transactionEntity);
                onTransactionSuccessful();
            }
        });
    }
}
