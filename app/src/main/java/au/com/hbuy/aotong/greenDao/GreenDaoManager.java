package au.com.hbuy.aotong.greenDao;

import android.content.Context;
import android.util.Log;

import au.com.hbuy.aotong.MyApplication;
import au.com.hbuy.aotong.greenDao.gen.DaoMaster;
import au.com.hbuy.aotong.greenDao.gen.DaoSession;

/**
 * Created by yangwei on 2016/8/11--10:19.
 * <p/>
 * E-Mail:yangwei199402@gmail.com
 */
public class GreenDaoManager {
    private static GreenDaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    public GreenDaoManager() {
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(MyApplication.getContext(), "message-db", null);
        DaoMaster mDaoMaster = new DaoMaster(devOpenHelper.getWritableDb());
        mDaoSession = mDaoMaster.newSession();
    }

    public static GreenDaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new GreenDaoManager();
       }
        return mInstance;
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public DaoSession getNewSession() {
        mDaoSession = mDaoMaster.newSession();
        return mDaoSession;
    }
}
