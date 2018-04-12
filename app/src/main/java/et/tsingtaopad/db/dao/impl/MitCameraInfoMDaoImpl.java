package et.tsingtaopad.db.dao.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitCameraInfoMDao;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.table.MitCameraInfoM;
import et.tsingtaopad.db.table.MstCameraInfoM;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.MstCameraListMStc;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MitCameraInfoMDaoImpl extends BaseDaoImpl<MitCameraInfoM, String> implements MitCameraInfoMDao {

    public MitCameraInfoMDaoImpl(ConnectionSource
                        connectionSource) throws SQLException {
        super(connectionSource, MitCameraInfoM.class);
    }

}
