/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.basalt.ewallet.dataclasses;

/**
 *
 * @author Dewan
 */
public class EWUser {

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the passhash
     */
    public String getPasshash() {
        return passhash;
    }

    /**
     * @param passhash the passhash to set
     */
    public void setPasshash(String passhash) {
        this.passhash = passhash;
    }
    private Long id;
    private String username;
    private String passhash;
    
}
