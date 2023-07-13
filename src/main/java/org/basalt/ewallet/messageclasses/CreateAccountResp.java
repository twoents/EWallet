/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.basalt.ewallet.messageclasses;

/**
 *
 * @author Dewan
 */
public class CreateAccountResp {
    private Long userId;
    public CreateAccountResp( Long userId ) {
        setUserId(userId);
    }
     /**
     * @return the userId
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * @param userId the userId to set
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    
    
}
