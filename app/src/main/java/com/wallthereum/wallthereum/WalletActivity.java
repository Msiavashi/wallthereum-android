package com.wallthereum.wallthereum;

import androidx.recyclerview.widget.RecyclerView;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
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

        Web3j connection = Network.getNetwork().getmConnection();
        try {
            EthGetBalance ethGetBalance = connection
                    .ethGetBalance(Wallet.getWallet().getAddress(), DefaultBlockParameterName.LATEST)
                    .sendAsync()
                    .get();
            BigInteger wei = ethGetBalance.getBalance();
            Convert.fromWei(wei.toString(), Convert.Unit.ETHER);        //converting wei to ether
            TextView balanceView = (TextView) findViewById(R.id.balance_view);
            balanceView.setText(wei.toString() + " ETHER");
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
                break;
            case R.id.receive_button:
                DialogPlus dialogPlus = DialogPlus.newDialog(this)
                    .setGravity(Gravity.CENTER)
                    .setCancelable(true)
                    .setContentHolder(new ViewHolder(R.layout.receive_view))
                    .setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(DialogPlus dialog, View view) {
                            switch (view.getId()){
                                case R.id.copy_button:
//                                    copy wallet address to clipboard
                                    final ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clipData = ClipData.newPlainText("wallet address", Wallet.getWallet().getAddress());
                                    clipboardManager.setPrimaryClip(clipData);
                                    Toast.makeText(WalletActivity.this, "copied", Toast.LENGTH_SHORT).show();
                                    break;
                                case R.id.reject_button:
                                    dialog.dismiss();
                                    break;
                                default:
                                    break;
                            }
                        }
                    })
                    .create();
                dialogPlus.show();
                EditText editText = findViewById(R.id.wallet_address_textarea);
                editText.setText(Wallet.getWallet().getAddress().replace("0x", ""));
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
