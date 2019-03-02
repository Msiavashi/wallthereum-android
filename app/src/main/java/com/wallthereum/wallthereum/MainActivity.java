package com.wallthereum.wallthereum;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.web3j.crypto.CipherException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import androidx.appcompat.app.AlertDialog;

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
                                createWallet(password);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private void createWallet(final String password){
        findViewById(R.id.loading).setVisibility(View.VISIBLE);
        final Wallet wallet = Wallet.getWallet();
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                //TODO your background code
                if(isInternetConnected()){
                    try {
                        String path = wallet.create(password);
                        wallet.unlockWallet(path, password);
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

    public void onUnlockExistingWalletClicked(View view) {
        final String[] options = {getResources().getString(R.string.unlock_with_pk), getResources().getString(R.string.unlock_with_keystore), getResources().getString(R.string.sync)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.unlock_dialog_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:     //options[0]
                        break;
                    case 1:     //options[1]
                        getKeystoreFile();
                        break;
                    case 2:     //options[2]
                        break;
                    default:
                        break;
                }
            }
        });
        builder.show();
    }

    private void getKeystoreFile(){
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(getFilesDir().getPath());
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.offset = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"json"};
        final FilePickerDialog filePickerDialog = new FilePickerDialog(MainActivity.this,properties);
        filePickerDialog.setTitle(R.string.select_file);
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                unlockKeystore(files[0]);
            }
        });
        filePickerDialog.show();

    }

    private void unlockKeystore(final String filePath) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edittext.setHint(R.string.hint_password);
        alert.setTitle(R.string.hint_password);
        alert.setView(edittext);
        alert.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                final String password = edittext.getText().toString();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Wallet.getWallet().unlockWallet(filePath, password);
                            Log.d(TAG, "salam");
                            Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                            startActivity(intent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.loading).setVisibility(View.GONE);
                                    Toast.makeText(MainActivity.this, R.string.wallet_unlock_success, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (CipherException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Cipher Errror", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        alert.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // what ever you want to do with No option.
                dialog.cancel();
            }
        });

        alert.show();
    }
}
