package com.wallthereum.wallthereum.coin.Ethereum;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.wallthereum.wallthereum.BaseActivity;
import com.wallthereum.wallthereum.R;

import java.util.HashMap;


public class Network {
    private String mName = "kovan";
    private int mChainId = 42;
    private String mAddress = "https://kovan.infura.io/v3/0f6aa93d937241f1aa67cb7ff365ce77";

    private Network(){}

    private static class NetworkHolder {
        private static final Network NETWORK = new Network();
    }

    public static Network getNetwork() {
        return NetworkHolder.NETWORK;
    }

    public String[] getNames(){
        return BaseActivity.getContext().getResources().getStringArray(R.array.networks_names);
    }

    public boolean changeNetwork(String name, String address, int chainId){
        return true;
    }

    public String getmName() {
        return mName;
    }

    public int getmChainId() {
        return mChainId;
    }

    public String getmAddress() {
        return mAddress;
    }
}
