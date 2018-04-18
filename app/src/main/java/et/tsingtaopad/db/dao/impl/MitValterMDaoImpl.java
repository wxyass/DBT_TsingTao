/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitValterMDao;
import et.tsingtaopad.db.dao.MitVisitMDao;
import et.tsingtaopad.db.table.MitValterM;
import et.tsingtaopad.db.table.MitVisitM;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述:
 */
public class MitValterMDaoImpl extends BaseDaoImpl<MitValterM, String> implements MitValterMDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitValterMDaoImpl(ConnectionSource connectionSource,
                             Class<MitValterM> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
