package com.ytzl.gotrip.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sms")
public class SmsProperties {
   private String servierIP;
   private String servierPort;
   private String accountSid;
   private String accountToken;
   private String appId;

    public String getServierIP() {
        return servierIP;
    }

    public void setServierIP(String servierIP) {
        this.servierIP = servierIP;
    }

    public String getServierPort() {
        return servierPort;
    }

    public void setServierPort(String servierPort) {
        this.servierPort = servierPort;
    }

    public String getAccountSid() {
        return accountSid;
    }

    public void setAccountSid(String accountSid) {
        this.accountSid = accountSid;
    }

    public String getAccountToken() {
        return accountToken;
    }

    public void setAccountToken(String accountToken) {
        this.accountToken = accountToken;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }
}
