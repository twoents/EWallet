package org.basalt.ewallet.messageclasses;

public class CreateAccountResp {
    private Long userId;
    public CreateAccountResp( Long userId ) {
        setUserId(userId);
    }
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
