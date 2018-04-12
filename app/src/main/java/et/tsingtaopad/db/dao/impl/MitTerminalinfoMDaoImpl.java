package et.tsingtaopad.db.dao.impl;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;

import et.tsingtaopad.db.dao.MitTerminalinfoMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMTempDao;
import et.tsingtaopad.db.table.MitTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: 终端表的DAO层</br>
 */
public class MitTerminalinfoMDaoImpl extends BaseDaoImpl<MitTerminalinfoM, String> implements MitTerminalinfoMDao {

    public MitTerminalinfoMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MitTerminalinfoM.class);
    }



}
