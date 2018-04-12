package et.tsingtaopad.db.dao;

import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.Dao;

import java.util.List;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.table.MitCameraInfoM;
import et.tsingtaopad.db.table.MstCameraInfoM;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.MstCameraListMStc;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 拜访拍照表的DAO层</br>
 */
public interface MitCameraInfoMDao extends Dao<MitCameraInfoM, String> {




}
