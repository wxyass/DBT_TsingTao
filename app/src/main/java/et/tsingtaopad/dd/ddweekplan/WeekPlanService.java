package et.tsingtaopad.dd.ddweekplan;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.pinyin.PinYin4jUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitPlandayvalMDao;
import et.tsingtaopad.db.dao.MstAgencyinfoMDao;
import et.tsingtaopad.db.dao.MstAgencyvisitMDao;
import et.tsingtaopad.db.table.MitAgencynumM;
import et.tsingtaopad.db.table.MitAgencyproM;
import et.tsingtaopad.db.table.MitPlandayM;
import et.tsingtaopad.db.table.MitPlandaydetailM;
import et.tsingtaopad.db.table.MitPlandayvalM;
import et.tsingtaopad.db.table.MitPlanweekM;
import et.tsingtaopad.db.table.MitValcheckitemMTemp;
import et.tsingtaopad.db.table.MitValchecktypeMTemp;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstAgencytransferInfo;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstInvoicingInfo;
import et.tsingtaopad.dd.ddagencycheck.domain.InOutSaveStc;
import et.tsingtaopad.dd.ddagencycheck.domain.ZsInOutSaveStc;
import et.tsingtaopad.dd.ddweekplan.domain.DayDetailStc;
import et.tsingtaopad.dd.ddweekplan.domain.DayPlanStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.agencyvisit.domain.AgencySelectStc;
import et.tsingtaopad.main.visit.agencyvisit.domain.TransferStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 原因 BUG号 修改人 修改版本</br>
 */
@SuppressLint("DefaultLocale")
public class WeekPlanService {

    private final String TAG = "WeekPlanService";

    private Context context;

    public WeekPlanService(Context context) {
        this.context = context;
    }


    public void saveTable(MitPlanweekM weekplan,
                          DayPlanStc workplanstc,
                          List<DayDetailStc> dayDetailStcs,
                          String weekDateStart,
                          String weekDateEnd) {
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MitPlanweekM, String> mitPlanweekMDao = helper.getMitPlanweekMDao();
            Dao<MitPlandayM, String> mitPlandayMDao = helper.getMitPlandayMDao();
            Dao<MitPlandaydetailM, String> mitPlandaydetailMDao = helper.getMitPlandaydetailMDao();
            Dao<MitPlandayvalM, String> mitPlandayvalMDao = helper.getMitPlandayvalMDao();
            MitPlandayvalMDao mitPlandayvalMDao2 = helper.getDao(MitPlandayvalM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            MitPlanweekM mitPlanweekM = null;
            // 周计划主表
            if(weekplan == null){
                mitPlanweekM = new MitPlanweekM();
                mitPlanweekM.setId(FunUtil.getUUID());
                mitPlanweekM.setStarttime(weekDateStart);
                mitPlanweekM.setEndtime(weekDateEnd);

                mitPlanweekM.setCredate(new Date());
                mitPlanweekM.setUpdatedate(new Date());
                mitPlanweekM.setCreuserareaid(PrefUtils.getString(context,"departmentid",""));
                mitPlanweekM.setCreuser(PrefUtils.getString(context,"userid",""));
                mitPlanweekM.setUpdateuser(PrefUtils.getString(context,"userid",""));
                mitPlanweekMDao.create(mitPlanweekM);
            }else{
                mitPlanweekM = weekplan;
            }

            // 日计划主表
            MitPlandayM mitPlandayM = new MitPlandayM();
            mitPlandayM.setId(workplanstc.getPlanKey());// 日计划主键
            mitPlandayM.setPlanweekid(mitPlanweekM.getId());// 周计划主键
            mitPlandayM.setPlandate(workplanstc.getPlandate());// 计划时间
            mitPlandayM.setStatus("1");// // 0:未制定 1等待提交 2待审核 3 审核通过  4 未通过
            mitPlandayMDao.createOrUpdate(mitPlandayM);



            // 日计划详情表
            MitPlandaydetailM mitPlandaydetailM;
            for (DayDetailStc indexItem : dayDetailStcs) {
                mitPlandaydetailM = new MitPlandaydetailM();
                if(indexItem.getDetailkey() == null || "".equals(indexItem.getDetailkey())){
                    mitPlandaydetailM.setId(FunUtil.getUUID());
                }else{
                    mitPlandaydetailM.setId(indexItem.getDetailkey());
                }
                mitPlandaydetailM.setPlanweekid(mitPlanweekM.getId());// // 周计划主键
                mitPlandaydetailM.setPlandayid(mitPlandayM.getId());// 日计划主键
                mitPlandaydetailM.setPlanareaid(indexItem.getValareakey());// 追溯区域
                mitPlandaydetailM.setPlangridid(indexItem.getValgridkey());// 追溯定格
                mitPlandaydetailMDao.createOrUpdate(mitPlandaydetailM);

                // 删除 日计划详情表附表  (在下面会重新添加)
                mitPlandayvalMDao2.deleteByPlanweekid(helper,mitPlandaydetailM.getId());

                // 日计划详情表附表
                MitPlandayvalM mitPlandayvalM;
                List<String> valchecknameLv = indexItem.getValchecknameLv();
                for (String s : valchecknameLv){
                    mitPlandayvalM = new MitPlandayvalM();
                    mitPlandayvalM.setId(FunUtil.getUUID());
                    mitPlandayvalM.setPlanweekid(mitPlanweekM.getId());// // 周计划主键
                    mitPlandayvalM.setPlandaydetailid(mitPlandaydetailM.getId());// 日计划详情主键
                    mitPlandayvalM.setPlancomtype("0");// 0是追溯项1是路线
                    mitPlandayvalM.setPlanareaid(indexItem.getValareakey());// 追溯区域
                    mitPlandayvalM.setPlangridid(indexItem.getValgridkey());// 追溯定格
                    mitPlandayvalM.setPlancomid(s);
                    mitPlandayvalMDao.createOrUpdate(mitPlandayvalM);
                }
                String valroutekeys = indexItem.getValroutekeys();
                String[] valroutes = valroutekeys.split(",");
                for (String s : valroutes){
                    mitPlandayvalM = new MitPlandayvalM();
                    mitPlandayvalM.setId(FunUtil.getUUID());
                    mitPlandayvalM.setPlanweekid(mitPlanweekM.getId());// // 周计划主键
                    mitPlandayvalM.setPlandaydetailid(mitPlandaydetailM.getId());// 日计划详情主键
                    mitPlandayvalM.setPlancomtype("1");// 0是追溯项1是路线
                    mitPlandayvalM.setPlanareaid(indexItem.getValareakey());// 追溯区域
                    mitPlandayvalM.setPlangridid(indexItem.getValgridkey());// 追溯定格
                    mitPlandayvalM.setPlancomid(s);
                    mitPlandayvalMDao.createOrUpdate(mitPlandayvalM);
                }
            }
            connection.commit(null);
        } catch (Exception e) {
            Log.e(TAG, "保存追溯查指标数据发生异常", e);
            //ViewUtil.sendMsg(context, R.string.checkindex_save_fail);
            try {
                connection.rollback(null);
            } catch (SQLException e1) {
                Log.e(TAG, "回滚追溯查指标数据发生异常", e1);
            }
        }
    }
}
