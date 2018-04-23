/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitValcmpMDao;
import et.tsingtaopad.db.dao.MitValsupplyMDao;
import et.tsingtaopad.db.table.MitValcmpM;
import et.tsingtaopad.db.table.MitValsupplyM;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述:
 */
public class MitValcmpMDaoImpl extends BaseDaoImpl<MitValcmpM, String> implements MitValcmpMDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitValcmpMDaoImpl(ConnectionSource connectionSource,
                             Class<MitValcmpM> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
