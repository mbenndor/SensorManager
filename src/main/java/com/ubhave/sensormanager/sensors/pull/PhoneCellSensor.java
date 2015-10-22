package com.ubhave.sensormanager.sensors.pull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.ubhave.sensormanager.ESException;
import com.ubhave.sensormanager.data.SensorData;
import com.ubhave.sensormanager.data.pull.PhoneCellData;
import com.ubhave.sensormanager.data.pull.PhoneCellDataList;
import com.ubhave.sensormanager.process.pull.PhoneCellProcessor;
import com.ubhave.sensormanager.sensors.SensorUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by m on 22.10.15.
 */
public class PhoneCellSensor extends AbstractPullSensor
{
    private static final String TAG = "PhoneCellSensor";
    private static final String PERMISSION_ACCESS_CELL_INFO = "android.permission.ACCESS_COARSE_LOCATION";

    private static PhoneCellSensor phoneCellSensor;
    private static Object lock = new Object();

    private ArrayList<PhoneCellData> visibleCells;
    private volatile PhoneCellDataList phoneCellDataList;

    protected PhoneCellSensor(Context context) {
        super(context);
    }

    public static PhoneCellSensor getPhoneCellSensor(final Context context) throws ESException {

        if (phoneCellSensor == null)
        {
            synchronized (lock)
            {
                if (phoneCellSensor == null)
                {
                    if (permissionGranted(context, PERMISSION_ACCESS_CELL_INFO))
                    {
                        phoneCellSensor = new PhoneCellSensor(context);
                    }
                    else
                    {
                        throw new ESException(ESException.PERMISSION_DENIED, SensorUtils.SENSOR_NAME_PHONE_RADIO);
                    }
                }
            }
        }
        return phoneCellSensor;
    }

    @Override
    protected SensorData getMostRecentRawData()
    {
        return phoneCellDataList;
    }

    @Override
    protected void processSensorData()
    {
        PhoneCellProcessor phoneCellProcessor = (PhoneCellProcessor)getProcessor();
        phoneCellDataList = phoneCellProcessor.process(pullSenseStartTimestamp, visibleCells, sensorConfig.clone());
    }

    @Override
    protected String getLogTag()
    {
        return TAG;
    }

    @Override
    public int getSensorType()
    {
        return SensorUtils.SENSOR_TYPE_PHONE_CELL;
    }

    @Override
    protected boolean startSensing()
    {
        new Thread()
        {
            @SuppressLint("NewApi")
            public void run()
            {
                try {
                    visibleCells = new ArrayList<PhoneCellData>();
                    TelephonyManager telephonyManager = (TelephonyManager) applicationContext.getSystemService(Context.TELEPHONY_SERVICE);
                    // TODO handle old API
                    List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();
                    if (cellInfos == null) {
                        // getAllCellInfo() not supported, try old methods
                        switch (telephonyManager.getPhoneType()) {
                            case TelephonyManager.PHONE_TYPE_GSM:
                                GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                                String networkOperator = telephonyManager.getNetworkOperator();
                                String mcc = networkOperator.substring(0, 3);
                                String mnc = networkOperator.substring(3);
                                visibleCells.add(new PhoneCellData(mcc, mnc, cellLocation.getLac(), cellLocation.getCid()));
                                break;
                            default:
                                // TODO: handle unsupported phone type...
                                Log.d(TAG, "unsupported PHONE_TYPE");
                                break;
                        }
                    } else {
                        switch (telephonyManager.getPhoneType()) {
                            case TelephonyManager.PHONE_TYPE_GSM:
                                for (CellInfo cellInfo : cellInfos) {
                                    int  mcc, mnc, lac, cid, level,asuLevel,dbm;
                                    boolean isRegistered=false;
                                    if(cellInfo.getClass().equals(CellInfoWcdma.class)){
                                        CellInfoWcdma cellInfoWdcma = (CellInfoWcdma)cellInfo;
                                        CellIdentityWcdma cellIdentity = cellInfoWdcma.getCellIdentity();
                                        CellSignalStrengthWcdma cellSignalStrength = cellInfoWdcma.getCellSignalStrength();
                                        mcc = cellIdentity.getMcc();
                                        mnc = cellIdentity.getMnc();
                                        lac = cellIdentity.getLac();
                                        cid = cellIdentity.getCid();
                                        level = cellSignalStrength.getLevel();
                                        asuLevel = cellSignalStrength.getAsuLevel();
                                        dbm = cellSignalStrength.getDbm();
                                        isRegistered = cellInfoWdcma.isRegistered();
                                    }else{
                                        CellInfoGsm cellInfoGsm =  (CellInfoGsm)cellInfo;
                                        CellIdentityGsm cellIdentity = cellInfoGsm.getCellIdentity();
                                        CellSignalStrengthGsm cellSignalStrength = cellInfoGsm.getCellSignalStrength();
                                        mcc = cellIdentity.getMcc();
                                        mnc = cellIdentity.getMnc();
                                        lac = cellIdentity.getLac();
                                        cid = cellIdentity.getCid();
                                        level = cellSignalStrength.getLevel();
                                        asuLevel = cellSignalStrength.getAsuLevel();
                                        dbm = cellSignalStrength.getDbm();
                                        isRegistered = cellInfoGsm.isRegistered();

                                    }
                                    visibleCells.add(new PhoneCellData(
                                            String.valueOf(mcc),String.valueOf(mnc), lac, cid,
                                            level, asuLevel,dbm, isRegistered));
                                }
                                break;

                            default:
                                // TODO: handle unsupported phone type...
                                Log.d(TAG, "unsupported PHONE_TYPE");
                                break;
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    // sensing complete
                    notifySenseCyclesComplete();
                }
            }
        }.start();

        return true;
    }

    @Override
    protected void stopSensing()
    {
        // TODO Auto-generated method stub
    }
}
