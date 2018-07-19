package com.gigabit;

import java.io.IOException;
import java.util.ArrayList;

public class Request extends Thread {

    private String oidValue;
    private int snmpVersion;
    private String community;
    private String port;
    private ArrayList<String> ip;

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
        ArrayList<String> dlink = new ArrayList<>();
        ArrayList<String> edgeCore = new ArrayList<>();
        ArrayList<String> alcatel = new ArrayList<>();
        ArrayList<String> huawei = new ArrayList<>();
        ArrayList<String> olt = new ArrayList<>();
        ArrayList<String> other = new ArrayList<>();

        for (String currentIP : ip) {

            try {
                String s = Main.checkOid(community, snmpVersion, currentIP, port, oidValue);

                if (s.contains("D-Link") || s.contains("DES") || s.contains("DGS")) {
                    String[] res = s.split(":");
                    dlink.add(res[0] + ";dlink;up");

                }
                else if (s.contains("ES3510") || s.contains("ES3528M") || s.contains("ES3526XA") || s.contains("ES3528MV2") || s.contains("ECS3510")) {
                    String[] res = s.split(":");
                    edgeCore.add(res[0] + ";edgecore;up");
                }
                else if (s.contains("Alcatel") || s.contains("OmniStack")) {
                    String[] res = s.split(":");
                    alcatel.add(res[0] + ";alcatel;up");
                }
                else if (s.contains("S3328TP-EI-24S") || s.contains("S2326TP-EI")) {
                    String[] res = s.split(":");
                    huawei.add(res[0] + ";huawei;up");
                }

                else if (s.contains("Huawei Integrated Access Software")) {
                    String[] res = s.split(":");
                    olt.add(res[0] + ";ma5608t;up");
                }

                else if (s.contains("BDCOM")) {
                    String[] res = s.split(":");
                    olt.add(res[0] + ";bdcom;up");
                }

                else {
                    other.add(s);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Main.routerdbCreator("Dlink", dlink);
            Main.routerdbCreator("EdgeCore",edgeCore);
            Main.routerdbCreator("Alcatel", alcatel);
            Main.routerdbCreator("Huawei", huawei);
            Main.routerdbCreator("Olt", olt);
            Main.routerdbCreator("Other", other);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
