package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitVistproductInfoDao;
import et.tsingtaopad.db.dao.MstVistproductInfoTempDao;
import et.tsingtaopad.db.table.MitVistproductInfo;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MitVistproductInfoDaoImpl extends BaseDaoImpl<MitVistproductInfo, String> implements MitVistproductInfoDao {

	public MitVistproductInfoDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MitVistproductInfo.class);
	}


}
