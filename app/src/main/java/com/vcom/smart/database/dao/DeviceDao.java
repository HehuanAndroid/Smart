package com.vcom.smart.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.vcom.smart.database.entity.DeviceDB;

import java.util.List;

@Dao
public interface DeviceDao {

    @Insert
    void insertDevice(DeviceDB... device);

    @Delete
    void deleteDevice(DeviceDB device);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDevice(DeviceDB device);

    @Query("select * from DeviceDB")
    List<DeviceDB> getAllLocalDevices();

    @Query("select count(*) from DeviceDB")
    int getDeviceCount();

    @Query("select * from DeviceDB where meshAddress==:meshAdds")
    DeviceDB getDeviceByMesh(int meshAdds);

    @Query("delete from DeviceDB")
    int cleanAll();

}
