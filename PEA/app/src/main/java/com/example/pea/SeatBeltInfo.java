package com.example.pea;

public class SeatBeltInfo {
    String reg_no;
    String sb_st;

    public SeatBeltInfo() {}

    public SeatBeltInfo(String reg_no, String sb_st) {
        this.reg_no = reg_no;
        this.sb_st = sb_st;
    }

    public String getReg_no() {
        return reg_no;
    }

    public void setReg_no(String reg_no) {
        this.reg_no = reg_no;
    }

    public String getSb_st() {
        return sb_st;
    }

    public void setSb_st(String sb_st) {
        this.sb_st = sb_st;
    }
}
