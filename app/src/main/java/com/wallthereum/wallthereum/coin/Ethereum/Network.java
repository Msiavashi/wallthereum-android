package com.wallthereum.wallthereum.coin.Ethereum;


import com.wallthereum.wallthereum.BaseActivity;
import com.wallthereum.wallthereum.Exceptions.ConnectionException;
import com.wallthereum.wallthereum.R;

import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Network {
    private String mName = "kovan";
    private int mChainId = 42;
    private String mAddress = "https://kovan.infura.io/v3/0f6aa93d937241f1aa67cb7ff365ce77";
    private String gasStationAPI = "https://ethgasstation.info/json/ethgasAPI.json";


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

    public Web3j getmConnection() {
        return mConnection;
    }

    public String retrieveGasPrice() throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(this.gasStationAPI)
                .build();
        try(Response response = client.newCall(request).execute()){
            return response.body().string();
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

    public void sendTransaction(BigInteger value, String toAddr, String fromAddr, String privateKey, BigInteger gasLimit, BigInteger gasPrice) throws IOException {
//        getting nonce value
        EthGetTransactionCount ethGetTransactionCount = this.getmConnection().ethGetTransactionCount(fromAddr, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

//        creating raw transaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, toAddr, value);

//        sigining raw txn
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, Wallet.getWallet().getCredentials());
        String hexValue =   Numeric.toHexString(signedMessage);

//        sending signed transaction
        EthSendTransaction ethSendTransaction = this.getmConnection().ethSendRawTransaction(hexValue).send();
    }
}
