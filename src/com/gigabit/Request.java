package com.gigabit;

import java.io.IOException;
import java.util.ArrayList;

import static com.gigabit.Main.checkOid;
import static com.gigabit.Main.sortByVendors;

public class Request extends Thread {

    private String oidValue;
    private int snmpVersion;
    private String community;
    private String port;
    private ArrayList<String> ip;

    private ArrayList<String> result = new ArrayList<>();

    public Request(String oidValue, int snmpVersion, String community, String port, ArrayList<String> ip){
        this.oidValue = oidValue;
        this.snmpVersion = snmpVersion;
        this.community = community;
        this.port = port;
        this.ip = ip;
    }

    @Override
    public void run() {

        System.out.println("thread start");
        for (String currentIP : ip) {

            try {
                String s = checkOid(community, snmpVersion, currentIP, port, oidValue);
                //System.out.println(s);
                result.add(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sortByVendors(result);

    }

    public ArrayList<String> getResult() {
        return result;
    }

    public void setResult(ArrayList<String> result) {
        this.result = result;
    }
}
