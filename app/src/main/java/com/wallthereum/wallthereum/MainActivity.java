package com.wallthereum.wallthereum;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;
import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.ViewHolder;
import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.Exceptions.InvalidPKException;
import com.wallthereum.wallthereum.coin.Ethereum.Network;
import com.wallthereum.wallthereum.coin.Ethereum.Wallet;

import org.web3j.crypto.CipherException;

import java.io.File;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import androidx.appcompat.app.AlertDialog;

public class MainActivity extends BaseActivity {

    private final String TAG = "MainActivity";
    private TextInputEditText mPasswordInput;
    private int mMinimumPasswordLength = 8;
    public static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        this.mPasswordInput = findViewById(R.id.main_password_field);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        recreate();
    }

    public void onClickNewWallet(View view) {
        final String password = mPasswordInput.getText().toString();
        if (password.isEmpty()){
//            Toast.makeText(this, R.string.empty_password, Toast.LENGTH_SHORT).show();
            mPasswordInput.setError(getResources().getString(R.string.empty_password_error));
            return;
        }else if (password.length() < mMinimumPasswordLength){
//            Toast.makeText(this, R.string.password_length_error_toast, Toast.LENGTH_SHORT).show();
            mPasswordInput.setError(getResources().getString(R.string.password_length_input_error));
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
                if(Network.getNetwork().isInternetConnected(mContext)){
                    try {
                        String path = wallet.create(password);
                        wallet.unlockKeystore(path, password);
                        Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                        intent.putExtra("new_wallet", true);
                        startActivity(intent);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                findViewById(R.id.loading).setVisibility(View.GONE);
                            }
                        });

                    } catch (ConnectionException e) {
                        Toast.makeText(MainActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Toast.makeText(MainActivity.this, R.string.file_error_toast, Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(MainActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }


    public void onUnlockExistingWalletClicked(View view) {
        final String[] options = {getResources().getString(R.string.unlock_with_pk), getResources().getString(R.string.unlock_with_keystore)};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.unlock_dialog_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:     //options[0]
                        unlockWithPK();
                        break;
                    case 1:     //options[1]
                        promptStorageSelection();
                        break;
                    default:
                        break;
                }
            }
        });

        builder.show();
    }

    private void unlockWithPK() {
        if(!Network.getNetwork().isInternetConnected(mContext)){
            Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        final EditText edittext = new EditText(this);
        edittext.setBackgroundTintList(getResources().getColorStateList(R.color.black));
        edittext.setInputType(InputType.TYPE_CLASS_TEXT);
        edittext.setHint(R.string.hint_private_key);
        alert.setTitle(R.string.private_key_dialog_title);
        alert.setView(edittext);
        alert.setPositiveButton(R.string.accept, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                findViewById(R.id.loading).setVisibility(View.VISIBLE);
                final String pk = edittext.getText().toString();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Wallet.getWallet().unlockPrivateKey(pk);
                            Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                            startActivity(intent);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.wallet_unlock_success, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (ConnectionException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.connection_error, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (InvalidPKException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.invalid_pk_toast, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }finally {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.loading).setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });

            }
        });

        alert.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }

    private void promptStorageSelection() {
        String[] options = {getResources().getString(R.string.wallthereum_wallets), getResources().getString(R.string.device_storage)};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(R.string.select_key_file_using)
                .setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                getKeystoreFromPath(getFilesDir().getPath());
                                break;
                            case 1:
                                getKeystoreFromPath(DialogConfigs.DEFAULT_DIR);
                                break;
                            default:
                                break;
                        }
                    }
                });
        builder.show();
    }

    private void getKeystoreFromPath(String path) {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(path);
        properties.error_dir = new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions = new String[]{"json"};
        final FilePickerDialog filePickerDialog = new FilePickerDialog(MainActivity.this, properties);
        filePickerDialog.setTitle(R.string.select_file);
        filePickerDialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                Wallet.getWallet().setCurrentKeyStoreFile(files[0]);
                unlockKeystore(files[0]);
            }
        });
        filePickerDialog.show();
    }


    private void unlockKeystore(final String filePath) {
        if(!Network.getNetwork().isInternetConnected(mContext)){
            Toast.makeText(this, R.string.connection_error, Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        final EditText edittext = new EditText(this);
        edittext.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        edittext.setBackgroundTintList(getResources().getColorStateList(R.color.black));
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
                            Wallet.getWallet().unlockKeystore(filePath, password);
                            Intent intent = new Intent(MainActivity.this, WalletActivity.class);
                            startActivity(intent);
                        } catch (IOException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.file_error_toast, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (CipherException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.decryption_error_toast, Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (ConnectionException e) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, R.string.connection_error, Toast.LENGTH_LONG).show();
                                }
                            });
                        }finally {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.loading).setVisibility(View.GONE);
                                }
                            });
                        }
                    }
                });

            }
        });

        alert.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }
}
