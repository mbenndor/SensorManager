package com.ubhave.sensormanager.data.pull;

import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.List;

/**
 * Created by m on 22.10.15.
 */
public class PhoneCellDataList extends SensorData
{
    private List<PhoneCellData> phoneCell;


    public PhoneCellDataList(long senseStartTimestamp, SensorConfig sensorConfig)
    {
        super(senseStartTimestamp, sensorConfig);
    }

    public void setPhoneCell(List<PhoneCellData> phoneCell)
    {
        this.phoneCell = phoneCell;
    }

    public List<PhoneCellData> getPhoneCell()
    {
        return phoneCell;
    }


    @Override
    public int getSensorType()
    {
        return SensorUtils.SENSOR_TYPE_PHONE_RADIO;
    }
}

