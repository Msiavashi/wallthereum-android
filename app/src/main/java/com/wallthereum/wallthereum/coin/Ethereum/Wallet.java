//TODO: add password validation

package com.wallthereum.wallthereum.coin.Ethereum;

import com.wallthereum.wallthereum.Exceptions.ConnectionException;

public class Wallet {
    private String mPassword;


    public Wallet(String password){
        this.mPassword = password;
    }

    public void showWarningDialog() {

    }

    public void onWarningDeclined() {

    }

    public void onWarningAccepted() {

    }

    public void create() throws ConnectionException {
        
        if(!Network.getNetwork().isConnected()){
            Network.getNetwork().connect();
        }

    }
    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String Password) {
        this.mPassword = mPassword;
    }
}
