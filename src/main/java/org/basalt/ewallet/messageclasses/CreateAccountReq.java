package org.basalt.ewallet.messageclasses;

public class CreateAccountReq {
    private String username;
    private String passhash;

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
    
}
