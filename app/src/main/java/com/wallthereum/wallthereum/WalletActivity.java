package com.wallthereum.wallthereum;

import androidx.core.app.ActivityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class WalletActivity extends BaseActivity implements View.OnClickListener {

    private static final int DIRECTORY_CHOOSER_REQUEST_CODE = 42;
    private static final String TAG = "Wallet Activity";
    private static final int EXTERNAL_STORAGE_WRITE_REQUEST_CODE = 1;
    private RecyclerView mTransactionsHistory;
    private TextView mEmptyTransaction;
    private static List<TransactionEntity> transactionsList = new ArrayList<>();
    private TransactionsAdapter mTransactionsAdapter;
    private TransactionModelView mTransactionModelView;
    public static final String[] longPressOptions = {getContext().getResources().getString(R.string.transaction_status), getContext().getResources().getString(R.string.transaction_delete)};

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

    private void initBalance() {
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
        } finally {
            loading.setVisibility(View.GONE);
        }
    }

    private void initFab() {
        FabOptions fabOptions = findViewById(R.id.fab_options);
        fabOptions.setButtonsMenu(R.menu.fab_menu);
        fabOptions.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        /*
            Handling wallet panel fab oprions items onClick events
         */
        switch (view.getId()) {
            case R.id.send_button:
                showTransactionDialog();
                break;
            case R.id.receive_button:
                showAddressDialog();
                break;
            case R.id.keystore_button:
                saveKeystoreAs();
                break;
            case R.id.private_key_button:
                showPKDialog();
                break;
            default:
                break;
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (resultCode == RESULT_OK) {
            Uri treeUri = resultData.getData();
            String dstPath = StorageHelper.getFullPathFromTreeUri(treeUri, getContext());
            Log.d(TAG,    dstPath + "/" + new File(Wallet.getWallet().getCurrentKeystoreAddress()).getName());
            Log.d(TAG, Wallet.getWallet().getCurrentKeystoreAddress());
            try {
                StorageHelper.copyFileOrDirectory(Wallet.getWallet().getCurrentKeystoreAddress(),  dstPath);
                Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            }catch (Exception e){
                Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
            }
            // List all existing files inside picked directory
//            for (DocumentFile file : pickedDir.listFiles()) {
//                Log.d(TAG, "Found file " + file.getName() + " with size " + file.length());
//            }

            // Create a new file and write into it
//            DocumentFile newFile = pickedDir.createFile("text/plain", "My Novel");
//            OutputStream out = getContentResolver().openOutputStream(newFile.getUri());
//            out.write("A long time ago...".getBytes());
//            out.close();
        }
    }

    private void saveKeystoreAs() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(TAG,"Permission is granted");
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                startActivityForResult(intent, DIRECTORY_CHOOSER_REQUEST_CODE);
                return;
            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, EXTERNAL_STORAGE_WRITE_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRestart()
    {
        super.onRestart();
        initTransactionsHistory();
        // do some stuff here
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
        editText.setBackgroundTintList(getResources().getColorStateList(R.color.black));
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.address)
                .setView(editText)
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
        editText.setBackgroundTintList(getResources().getColorStateList(R.color.black));
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(R.string.private_key_dialog_title)
                .setView(editText)
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
                        mTransactionsAdapter = new TransactionsAdapter(transactionsList);
                        mTransactionModelView = ViewModelProviders.of(WalletActivity.this).get(TransactionModelView.class);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
                        mTransactionsHistory.setLayoutManager(layoutManager);
                        mTransactionsHistory.setItemAnimator(new DefaultItemAnimator());
                        mTransactionsHistory.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
                        mTransactionsHistory.setAdapter(mTransactionsAdapter);
                        mTransactionModelView.getAllTransactions().observe(WalletActivity.this, new Observer<List<TransactionEntity>>() {
                            @Override
                            public void onChanged(List<TransactionEntity> transactionEntities) {
                                mTransactionsAdapter.setData(transactionEntities);
                                if (transactionEntities == null || transactionsList.isEmpty()){
                                    mEmptyTransaction.setVisibility(View.VISIBLE);
                                    mTransactionsHistory.setVisibility(View.GONE);
                                }else {
                                    mEmptyTransaction.setVisibility(View.GONE);
                                    mTransactionsHistory.setVisibility(View.VISIBLE);
                                }
                            }
                        });
                    }
                });
            }
        });
    }
}
