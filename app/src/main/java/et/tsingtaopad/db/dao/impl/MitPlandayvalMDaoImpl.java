package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitPlandayvalMDao;
import et.tsingtaopad.db.table.MitPlandayvalM;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MitPlandayvalMDaoImpl extends BaseDaoImpl<MitPlandayvalM, String> implements MitPlandayvalMDao {

    public MitPlandayvalMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MitPlandayvalM.class);
    }

}
