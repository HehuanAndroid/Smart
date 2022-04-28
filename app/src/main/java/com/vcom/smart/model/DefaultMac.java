package com.vcom.smart.model;

import androidx.annotation.NonNull;

/**
 * @Author Lzz
 * @Date 2020/11/4 17:54
 */
public class DefaultMac {

    private String positionId;
    private String userId;
    private String productMac;
    private String productPId;

    public String getPositionId() {
        return positionId;
    }

    public void setPositionId(String positionId) {
        this.positionId = positionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductMac() {
        return productMac;
    }

    public void setProductMac(String productMac) {
        this.productMac = productMac;
    }

    public String getProductPId() {
        return productPId;
    }

    public void setProductPId(String productPId) {
        this.productPId = productPId;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
