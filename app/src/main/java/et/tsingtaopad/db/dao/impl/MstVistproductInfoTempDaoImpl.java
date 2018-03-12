package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MstVistproductInfoTempDao;
import et.tsingtaopad.db.table.MstVistproductInfoTemp;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MstVistproductInfoTempDaoImpl extends BaseDaoImpl<MstVistproductInfoTemp, String> implements MstVistproductInfoTempDao {

	public MstVistproductInfoTempDaoImpl(ConnectionSource connectionSource) throws SQLException {
		super(connectionSource, MstVistproductInfoTemp.class);
	}


}
