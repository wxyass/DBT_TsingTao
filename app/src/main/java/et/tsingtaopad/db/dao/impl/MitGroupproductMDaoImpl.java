/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitGroupproductMDao;
import et.tsingtaopad.db.dao.MstGroupproductMTempDao;
import et.tsingtaopad.db.table.MitGroupproductM;
import et.tsingtaopad.db.table.MstGroupproductMTemp;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 终端进货详情实现DAO</br>
 */
public class MitGroupproductMDaoImpl extends BaseDaoImpl<MitGroupproductM, String> implements MitGroupproductMDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitGroupproductMDaoImpl(ConnectionSource connectionSource,
                                   Class<MitGroupproductM> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
