/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.basalt.ewallet.dataclasses;

/**
 *
 * @author Dewan
 */
public class EWLoginQuery extends EWSession {
    private Long walletId;

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
