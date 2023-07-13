package org.basalt.ewallet.messageclasses;

import java.util.Date;

public class TransactionReq {
    private Long walletId;
    private Date fromDate;
    private Date toDate;

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }
    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    /**
     * @return the walletId
     */
    public Long getWalletId() {
        return walletId;
    }

    /**
     * @param walletId the walletId to set
     */
    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
}
