package com.wallthereum.wallthereum;

import com.google.android.material.textfield.TextInputEditText;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;

import java.net.ContentHandler;

import androidx.core.view.GravityCompat;

public class MainActivity extends BaseActivity {



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
                .create();
            dialog.show();
    }
}
