package com.vcom.smart.model;

import android.text.TextUtils;

/**
 * @Author Lzz
 * @Date 2020/10/30 9:48
 */
public class User {

    private String userId;
    private String userName;
    private String userPassword;
    private String invitationCode;
    private String createTime;
    private String isLock;
    private String meshPassword;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getInvitationCode() {
        return invitationCode;
    }

    public void setInvitationCode(String invitationCode) {
        this.invitationCode = invitationCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getIsLock() {
        return isLock;
    }

    public void setIsLock(String isLock) {
        this.isLock = isLock;
    }

    public String getMeshPassword() {
        return meshPassword;
    }

    public void setMeshPassword(String meshPassword) {
        this.meshPassword = meshPassword;
    }

    public boolean isEmpty(){
        return TextUtils.isEmpty(userId);
    }

}
