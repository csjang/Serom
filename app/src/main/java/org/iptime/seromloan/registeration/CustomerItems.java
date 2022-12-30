package org.iptime.seromloan.registeration;

/**
 * Created by csjan on 2017-10-16.
 */

public class CustomerItems {

    String custNo;
    String shopName;
    String repayAmt;
    String depositAmt;;
    String sendYN;
    int resId;

    public CustomerItems(String custNo, String shopName, String repayAmt, String depositAmt,int resId, String sendYN) {
        this.custNo = custNo;
        this.shopName = shopName;
        this.repayAmt = repayAmt;
        this.depositAmt = depositAmt;
        this.sendYN = sendYN;
        this.resId = resId;
    }


    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getRepayAmt() {
        return repayAmt;
    }

    public void setRepayAmt(String repayAmt) {
        this.repayAmt = repayAmt;
    }

    public String getDepositAmt() {
        return depositAmt;
    }

    public void setDepositAmt(String depositAmt) {
        this.depositAmt = depositAmt;
    }

    public int getResId() { return resId; }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getSendYN() {
        return sendYN;
    }

    public void setSendYN(String sendYN) {
        this.sendYN = sendYN;
    }

}
