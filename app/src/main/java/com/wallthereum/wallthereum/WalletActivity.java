package com.wallthereum.wallthereum;

import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.joaquimley.faboptions.FabOptions;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.concurrent.ExecutionException;

public class WalletActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "Wallet Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

//        set elevation of actionbar to 0
        this.getSupportActionBar().setElevation(0);
        this.initTransactionsHistory();
        this.initFab();
        this.initBalance();
    }

    private void initBalance(){
//        set loading
        ProgressBar loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        try {
            BigInteger wei = Wallet.getWallet().getBalance();
            BigDecimal ether = Convert.fromWei(wei.toString(), Convert.Unit.ETHER);        //converting wei to ether
            TextView balanceView = (TextView) findViewById(R.id.balance_view);
            balanceView.setText(ether + " Ether(s)");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
//            dismiss loading
            loading.setVisibility(View.GONE);
        }
    }

    private void initFab() {
        FabOptions fabOptions = findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(R.menu.fab_menu);
        fabOptions.setOnClickListener(this);
    }


    @Override
    public void onClick(View view){
        /*
            Handling wallet panel fab oprions items onClick events
         */
        switch (view.getId()){
            case R.id.send_button:
                showTransactionDialog();
                break;
            case R.id.receive_button:
                showAddressDialog();
                break;
            case R.id.keystore_button:
                break;
            case R.id.private_key_button:
                showPKDialog();
                break;
            default:
                break;
        }
    }

    private void showTransactionDialog() {
        Intent intent = new Intent(WalletActivity.this, SendTransactionActivity.class);
        startActivity(intent);
    }

    private void showToast(String message){
        Toast.makeText(WalletActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private void showAddressDialog() {
        final EditText editText = new EditText(this);
        editText.setText(Wallet.getWallet().getAddress().replace("0x", ""));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.address)
                .setView(editText)
                .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        copyToClipBoard(Wallet.getWallet().getAddress(), "address");
                        showToast(getResources().getString(R.string.copied));
                    }
                });
        builder.show();
    }

    private void showPKDialog() {
        final EditText editText = new EditText(this);
        editText.setText(Wallet.getWallet().getEcKeyPair().getPrivateKey().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle(R.string.private_key_dialog_title)
                .setView(editText)
                .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton(R.string.copy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
                        showToast(getResources().getString(R.string.copied));
                        copyToClipBoard(editText.getText().toString(), "pk");
                    }
                });
        builder.show();
    }

    private void copyToClipBoard(String data, String label) {
        final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(label, data);
        clipboardManager.setPrimaryClip(clipData);
    }

    private void initTransactionsHistory(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.transactions_history);
        TextView textView = (TextView) findViewById(R.id.empty_transactions);
        recyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

}
