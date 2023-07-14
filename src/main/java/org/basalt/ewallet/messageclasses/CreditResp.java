package org.basalt.ewallet.messageclasses;

import java.math.BigDecimal;

public class CreditResp {
    private Long walletId;
    private BigDecimal balance;
    private String walName;

    public Long getWalletId() {
        return walletId;
    }
    public void setWalletId(Long walletId) {
        this.walletId = walletId;
    }
    public BigDecimal getBalance() {
        return balance;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /**
     * @return the walName
     */
    public String getWalName() {
        return walName;
    }

    /**
     * @param walName the walName to set
     */
    public void setWalName(String walName) {
        this.walName = walName;
    }
}
