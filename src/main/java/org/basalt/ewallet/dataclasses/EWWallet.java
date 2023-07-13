package org.basalt.ewallet.dataclasses;

import java.math.BigDecimal;

public class EWWallet {
    private Long id;
    private String walName;
    private BigDecimal balance;
    private Long userId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getWalName() {
        return walName;
    }

    public void setWalName(String wal_name) {
        this.walName = wal_name;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long user_id) {
        this.userId = user_id;
    }
    
    
}
