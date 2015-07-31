package com.example.udpvoice;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class Tools {
	public static String getLocalIpAddress(){ 
        
        try{ 
             for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) { 
                 NetworkInterface intf = en.nextElement();   
                    for (Enumeration<InetAddress> enumIpAddr = intf   
                            .getInetAddresses(); enumIpAddr.hasMoreElements();) {   
                        InetAddress inetAddress = enumIpAddr.nextElement();   
                        if (!inetAddress.isLoopbackAddress()) {   
                             
                            return inetAddress.getHostAddress().toString();   
                        }   
                    }   
             } 
        }catch (SocketException e) { 
            // TODO: handle exception 
        } 
         
        return null;  
    } 


}
