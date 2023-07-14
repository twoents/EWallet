package org.basalt.ewallet.messageclasses;

import java.math.BigDecimal;

public class BalanceResp {
    private Long walletId;
    private String walName;
    private BigDecimal balance;

    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public String getWalName() {
        return walName;
    }
    public void setWalName(String walName) {
        this.walName = walName;
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
