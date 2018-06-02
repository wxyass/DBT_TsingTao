package et.tsingtaopad.db.dao.impl;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitRepairterMDao;
import et.tsingtaopad.db.table.MitRepairterM;
import et.tsingtaopad.dd.dddealplan.domain.DealStc;
import et.tsingtaopad.dd.dddealplan.make.domain.DealPlanMakeStc;
import et.tsingtaopad.dd.dddealplan.remake.domain.ReCheckTimeStc;
import et.tsingtaopad.dd.ddweekplan.domain.DayDetailStc;
import et.tsingtaopad.initconstvalues.domain.KvStc;


/**
 * Created by yangwenmin on 2017/12/12.
 * 功能描述: </br>
 */
public class MitRepairterMDaoImpl extends BaseDaoImpl<MitRepairterM, String> implements MitRepairterMDao {

    public MitRepairterMDaoImpl(ConnectionSource connectionSource) throws SQLException {
        super(connectionSource, MitRepairterM.class);
    }

    @Override
    public List<DealPlanMakeStc> querySelectTerminal(DatabaseHelper helper, String repairid) {
        List<DealPlanMakeStc> detailStcs = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select g.terminalkey, g.terminalname,h.gridkey,h.gridname,h.username,h.userid from MIT_REPAIRTER_M mv  ");
        buffer.append("    left join MST_TERMINALINFO_M g on g.terminalkey = mv.terminalkey   ");
        buffer.append("    left join MST_GRID_M h on h.gridkey = mv.gridkey   ");
        buffer.append("    where repairid = ? ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{repairid});
        DealPlanMakeStc kvStc ;
        while (cursor.moveToNext()) {
            kvStc = new DealPlanMakeStc();
            kvStc.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            kvStc.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            kvStc.setGridkey(cursor.getString(cursor.getColumnIndex("gridkey")));
            kvStc.setGridname(cursor.getString(cursor.getColumnIndex("gridname")));
            kvStc.setUserkey(cursor.getString(cursor.getColumnIndex("userid")));
            kvStc.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            detailStcs.add(kvStc);
        }
        return detailStcs;
    }

    @Override
    public List<DealStc> queryDealPan(DatabaseHelper helper) {
        List<DealStc> detailStcs = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();

        buffer.append("select mv.id repairid,g.id repaircheckid, mv.content, mv.repairremark, mv.checkcontent,  ");
        buffer.append("    f.gridkey, f.gridname, f.userid, f.username, g.status, g.repairtime   ");
        buffer.append("    from MIT_REPAIR_M mv   ");
        buffer.append("    left join MIT_REPAIRCHECK_M g     on g.repairid = mv.id ");
        buffer.append("    left join MST_GRID_M f     on f.gridkey = mv.gridkey ");
        buffer.append("    group by mv.id ");
        buffer.append("    order by g.repairtime desc ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), null);
        DealStc kvStc ;
        while (cursor.moveToNext()) {
            kvStc = new DealStc();
            kvStc.setRepairid(cursor.getString(cursor.getColumnIndex("repairid")));
            kvStc.setRepaircheckid(cursor.getString(cursor.getColumnIndex("repaircheckid")));
            kvStc.setContent(cursor.getString(cursor.getColumnIndex("content")));
            kvStc.setRepairremark(cursor.getString(cursor.getColumnIndex("repairremark")));
            kvStc.setCheckcontent(cursor.getString(cursor.getColumnIndex("checkcontent")));
            kvStc.setGridkey(cursor.getString(cursor.getColumnIndex("gridkey")));
            kvStc.setGridname(cursor.getString(cursor.getColumnIndex("gridname")));
            kvStc.setUserid(cursor.getString(cursor.getColumnIndex("userid")));
            kvStc.setUsername(cursor.getString(cursor.getColumnIndex("username")));
            kvStc.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            kvStc.setRepairtime(cursor.getString(cursor.getColumnIndex("repairtime")));
            detailStcs.add(kvStc);
        }
        return detailStcs;
    }

    @Override
    public List<DealStc> queryDealPlanTermName(DatabaseHelper helper,String repairid) {
        List<DealStc> detailStcs = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();

        buffer.append("select mv.id, i.terminalkey,i.terminalname   ");
        buffer.append("        from MIT_REPAIR_M mv   ");
        buffer.append("   left join MIT_REPAIRTER_M h on h.repairid = mv.id    ");
        buffer.append("   left join MST_TERMINALINFO_M i on i.terminalkey = h.terminalkey    ");
        buffer.append("    where h.repairid = ? ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{repairid});
        DealStc kvStc ;
        while (cursor.moveToNext()) {
            kvStc = new DealStc();
            kvStc.setRepairid(cursor.getString(cursor.getColumnIndex("id")));
            kvStc.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
            kvStc.setTerminalname(cursor.getString(cursor.getColumnIndex("terminalname")));
            detailStcs.add(kvStc);
        }
        return detailStcs;
    }


    @Override
    public List<ReCheckTimeStc> queryDealPlanStatus(DatabaseHelper helper, String repairid) {
        List<ReCheckTimeStc> detailStcs = new ArrayList<>();
        StringBuffer buffer = new StringBuffer();
        buffer.append("select * from MIT_REPAIRCHECK_M   ");
        buffer.append("    where repairid = ? ");

        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery(buffer.toString(), new String[]{repairid});
        ReCheckTimeStc kvStc ;
        while (cursor.moveToNext()) {
            kvStc = new ReCheckTimeStc();
            kvStc.setId(cursor.getString(cursor.getColumnIndex("id")));
            kvStc.setRepairid(cursor.getString(cursor.getColumnIndex("repairid")));
            kvStc.setStatus(cursor.getString(cursor.getColumnIndex("status")));
            kvStc.setRepairtime(cursor.getString(cursor.getColumnIndex("repairtime")));
            detailStcs.add(kvStc);
        }
        return detailStcs;
    }

}
