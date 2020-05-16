package com.cdqf.cart_class;

public class Card {
    private String szTwoIdName; // 姓名
    private String szTwoIdSex; // 性别
    private String szTwoIdNation; // 民族
    private String szTwoIdBirthday; // 出生日期
    private String szTwoIdAddress; // 住址
    private String szTwoIdNo; // 身份证号码
    private String szTwoIdSignedDepartment; // 签发机关
    private String szTwoIdValidityPeriodBegin; // 有效期起始日期 YYYYMMDD
    private String szTwoIdValidityPeriodEnd; // 有效期截止日期 YYYYMMDD 有效期为 长期时存储“长期”
    private String szTwoIdNewAddress; // 最新住址
    private byte[] arrTwoIdPhoto; // 照片信息
    private String arrTwoIdPhotoBase64; // 照片信息
//    private byte[] arrTwoIdFingerprint; // 指纹信息
    private String szSNID;
    private String szDNID;
    private String szTwoOtherNO;// 通行证类号码
    private String szTwoSignNum; // 签发次数 
    private String szTwoRemark1; // 预留区 
    private String szTwoType; //证件类型标识 
    private String szTwoRemark2; // 预留区

    public String getSzTwoIdName() {
        return szTwoIdName;
    }

    public void setSzTwoIdName(String szTwoIdName) {
        this.szTwoIdName = szTwoIdName;
    }

    public String getSzTwoIdSex() {
        return szTwoIdSex;
    }

    public void setSzTwoIdSex(String szTwoIdSex) {
        this.szTwoIdSex = szTwoIdSex;
    }

    public String getSzTwoIdNation() {
        return szTwoIdNation;
    }

    public void setSzTwoIdNation(String szTwoIdNation) {
        this.szTwoIdNation = szTwoIdNation;
    }

    public String getSzTwoIdBirthday() {
        return szTwoIdBirthday;
    }

    public void setSzTwoIdBirthday(String szTwoIdBirthday) {
        this.szTwoIdBirthday = szTwoIdBirthday;
    }

    public String getSzTwoIdAddress() {
        return szTwoIdAddress;
    }

    public void setSzTwoIdAddress(String szTwoIdAddress) {
        this.szTwoIdAddress = szTwoIdAddress;
    }

    public String getSzTwoIdNo() {
        return szTwoIdNo;
    }

    public void setSzTwoIdNo(String szTwoIdNo) {
        this.szTwoIdNo = szTwoIdNo;
    }

    public String getSzTwoIdSignedDepartment() {
        return szTwoIdSignedDepartment;
    }

    public void setSzTwoIdSignedDepartment(String szTwoIdSignedDepartment) {
        this.szTwoIdSignedDepartment = szTwoIdSignedDepartment;
    }

    public String getSzTwoIdValidityPeriodBegin() {
        return szTwoIdValidityPeriodBegin;
    }

    public void setSzTwoIdValidityPeriodBegin(String szTwoIdValidityPeriodBegin) {
        this.szTwoIdValidityPeriodBegin = szTwoIdValidityPeriodBegin;
    }

    public String getSzTwoIdValidityPeriodEnd() {
        return szTwoIdValidityPeriodEnd;
    }

    public void setSzTwoIdValidityPeriodEnd(String szTwoIdValidityPeriodEnd) {
        this.szTwoIdValidityPeriodEnd = szTwoIdValidityPeriodEnd;
    }

    public String getSzTwoIdNewAddress() {
        return szTwoIdNewAddress;
    }

    public void setSzTwoIdNewAddress(String szTwoIdNewAddress) {
        this.szTwoIdNewAddress = szTwoIdNewAddress;
    }

    public byte[] getArrTwoIdPhoto() {
        return arrTwoIdPhoto;
    }

    public void setArrTwoIdPhoto(byte[] arrTwoIdPhoto) {
        this.arrTwoIdPhoto = arrTwoIdPhoto;
    }

    public String getArrTwoIdPhotoBase64() {
        return arrTwoIdPhotoBase64;
    }

    public void setArrTwoIdPhotoBase64(String arrTwoIdPhotoBase64) {
        this.arrTwoIdPhotoBase64 = arrTwoIdPhotoBase64;
    }

    //    public byte[] getArrTwoIdFingerprint() {
//        return arrTwoIdFingerprint;
//    }
//
//    public void setArrTwoIdFingerprint(byte[] arrTwoIdFingerprint) {
//        this.arrTwoIdFingerprint = arrTwoIdFingerprint;
//    }

    public String getSzSNID() {
        return szSNID;
    }

    public void setSzSNID(String szSNID) {
        this.szSNID = szSNID;
    }

    public String getSzDNID() {
        return szDNID;
    }

    public void setSzDNID(String szDNID) {
        this.szDNID = szDNID;
    }

    public String getSzTwoOtherNO() {
        return szTwoOtherNO;
    }

    public void setSzTwoOtherNO(String szTwoOtherNO) {
        this.szTwoOtherNO = szTwoOtherNO;
    }

    public String getSzTwoSignNum() {
        return szTwoSignNum;
    }

    public void setSzTwoSignNum(String szTwoSignNum) {
        this.szTwoSignNum = szTwoSignNum;
    }

    public String getSzTwoRemark1() {
        return szTwoRemark1;
    }

    public void setSzTwoRemark1(String szTwoRemark1) {
        this.szTwoRemark1 = szTwoRemark1;
    }

    public String getSzTwoType() {
        return szTwoType;
    }

    public void setSzTwoType(String szTwoType) {
        this.szTwoType = szTwoType;
    }

    public String getSzTwoRemark2() {
        return szTwoRemark2;
    }

    public void setSzTwoRemark2(String szTwoRemark2) {
        this.szTwoRemark2 = szTwoRemark2;
    }
}
