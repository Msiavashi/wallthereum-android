package com.wallthereum.wallthereum;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.joaquimley.faboptions.FabOptions;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionDB;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;
import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;
import org.web3j.utils.Convert;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WalletActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "Wallet Activity";
    private RecyclerView mTransactionsHistory;
    private TextView mEmptyTransaction;
    private static List<TransactionEntity> transactionsList;
    private TransactionsAdapter mTransactionsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        this.mTransactionsHistory = findViewById(R.id.transactions_history);
        this.mEmptyTransaction = findViewById(R.id.empty_transactions);
        this.initTransactionsHistory();
        this.initFab();
        this.initBalance();
    }

    private void initBalance(){
//        set loading
        ProgressBar loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);

        try {
            getSupportActionBar().setTitle(R.string.balance);
            getSupportActionBar().setSubtitle(Convert.fromWei(Wallet.getWallet().getBalance().toString(), Convert.Unit.ETHER) + " ETHER(s)");
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
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
        if(!Network.getNetwork().isInternetConnected(this)){
            Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
            return;
        }
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
        editText.setText(Wallet.getWallet().getEcKeyPair().getPrivateKey().toString(16));
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
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                transactionsList = TransactionDB.getTransactionDB(getContext()).transactionDAO().getWalletTransactions(Wallet.getWallet().getAddress());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (transactionsList.isEmpty()){
                            mEmptyTransaction.setVisibility(View.VISIBLE);
                            mTransactionsHistory.setVisibility(View.GONE);
                        }else {
                            mEmptyTransaction.setVisibility(View.GONE);
                            mTransactionsHistory.setVisibility(View.VISIBLE);
                            mTransactionsAdapter = new TransactionsAdapter(transactionsList);
                            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                            mTransactionsHistory.setLayoutManager(layoutManager);
                            mTransactionsHistory.setItemAnimator(new DefaultItemAnimator());
                            mTransactionsHistory.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                            mTransactionsHistory.setAdapter(mTransactionsAdapter);
                        }
                    }
                });
            }
        });
    }
}
