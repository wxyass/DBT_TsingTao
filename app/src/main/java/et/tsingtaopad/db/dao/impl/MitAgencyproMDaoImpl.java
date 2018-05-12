package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitAgencynumMDao;
import et.tsingtaopad.db.dao.MitAgencyproMDao;
import et.tsingtaopad.db.table.MitAgencynumM;
import et.tsingtaopad.db.table.MitAgencyproM;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MitAgencyproMDaoImpl extends BaseDaoImpl<MitAgencyproM, String> implements MitAgencyproMDao {

    public MitAgencyproMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MitAgencyproM.class);
    }

}
