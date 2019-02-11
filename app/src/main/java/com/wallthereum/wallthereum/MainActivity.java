package com.wallthereum.wallthereum;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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

    }
}
