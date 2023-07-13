package org.basalt.ewallet.messageclasses;

public class LoginReq {
    private String username;
    private String passHash;

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
     * @return the passHash
     */
    public String getPassHash() {
        return passHash;
    }

    /**
     * @param passHash the passHash to set
     */
    public void setPassHash(String passHash) {
        this.passHash = passHash;
    }
    
}
