package com.ubhave.sensormanager.data.pull;

/**
 * Created by m on 22.10.15.
 */
public class PhoneCellData extends PhoneRadioData{

    private boolean registered;
    private int level;
    private int asuLevel;
    private int dbm;
    private static final int d = -1;

    public PhoneCellData(String mcc, String mnc, int lac, int cid)
    {
        this(mcc, mnc, lac, cid, d, d, d, false);
    }

    public PhoneCellData(String mcc, String mnc, int lac, int cid, int level, int asuLevel, int dbm,  boolean registered)
    {
        super(mcc, mnc, lac, cid);
        this.level=level;
        this.asuLevel = asuLevel;
        this.dbm = dbm;
        this.registered = registered;
    }

    public boolean isRegistered()
    {
        return registered;
    }

    public void setRegistered(boolean registered)
    {
        this.registered = registered;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getAsuLevel() {
        return asuLevel;
    }

    public void setAsuLevel(int asuLevel) {
        this.asuLevel = asuLevel;
    }

    public int getDbm() {
        return dbm;
    }

    public void setDbm(int dbm) {
        this.dbm = dbm;
    }
}