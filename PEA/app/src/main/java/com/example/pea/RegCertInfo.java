package com.example.pea;

public class RegCertInfo {
    String username;
    String rcExpiryDate;

    public RegCertInfo() {}

    public RegCertInfo(String username, String rcExpiryDate) {
        this.username = username;
        this.rcExpiryDate = rcExpiryDate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRcExpiryDate() {
        return rcExpiryDate;
    }

    public void setRcExpiryDate(String rcExpiryDate) {
        this.rcExpiryDate = rcExpiryDate;
    }
}
