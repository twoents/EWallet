package org.basalt.ewallet.dataclasses;

import java.math.BigDecimal;
import java.util.Date;

public class EWTransaction {
    private Long id;
    private String description;
    private Date txDate;
    private BigDecimal amount;
    private long walletId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getTxDate() {
        return txDate;
    }

    public void setTxDate(Date txDate) {
        this.txDate = txDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public long getWalletId() {
        return walletId;
    }

    public void setWalletId(long userId) {
        this.walletId = userId;
    }
}
