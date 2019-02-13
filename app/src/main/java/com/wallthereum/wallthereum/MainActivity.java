package com.wallthereum.wallthereum;

import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import java.net.ContentHandler;

import androidx.core.view.GravityCompat;

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
        NewWallet newWallet = new NewWallet(password);


        DialogPlus dialog = DialogPlus.newDialog(this)
                .setGravity(Gravity.CENTER)
                .setContentHolder(new ViewHolder(R.layout.new_wallet_alert))
                .setCancelable(true)
                .setInAnimation(R.anim.fade_in_center)
                .setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(DialogPlus dialog, View view) {
                        switch (view.getId()){
                            case R.id.reject_button:
                                dialog.dismiss();
                                break;
                            case R.id.accept_button:
                                View progressOverlay = findViewById(R.id.progress_overlay);
                                progressOverlay.setVisibility(View.VISIBLE);


//                                dialog.dismiss();
//                                Intent intent = new Intent(MainActivity.this, Wallet.class);
//                                startActivity(intent);
                                break;
                            default:
                                break;
                        }
                    }
                })
                .create();
        dialog.show();
    }
}
