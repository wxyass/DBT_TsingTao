/**
 * 
 */
package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitValcheckitemMDao;
import et.tsingtaopad.db.dao.MitValchecktypeMDao;
import et.tsingtaopad.db.table.MitValcheckitemM;
import et.tsingtaopad.db.table.MitValchecktypeM;

/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述:
 */
public class MitValcheckitemMDaoImpl extends BaseDaoImpl<MitValcheckitemM, String> implements MitValcheckitemMDao {
	/**
	 * @param connectionSource
	 * @param dataClass
	 * @throws SQLException
	 */
	public MitValcheckitemMDaoImpl(ConnectionSource connectionSource,
                                   Class<MitValcheckitemM> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}


}
