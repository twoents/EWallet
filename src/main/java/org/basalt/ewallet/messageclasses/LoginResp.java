package org.basalt.ewallet.messageclasses;

public class LoginResp {
    private String status;
    private String token;
    private Long walletId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
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