package com.atgc.hd.comm;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

/**
 * Created by duanjisi on 2018/1/11.
 */

public class Utils {

    @SuppressLint("LongLogTag")
    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf
                        .getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    //根据IP获取本地Mac
    public static String getLocalMacAddressFromIp(Context context) {
        String mac_s = "";
        try {
            byte[] mac;
            NetworkInterface ne = NetworkInterface.getByInetAddress(InetAddress.getByName(getLocalIpAddress()));
            mac = ne.getHardwareAddress();
            mac_s = byte2hex(mac);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mac_s;
    }

    public static String byte2hex(byte[] b) {
        StringBuffer hs = new StringBuffer(b.length);
        String stmp = "";
        int len = b.length;
        for (int n = 0; n < len; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            if (stmp.length() == 1)
                hs = hs.append("0").append(stmp);
            else {
                hs = hs.append(stmp);
            }
        }
        return String.valueOf(hs);
    }

    public static void printIpAddress() {
        try {
            Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
            while (eni.hasMoreElements()) {

                NetworkInterface networkCard = eni.nextElement();
                if (!networkCard.isUp()) { // 判断网卡是否在使用
                    continue;
                }
                String DisplayName = networkCard.getDisplayName();
                List<InterfaceAddress> addressList = networkCard.getInterfaceAddresses();
                Iterator<InterfaceAddress> addressIterator = addressList.iterator();
                while (addressIterator.hasNext()) {
                    InterfaceAddress interfaceAddress = addressIterator.next();
                    InetAddress address = interfaceAddress.getAddress();
                    if (!address.isLoopbackAddress()) {
                        String hostAddress = address.getHostAddress();
                        if (hostAddress.indexOf(":") > 0) {
                        } else {
                            String maskAddress = calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength());
                            String gateway = calcSubnetAddress(hostAddress, maskAddress);

                            String broadcastAddress = null;
                            InetAddress broadcast = interfaceAddress.getBroadcast();
                            if (broadcast != null)
                                broadcastAddress = broadcast.getHostAddress();

                            Log.e("GGG", "DisplayName    =   " + DisplayName);
                            Log.e("GGG", "address        =   " + hostAddress);
                            Log.e("GGG", "mask           =   " + maskAddress);
                            Log.e("GGG", "gateway        =   " + gateway);
                            Log.e("GGG", "broadcast      =   " + broadcastAddress + "\n");
                            Log.e("GGG", "----- NetworkInterface  Separator ----\n\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getGateway() {
        String gateway = "";
        try {
            Enumeration<NetworkInterface> eni = NetworkInterface.getNetworkInterfaces();
            while (eni.hasMoreElements()) {

                NetworkInterface networkCard = eni.nextElement();
                if (!networkCard.isUp()) { // 判断网卡是否在使用
                    continue;
                }
                String DisplayName = networkCard.getDisplayName();
                List<InterfaceAddress> addressList = networkCard.getInterfaceAddresses();
                Iterator<InterfaceAddress> addressIterator = addressList.iterator();
                while (addressIterator.hasNext()) {
                    InterfaceAddress interfaceAddress = addressIterator.next();
                    InetAddress address = interfaceAddress.getAddress();
                    if (!address.isLoopbackAddress()) {
                        String hostAddress = address.getHostAddress();
                        if (hostAddress.indexOf(":") > 0) {
                        } else {
                            String maskAddress = calcMaskByPrefixLength(interfaceAddress.getNetworkPrefixLength());
                            gateway = calcSubnetAddress(hostAddress, maskAddress);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gateway;
    }

    public static String calcMaskByPrefixLength(int length) {

        int mask = 0xffffffff << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

    public static String calcSubnetAddress(String ip, String mask) {
        String result = "";
        try {
            // calc sub-net IP
            InetAddress ipAddress = InetAddress.getByName(ip);
            InetAddress maskAddress = InetAddress.getByName(mask);

            byte[] ipRaw = ipAddress.getAddress();
            byte[] maskRaw = maskAddress.getAddress();

            int unsignedByteFilter = 0x000000ff;
            int[] resultRaw = new int[ipRaw.length];
            for (int i = 0; i < resultRaw.length; i++) {
                resultRaw[i] = (ipRaw[i] & maskRaw[i] & unsignedByteFilter);
            }

            // make result string
            result = result + resultRaw[0];
            for (int i = 1; i < resultRaw.length; i++) {
                result = result + "." + resultRaw[i];
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return result;
    }


    /**
     * 29.     * 获取移动设备本地IP
     * 30.     *
     * 31.     * @return
     * 32.
     */
    public static InetAddress getLocalInetAddress() {
        InetAddress ip = null;
        try {
            //列举
            Enumeration<NetworkInterface> en_netInterface = NetworkInterface.getNetworkInterfaces();
            while (en_netInterface.hasMoreElements()) {//是否还有元素
                NetworkInterface ni = (NetworkInterface) en_netInterface.nextElement();//得到下一个元素
                Enumeration<InetAddress> en_ip = ni.getInetAddresses();//得到一个ip地址的列举
                while (en_ip.hasMoreElements()) {
                    ip = en_ip.nextElement();
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1)
                        break;
                    else
                        ip = null;
                }

                if (ip != null) {
                    break;
                }
            }
        } catch (SocketException e) {

            e.printStackTrace();
        }
        return ip;
    }

}
