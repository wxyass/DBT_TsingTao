/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import cn.com.benyoyo.manage.bs.IntStc.DataresultTerPurchaseDetailsStc;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.db.dao.MitVisitMDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.main.operation.indexstatus.domain.IndexStatusStc;
import et.tsingtaopad.main.visit.tomorrowworkrecord.domain.DayWorkDetailStc;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 终端进货详情实现DAO</br>
 */
public class MitVisitMDaoImpl extends BaseDaoImpl<MitVisitM, String> implements MitVisitMDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitVisitMDaoImpl(ConnectionSource connectionSource,
                            Class<MitVisitM> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
