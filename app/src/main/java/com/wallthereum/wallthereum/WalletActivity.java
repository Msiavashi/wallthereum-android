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
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.joaquimley.faboptions.FabOptions;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionDB;
import com.wallthereum.wallthereum.coin.Ethereum.DataBase.TransactionEntity;
import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.web3j.crypto.CipherException;
import org.web3j.utils.Convert;

import java.io.File;
import java.io.IOException;
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
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        this.mTransactionsHistory = findViewById(R.id.transactions_history);
        this.mEmptyTransaction = findViewById(R.id.empty_transactions);
        this.initTransactionsHistory();
        this.initFab();
        this.initBalance();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            if(extras.getBoolean("new_wallet")){
                showNewWalletDialog();
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to logout", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
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
            grantUriPermission(getPackageName(), treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            getContentResolver().takePersistableUriPermission(treeUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            try {

                if(Wallet.getWallet().getCurrentKeystoreAddress() == null){
//                    the wallet unlocked with a private key
                    createWalletForPK(dstPath);
                }else{
//                    file unlocked with a keystore
                    StorageHelper.copyFileOrDirectory(Wallet.getWallet().getCurrentKeystoreAddress(),  dstPath);
                    File file = new File(dstPath + File.separator + new File(Wallet.getWallet().getCurrentKeystoreAddress()).getName());
                    if (file.exists()){
                        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }catch (Exception e){
                Log.d(TAG, e.getMessage());
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

    private void createWalletForPK(String dstPath) {
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edittext.setBackgroundTintList(getResources().getColorStateList(R.color.black));
        edittext.setHint(R.string.hint_password);
        edittext.setGravity(Gravity.LEFT);                  //TODO: remove this for RTL support
        edittext.setTextDirection(View.TEXT_DIRECTION_LTR);     //TODO: remove this for RTL
        AlertDialog alert = new AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle(R.string.hint_password)
            .setView(edittext)
            .setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Toast.makeText(WalletActivity.this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                }
            })
            .setPositiveButton(R.string.accept, null)
            .create();
        alert.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button button = ((AlertDialog) alert).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(edittext.getText().toString().length() < MainActivity.mMinimumPasswordLength){
                            edittext.setError(getResources().getString(R.string.password_length_input_error));
                        }else {
                            try {
                                Wallet.getWallet().createAndSaveWalletFromPK(dstPath, edittext.getText().toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WalletActivity.this, R.string.saved, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } catch (IOException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WalletActivity.this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (CipherException e) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WalletActivity.this, R.string.save_failed, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }finally {
                                dialog.dismiss();
                            }
                        }
                    }
                });
            }
        });
        alert.show();
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

    public void showNewWalletDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(WalletActivity.getContext(), R.style.AlertDialogTheme)
                .setTitle(R.string.read_carefully)
                .setMessage(R.string.created_toast)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false);
        builder.show();
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
