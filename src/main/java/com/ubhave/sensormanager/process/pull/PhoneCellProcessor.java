package com.ubhave.sensormanager.process.pull;

import android.content.Context;

import com.ubhave.sensormanager.config.SensorConfig;
import com.ubhave.sensormanager.data.pull.PhoneCellData;
import com.ubhave.sensormanager.data.pull.PhoneCellDataList;
import com.ubhave.sensormanager.process.AbstractProcessor;

import java.util.ArrayList;

/**
 * Created by m on 22.10.15.
 */
public class PhoneCellProcessor extends AbstractProcessor {

    public PhoneCellProcessor(Context context, boolean rw, boolean sp) {
        super(context, rw, sp);
    }

    public PhoneCellDataList process(long pullSenseTimeStamp, final ArrayList<PhoneCellData> phoneCellDatas, SensorConfig sensorConfig){

        PhoneCellDataList phoneCellDataList = new PhoneCellDataList(pullSenseTimeStamp, sensorConfig);
        if(setRawData){
            phoneCellDataList.setPhoneCell(phoneCellDatas);
        }
        return phoneCellDataList;
    }
}