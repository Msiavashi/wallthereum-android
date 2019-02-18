package com.wallthereum.wallthereum;

import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.joaquimley.faboptions.FabOptions;

public class WalletActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = "Wallet Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        this.initTransactionsHistory();
        this.initFab();
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
                break;
            case R.id.receive_button:
                break;
            case R.id.keystore_button:
                break;
            case R.id.private_key_button:
                break;
            default:
                break;
        }
    }

    private void initTransactionsHistory(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.transactions_history);
        TextView textView = (TextView) findViewById(R.id.empty_transactions);
        recyclerView.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
    }

}
