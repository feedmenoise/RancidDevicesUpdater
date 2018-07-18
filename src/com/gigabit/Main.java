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

    public static void main(String[] args) throws Exception
    {
        String oidValue  = ".1.3.6.1.2.1.1.1.0";
        int snmpVersion  = SnmpConstants.version1;
        String community  = args[1];
        String port = "161";

        ArrayList<String> ip = createMgmtIPs(args[0]);

        for (String currentIP : ip) {

            System.out.println("Current IP: " + currentIP);
            checkOid(community, snmpVersion, currentIP, port, oidValue);
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

    private static void checkOid (String community, int snmpVersion, String ipAddress, String port, String oidValue) throws IOException {
        // Create TransportMapping and Listen
        TransportMapping<UdpAddress> transport = new DefaultUdpTransportMapping();
        transport.listen();

        // Create Target Address object
        CommunityTarget comtarget = new CommunityTarget();
        comtarget.setCommunity(new OctetString(community));
        comtarget.setVersion(snmpVersion);
        comtarget.setAddress(new UdpAddress(ipAddress + "/" + port));
        comtarget.setRetries(2);
        comtarget.setTimeout(1000);

        // Create the PDU object
        PDU pdu = new PDU();
        pdu.add(new VariableBinding(new OID(oidValue)));
        pdu.setType(PDU.GET);
        pdu.setRequestID(new Integer32(1));

        // Create Snmp object for sending data to Agent
        Snmp snmp = new Snmp(transport);

        System.out.println("Sending Request to Agent...");
        ResponseEvent response = snmp.get(pdu, comtarget);

        // Process Agent Response
        if (response != null) {
            //System.out.println("Got Response from Agent");
            PDU responsePDU = response.getResponse();

            if (responsePDU != null) {
                int errorStatus = responsePDU.getErrorStatus();
                int errorIndex = responsePDU.getErrorIndex();
                String errorStatusText = responsePDU.getErrorStatusText();

                if (errorStatus == PDU.noError) {
                    System.out.println("Snmp Get Response = " + responsePDU.getVariableBindings());
                } else {
                    System.out.println("Error: Request Failed");
                    System.out.println("Error Status = " + errorStatus);
                    System.out.println("Error Index = " + errorIndex);
                    System.out.println("Error Status Text = " + errorStatusText);
                }
            } else {
                System.out.println("Error: Response PDU is null");
            }
        } else {
            System.out.println("Error: Agent Timeout... ");
        }
        snmp.close();
    }
}