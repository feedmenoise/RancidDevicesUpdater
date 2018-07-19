package com.gigabit;

import java.io.IOException;
import java.util.ArrayList;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

public class Main  {

    public static void main(String[] args)
    {
        String oidValue = ".1.3.6.1.2.1.1.1.0";
        int snmpVersion = SnmpConstants.version1;
        String community = args[0];
        String port = "161";

        if (args[1] != null) {
            ArrayList<String> mgmtIPs780 = createMgmtIPs(args[1]);
            Request mgmt780 = new Request(oidValue, snmpVersion, community, port, mgmtIPs780);
            mgmt780.start();

            if (args[2] != null) {
                ArrayList<String> mgmtIPs783 = createMgmtIPs(args[2]);
                Request mgmt783 = new Request(oidValue, snmpVersion, community, port, mgmtIPs783);
                mgmt783.start();

                if (args[3] != null) {
                    ArrayList<String> mgmtIPs2275 = createMgmtIPs(args[3]);
                    Request mgmt2275 = new Request(oidValue, snmpVersion, community, port, mgmtIPs2275);
                    mgmt2275.start();

                }
            }
        }

    }

    private static ArrayList<String> createMgmtIPs(String template){

        ArrayList<String> arrayList = new ArrayList<>();

        for (int i = 0; i <= 255; i++) {
            String ip = template + i;
            arrayList.add(ip);
        }

        return arrayList;
    }

    static String checkOid(String community, int snmpVersion, String ipAddress, String port, String oidValue) throws IOException {

        String result;

        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();

        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oidValue)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        Snmp snmp = new Snmp(transport);

        ResponseEvent response = snmp.get(pdu, comtarget);

        if (response != null) {
            PDU responsePDU = response.getResponse();

            if (responsePDU != null) {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError) {
                    result = ipAddress + ": " + responsePDU.getVariableBindings();

                } else {
                    result = ipAddress + ": " + errorStatus + " "  + errorIndex + " " + errorStatusText;
                }
            } else {
                result = ipAddress + ": Error: Response PDU is null";
            }
        } else {
            result = ipAddress + ": Error: Agent Timeout... ";
        }
        snmp.close();
        return result;
    }

    static void sortByVendors(ArrayList<String> arrayList) {

        ArrayList<String> dlink = new ArrayList<>();
        ArrayList<String> edgeCore = new ArrayList<>();
        ArrayList<String> alcatel = new ArrayList<>();
        ArrayList<String> huawei = new ArrayList<>();
        ArrayList<String> olt = new ArrayList<>();
        ArrayList<String> other = new ArrayList<>();

        for (String s : arrayList) {
            if (s.contains("D-Link") || s.contains("DES") || s.contains("DGS")) {
                String[] res = s.split(":");
                dlink.add(res[0] + ";dlink;up");

            }
            else if (s.contains("ES3510") || s.contains("ES3528M") || s.contains("ES3526XA") || s.contains("ES3528MV2")) {
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
        }

        System.out.println(dlink);
        System.out.println("--------------------------");
        System.out.println(edgeCore);
        System.out.println("--------------------------");
        System.out.println(alcatel);
        System.out.println("--------------------------");
        System.out.println(huawei);
        System.out.println("--------------------------");
        System.out.println(olt);
        System.out.println("--------------------------");
        System.out.println(other);
    }


}
