package com.globalaccelerex.paymentapi.utils;

import com.globalaccelerex.paymentapi.enums.DeviceType;

public class DeviceDetector {
    public static DeviceType detect(String userAgent) {
        if (userAgent.contains("Android") || userAgent.contains("iPhone") || userAgent.contains("iPad")) {
            return DeviceType.MOBILE;
        } else if (userAgent.contains("Windows NT") || userAgent.contains("Macintosh")) {
            return DeviceType.WEB_BROWSER;
        } else {
            if (userAgent.contains("Mobile") || userAgent.contains("Tablet")) {
                return DeviceType.MOBILE;
            } else if (userAgent.contains("Desktop") || userAgent.contains("Postman")) {
                return DeviceType.WEB_BROWSER;
            } else {
                return DeviceType.UNKNOWN;
            }
        }
    }
}
