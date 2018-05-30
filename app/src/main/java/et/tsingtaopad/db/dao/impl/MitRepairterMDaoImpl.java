package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitRepairterMDao;
import et.tsingtaopad.db.table.MitRepairterM;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MitRepairterMDaoImpl extends BaseDaoImpl<MitRepairterM, String> implements MitRepairterMDao {

    public MitRepairterMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MitRepairterM.class);
    }

}
