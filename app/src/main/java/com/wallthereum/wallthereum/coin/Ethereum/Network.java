package com.wallthereum.wallthereum.coin.Ethereum;


import com.wallthereum.wallthereum.BaseActivity;
import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.R;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

public class Network {
    private String mName = "kovan";
    private int mChainId = 42;
    private String mAddress = "https://kovan.infura.io/v3/0f6aa93d937241f1aa67cb7ff365ce77";
    private Web3j mConnection;

    private Network(){}

    public boolean isConnected() {
        try {
            if (mConnection == null){
                return false;
            }
            mConnection.netListening().send();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static class NetworkHolder {
        private static final Network NETWORK = new Network();
    }


    public void connect() throws ConnectionException {
        try{
            mConnection = Web3jFactory.build(new HttpService(this.getmAddress()));
        }catch (Exception err){
            throw new ConnectionException("Network problem");
        }
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
