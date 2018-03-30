package et.tsingtaopad.dd.ddxt.checking;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCheckexerecordInfoDao;
import et.tsingtaopad.db.dao.MstGroupproductMDao;
import et.tsingtaopad.db.dao.MstPromotionsmDao;
import et.tsingtaopad.db.dao.MstVistproductInfoDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexQuicklyStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtCheckIndexService extends XtShopVisitService {
    
    private final String TAG = "XtCheckIndexService";
    
    public XtCheckIndexService(Context context, Handler handler) {
        super(context, handler);
    }

    /**
     * 初始化指标、指标值树级关系 对象
     */
    public void initCheckTypeStatus() {
        List<KvStc> checkTypeStatusList=new ArrayList<KvStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            PadChecktypeMDao dao = helper.getDao(PadChecktypeM.class);
            checkTypeStatusList= dao.queryCheckTypeStatus(helper, ConstValues.FLAG_0);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访指标失败", e);
            e.printStackTrace();
        }
        GlobalValues.indexLst = checkTypeStatusList;
    }

    /**
     * 获取协同拜访-查指标的促销活动页面部分的数据
     *
     * @param //prevVisitId 拜访ID
     * @param channelId     本次拜访终端的次渠道ID
     * @param termLevel     本次拜访终端的终端类型ID(终端等级)
     * @return
     */
    public List<CheckIndexPromotionStc> queryPromotion(String visitId, String channelId, String termLevel) {
        // 获取分项采集数据
        List<CheckIndexPromotionStc> stcLst = new ArrayList<CheckIndexPromotionStc>();
        AndroidDatabaseConnection connection = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPromotionsmDao dao = helper.getDao(MstPromotionsM.class);
            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);
            stcLst = dao.queryXtPromotionByterm(helper, visitId, channelId, termLevel);
            connection.commit(null);
        } catch (SQLException e) {
            Log.e(TAG, "获取拜访产品-我品竞品表DAO对象失败", e);
        }

        // 处理组合进店的情况
        List<CheckIndexPromotionStc> tempLst = new ArrayList<CheckIndexPromotionStc>();
        String promotionKey = "";
        CheckIndexPromotionStc itemStc = new CheckIndexPromotionStc();
        for (CheckIndexPromotionStc item : stcLst) {
            if (!promotionKey.equals(item.getPromotKey())) {
                promotionKey = item.getPromotKey();
                itemStc = item;
                tempLst.add(itemStc);
            } else {
                itemStc.setProId(itemStc.getProId() + "," + item.getProId());
                itemStc.setProName(itemStc.getProName() + "\n" + item.getProName());
            }

        }

        return tempLst;
    }

}
