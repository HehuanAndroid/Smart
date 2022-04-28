package com.vcom.smart.singleton;

import com.vcom.smart.model.DefaultMac;
import com.vcom.smart.model.DeviceType;
import com.vcom.smart.model.Equip;
import com.vcom.smart.model.Product;
import com.vcom.smart.model.Region;
import com.vcom.smart.model.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class VcomSingleton {

    public AtomicBoolean isBleReady = new AtomicBoolean(false);
//    public AtomicBoolean isGpsReady = new AtomicBoolean(false);
//    public AtomicBoolean isLocationReady = new AtomicBoolean(false);

    private User loginUser = new User();

    private List<Region> userRegion = new CopyOnWriteArrayList<>();
    private List<Product> userProduct = new CopyOnWriteArrayList<>();
    private List<DeviceType> deviceTypes = new CopyOnWriteArrayList<>();
    private List<DefaultMac> defaultMacs = new CopyOnWriteArrayList<>();
    private List<Equip> userEquips = new CopyOnWriteArrayList<>();

    private static final VcomSingleton ourInstance = new VcomSingleton();

    public static VcomSingleton getInstance() {
        return ourInstance;
    }

    private VcomSingleton() {

    }

    public void setLoginUser(User loginUser) {
        this.loginUser = loginUser;
    }

    public User getLoginUser() {
        return loginUser;
    }

    public boolean isUserLogin() {
        return loginUser.isEmpty();
    }

    public List<Region> getUserRegion() {
        return userRegion;
    }

    public void setUserRegion(List<Region> userRegion) {
        this.userRegion = userRegion;
    }

    public List<Product> getUserProduct() {
        return userProduct;
    }

    public void setUserProduct(List<Product> userProduct) {
        this.userProduct = userProduct;
    }

    public List<DeviceType> getDeviceTypes() {
        return deviceTypes;
    }

    public void setDeviceTypes(List<DeviceType> deviceTypes) {
        this.deviceTypes = deviceTypes;
    }

    public List<DefaultMac> getDefaultMacs() {
        return defaultMacs;
    }

    public void setDefaultMacs(List<DefaultMac> defaultMacs) {
        this.defaultMacs = defaultMacs;
    }

    public List<Equip> getUserEquips() {
        return userEquips;
    }

    public void setUserEquips(List<Equip> userEquips) {
        this.userEquips = userEquips;
    }
}
