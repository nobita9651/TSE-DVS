package com.example.uea;

public class PoluCertInfo {

    String username;
    String polCertExpiry;

    public PoluCertInfo() {}

    public PoluCertInfo(String username, String polCertExpiry) {
        this.username = username;
        this.polCertExpiry = polCertExpiry;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPolCertExpiry() {
        return polCertExpiry;
    }

    public void setPolCertExpiry(String polCertExpiry) {
        this.polCertExpiry = polCertExpiry;
    }
}
