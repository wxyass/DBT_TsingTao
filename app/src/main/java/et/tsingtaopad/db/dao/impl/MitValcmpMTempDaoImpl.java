/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitValcmpMTempDao;
import et.tsingtaopad.db.dao.MitValsupplyMTempDao;
import et.tsingtaopad.db.table.MitValcmpMTemp;
import et.tsingtaopad.db.table.MitValsupplyMTemp;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述:
 */
public class MitValcmpMTempDaoImpl extends BaseDaoImpl<MitValcmpMTemp, String> implements MitValcmpMTempDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitValcmpMTempDaoImpl(ConnectionSource connectionSource,
                                 Class<MitValcmpMTemp> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
