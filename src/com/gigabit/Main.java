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

        ArrayList<String> mgmtIPs780 = createMgmtIPs(args[1]);
        ArrayList<String> mgmtIPs783 = createMgmtIPs(args[2]);
        ArrayList<String> mgmtIPs2275 = createMgmtIPs(args[3]);

        String oidValue = ".1.3.6.1.2.1.1.1.0";
        int snmpVersion = SnmpConstants.version1;
        String community = args[0];
        String port = "161";


        Request mgmt780 = new Request(oidValue, snmpVersion, community, port, mgmtIPs780);
        Request mgmt783 = new Request(oidValue, snmpVersion, community, port, mgmtIPs783);
        Request mgmt2275 = new Request(oidValue, snmpVersion, community, port, mgmtIPs2275);

        mgmt780.start();
        mgmt783.start();
        mgmt2275.start();


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
}