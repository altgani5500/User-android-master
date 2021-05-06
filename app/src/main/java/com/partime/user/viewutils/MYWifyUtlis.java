package com.partime.user.viewutils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

public class MYWifyUtlis {


    public void connectWiFi(String SSID, String password, String Security, Context con) {
        try {

            Log.d("WIFY", "Item clicked, SSID " + SSID + " Security : " + Security);

            String networkSSID = SSID;
            String networkPass = password;

            WifiConfiguration conf = new WifiConfiguration();
            conf.SSID = "\"" + networkSSID + "\"";   // Please note the quotes. String should contain ssid in quotes
            conf.status = WifiConfiguration.Status.ENABLED;
            conf.priority = 40;
// Check if security type is WEP
            if (Security.toUpperCase().contains("WEP")) {
                Log.e("rht", "Configuring WEP");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("^[0-9a-fA-F]+$")) {
                    conf.wepKeys[0] = networkPass;
                } else {
                    conf.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                }

                conf.wepTxKeyIndex = 0;
// Check if security type is WPA
            } else if (Security.toUpperCase().contains("WPA")) {
                Log.e("WIFY", "Configuring WPA");

                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                conf.preSharedKey = "\"" + networkPass + "\"";
              
// Check if network is open network
            } else {
                Log.e("WIFY", "Configuring OPEN network");
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                conf.allowedAuthAlgorithms.clear();
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }
//Connect to the network
            WifiManager wifiManager = (WifiManager) con.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            int networkId = wifiManager.addNetwork(conf);

            Log.e("WIFY", "Add result " + networkId);

            List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    Log.e("WIFY", "WifiConfiguration SSID " + i.SSID);

                    boolean isDisconnected = wifiManager.disconnect();
                    Log.e("WIFY", "isDisconnected : " + isDisconnected);

                    boolean isEnabled = wifiManager.enableNetwork(i.networkId, true);
                    Log.e("WIFY", "isEnabled : " + isEnabled);

                    boolean isReconnected = wifiManager.reconnect();
                    Log.e("WIFY", "isReconnected : " + isReconnected);
                    
                    

                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* var wifyName = getWifyName(this@AddNetworkActivity).replace("\"", "")
                        if (wifyName .contains("NA")) {
        if (wifyName.contentEquals(userName)) {
            addWorkNetwork(userName, password)
        } else {
            MYWifyUtlis().connectWiFi(userName,password,"WEP",this@AddNetworkActivity)
            if(getWifyName(this@AddNetworkActivity).replace("\"", "").contains("NA") &&
                    !getWifyName(this@AddNetworkActivity).replace("\"", "").contentEquals(userName)){

                MYWifyUtlis().connectWiFi(userName,password,"WPA",this@AddNetworkActivity)
                if(getWifyName(this@AddNetworkActivity).replace("\"", "").contains("NA") &&
                        !getWifyName(this@AddNetworkActivity).replace("\"", "").contentEquals(userName)){
                    MYWifyUtlis().connectWiFi(userName,password,"WIFY",this@AddNetworkActivity)
                    if(getWifyName(this@AddNetworkActivity).replace("\"", "").contains("NA") &&
                            getWifyName(this@AddNetworkActivity).replace("\"", "").contentEquals(userName)){
                        addWorkNetwork(userName, password)
                    }else{
                        Toast.makeText(this@AddNetworkActivity,getString(R.string.no_network_msg),Toast.LENGTH_LONG).show()
                    }
                }else{
                    addWorkNetwork(userName, password)
                }
            }else{
                addWorkNetwork(userName, password)
            }
        }
    }*/
}
