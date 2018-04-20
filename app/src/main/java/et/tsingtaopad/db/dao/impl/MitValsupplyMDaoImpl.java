/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitValsupplyMDao;
import et.tsingtaopad.db.dao.MitValterMDao;
import et.tsingtaopad.db.table.MitValsupplyM;
import et.tsingtaopad.db.table.MitValterM;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述:
 */
public class MitValsupplyMDaoImpl extends BaseDaoImpl<MitValsupplyM, String> implements MitValsupplyMDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitValsupplyMDaoImpl(ConnectionSource connectionSource,
                                Class<MitValsupplyM> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
