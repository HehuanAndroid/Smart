package com.vcom.smart.model;

import java.util.List;

/**
 * @Author Lzz
 * @Date 2020/11/4 13:24
 */
public class Product {


    /**
     * equiId : 200828114535719
     * equiName : 三位开关
     * equiNickName : ThreeBitsSwitch
     * isSceneOn ： 0
     * version : null
     * equipInfoList : [{"equipInfoPid":"0x5804"}]
     * type : 201104143340988
     * dictName : 开关
     * dictType : 2
     */

    private String equiId;
    private String equiName;
    private String equiNickName;
    private Object version;
    private String type;
    private String dictName;
    private String dictType;
    private int isSceneOn;

    private List<EquipInfo> equipInfoList;

    public String getEquiId() {
        return equiId;
    }

    public void setEquiId(String equiId) {
        this.equiId = equiId;
    }

    public String getEquiName() {
        return equiName;
    }

    public void setEquiName(String equiName) {
        this.equiName = equiName;
    }

    public String getEquiNickName() {
        return equiNickName;
    }

    public void setEquiNickName(String equiNickName) {
        this.equiNickName = equiNickName;
    }

    public Object getVersion() {
        return version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    public String getDictType() {
        return dictType;
    }

    public void setDictType(String dictType) {
        this.dictType = dictType;
    }

    public int getIsSceneOn() {
        return isSceneOn;
    }

    public void setIsSceneOn(int isSceneOn) {
        this.isSceneOn = isSceneOn;
    }

    public List<EquipInfo> getEquipInfoList() {
        return equipInfoList;
    }

    public void setEquipInfoList(List<EquipInfo> equipInfoList) {
        this.equipInfoList = equipInfoList;
    }

    public static class EquipInfo {
        private String equipInfoPid;
        private String equipInfoType;

        public String getEquipInfoPid() {
            return equipInfoPid;
        }

        public void setEquipInfoPid(String equipInfoPid) {
            this.equipInfoPid = equipInfoPid;
        }

        public String getEquipInfoType() {
            return equipInfoType;
        }

        public void setEquipInfoType(String equipInfoType) {
            this.equipInfoType = equipInfoType;
        }
    }
}
