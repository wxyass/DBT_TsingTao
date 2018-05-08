package et.tsingtaopad.dd.ddagencyres;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitValterMDao;
import et.tsingtaopad.db.dao.MitVisitMDao;
import et.tsingtaopad.db.dao.MstAgencyKFMDao;
import et.tsingtaopad.db.dao.MstAgencyvisitMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MitValcheckterM;
import et.tsingtaopad.db.table.MitValterM;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMCart;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectService;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.main.visit.agencyvisit.domain.AgencySelectStc;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermListService.java</br>
 * 功能描述: 巡店拜访_终端列表的业务逻辑</br>
 * 版本 V 1.0</br>
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
@SuppressLint("DefaultLocale")
public class DdAgencySelectService extends XtTermSelectService{

    private final String TAG = "TermListService";

    private Context context;

    public DdAgencySelectService(Context context) {
        super(context);
        this.context = context;
    }

    /***
     * 查询经销商开发表所有记录
     */
    public ArrayList<MstAgencyKFM> queryDdMstAgencyKFMAll(){
        ArrayList<MstAgencyKFM> valueLst =new ArrayList<MstAgencyKFM>();
        try {

            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyKFMDao dao = (MstAgencyKFMDao) helper.getMstAgencyKFMDao();

            valueLst =dao.queryMstAgencyKFMLst(helper,"0");
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "查询图片类型表中所有记录", e);
        }
        return valueLst;
    }

    /**
     * 选择经销商的数据查询
     * @return
     */
    public List<AgencySelectStc> queryDdagencySelectLst() {
        List<AgencySelectStc> agencySelectStcLst = new ArrayList<AgencySelectStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstAgencyvisitMDao dao = helper.getDao(MstAgencyvisitM.class);
            //agencySelectStcLst = dao.agencySelectQuery(helper, ConstValues.loginSession.getGridId());
            agencySelectStcLst = dao.agencySelectQuery(helper, PrefUtils.getString(context, "gridId", ""));
        } catch (SQLException e) {
            Log.e(TAG, "选择经销商的数据查询时报错", e);
        }
        return agencySelectStcLst;
    }


}
