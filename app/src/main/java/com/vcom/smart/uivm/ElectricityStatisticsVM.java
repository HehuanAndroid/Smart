package com.vcom.smart.uivm;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * @author Banlap on 2021/1/19
 */
public class ElectricityStatisticsVM extends AndroidViewModel {

    private ElectricityStatisticsCallBack callBack;

    public ElectricityStatisticsVM(@NonNull Application application) {
        super(application);
    }
    public void setCallBack(ElectricityStatisticsCallBack callBack){
        this.callBack = callBack;
    }

    public void back() {
        callBack.viewBack();
    }

    public interface ElectricityStatisticsCallBack {
        void viewBack();
    }
}
