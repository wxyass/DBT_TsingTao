package et.tsingtaopad.home.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.com.benyoyo.manage.bs.IntStc.DataSynStc;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.ZipUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DBManager;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstPictypeMDao;
import et.tsingtaopad.db.table.CmmAreaM;
import et.tsingtaopad.db.table.CmmBoardM;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.db.table.MstAgencygridInfo;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencytransferInfo;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstCameraInfoMTemp;
import et.tsingtaopad.db.table.MstCheckexerecordInfo;
import et.tsingtaopad.db.table.MstCheckmiddleInfo;
import et.tsingtaopad.db.table.MstCmpagencyInfo;
import et.tsingtaopad.db.table.MstCmpbrandsM;
import et.tsingtaopad.db.table.MstCmpcompanyM;
import et.tsingtaopad.db.table.MstCmproductinfoM;
import et.tsingtaopad.db.table.MstCmpsupplyInfo;
import et.tsingtaopad.db.table.MstCollectionexerecordInfo;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstGroupproductM;
import et.tsingtaopad.db.table.MstInvoicingInfo;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstMonthtargetInfo;
import et.tsingtaopad.db.table.MstPictypeM;
import et.tsingtaopad.db.table.MstPlanTerminalM;
import et.tsingtaopad.db.table.MstPlanWeekforuserM;
import et.tsingtaopad.db.table.MstPlancheckInfo;
import et.tsingtaopad.db.table.MstPlancollectionInfo;
import et.tsingtaopad.db.table.MstPlanforuserM;
import et.tsingtaopad.db.table.MstPlanrouteInfo;
import et.tsingtaopad.db.table.MstPowerfulchannelInfo;
import et.tsingtaopad.db.table.MstPowerfulterminalInfo;
import et.tsingtaopad.db.table.MstPriceM;
import et.tsingtaopad.db.table.MstPricedetailsInfo;
import et.tsingtaopad.db.table.MstProductM;
import et.tsingtaopad.db.table.MstProductareaInfo;
import et.tsingtaopad.db.table.MstProductshowM;
import et.tsingtaopad.db.table.MstPromoproductInfo;
import et.tsingtaopad.db.table.MstPromotermInfo;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstPromotionstypeM;
import et.tsingtaopad.db.table.MstQuestionsanswersInfo;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstShowpicInfo;
import et.tsingtaopad.db.table.MstSynckvM;
import et.tsingtaopad.db.table.MstTermLedgerInfo;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVisitauthorizeInfo;
import et.tsingtaopad.db.table.MstVisitmemoInfo;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstWorksummaryInfo;
import et.tsingtaopad.db.table.PadCheckaccomplishInfo;
import et.tsingtaopad.db.table.PadCheckproInfo;
import et.tsingtaopad.db.table.PadCheckstatusInfo;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.db.table.PadPlantempcheckM;
import et.tsingtaopad.db.table.PadPlantempcollectionInfo;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class MainService extends XtShopVisitService {

    private final String TAG = "MainService";
    private static Context context;
    private DatabaseHelper helper;
    private DBManager dBManager = null;

    private Dao<MstVisitmemoInfo, String> mstVisitmemoInfoDao = null;
    private Dao<MstVisitM, String> mstVisitMDao = null;
    private Dao<MstQuestionsanswersInfo, String> mstQuestionsanswersInfoDao = null;
    private Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = null;
    private Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = null;
    private Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = null;
    private Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = null;
    private Dao<MstVistproductInfo, String> mstVistproductInfoDao = null;
    private Dao<MstCheckexerecordInfo, String> mstCheckexerecordInfoDao = null;
    private Dao<MstPromotermInfo, String> mstPromotermInfoDao = null;
    private Dao<MstCollectionexerecordInfo, String> mstCollectionexerecordInfoDao = null;
    private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao = null;
    private Dao<MstPlanforuserM, String> mstPlanforuserMDao = null;
    private Dao<MstPlanWeekforuserM, String> mstPlanWeekforuserMDao = null;
    private Dao<MstSynckvM, String> mstSynckvMDao = null;
    private Dao<CmmBoardM, String> cmmBoardMDao = null;
    private Dao<CmmDatadicM, String> cmmDatadicMDao = null;
    private Dao<CmmAreaM, String> cmmAreaMDao = null;
    private Dao<MstGridM, String> mstGridMDao = null;
    private Dao<MstRouteM, String> mstRouteMDao = null;
    private Dao<MstMarketareaM, String> mstMarketareaMDao = null;
    private Dao<MstAgencysupplyInfo, String> mstAgencysupplyInfoDao = null;
    private Dao<MstAgencyvisitM, String> mstAgencyvisitMDao = null;
    private Dao<MstAgencyinfoM, String> mstAgencyinfoMDao = null;
    private Dao<MstAgencygridInfo, String> mstAgencygridInfoDao = null;
    private Dao<MstPromotionsM, String> mstPromotionsMDao = null;
    private Dao<MstPromotionstypeM, String> mstPromotionstypeMDao = null;
    private Dao<MstPromoproductInfo, String> mstPromoproductInfoDao = null;
    private Dao<MstCmpbrandsM, String> mstCmpbrandsMDao = null;
    private Dao<MstCmpcompanyM, String> mstCmpcompanyMDao = null;
    private Dao<MstCmproductinfoM, String> mstCmproductinfoMDao = null;
    private Dao<MstProductM, String> mstProductMDao = null;
    private Dao<MstCheckmiddleInfo, String> mstCheckmiddleInfoDao = null;
    private Dao<MstVisitauthorizeInfo, String> mstVisitauthorizeInfoDao = null;
    private Dao<PadChecktypeM, String> padChecktypeMDao = null;
    private Dao<PadCheckstatusInfo, String> padCheckstatusInfoDao = null;
    private Dao<PadCheckaccomplishInfo, String> padCheckaccomplishInfoDao = null;
    private Dao<PadCheckproInfo, String> padCheckproInfoDao = null;
    private Dao<PadPlantempcheckM, String> padPlantempcheckMDao = null;
    private Dao<PadPlantempcollectionInfo, String> padPlantempcollectionInfoDao = null;
    private Dao<MstPowerfulterminalInfo, String> mstPowerfulterminalInfoDao = null;
    private Dao<MstPowerfulchannelInfo, String> mstPowerfulchannelInfoDao = null;
    private Dao<MstPriceM, String> mstPriceMDao = null;
    private Dao<MstPricedetailsInfo, String> mstPricedetailsInfoDao = null;
    private Dao<MstProductareaInfo, String> mstProductareaInfoDao = null;
    private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao = null;
    private Dao<MstPlanTerminalM, String> MstPlanTerminalMDao = null;
    private Dao<MstCmpsupplyInfo, String> mstCmpsupplyInfoDao = null;
    private Dao<MstPlanrouteInfo, String> mstPlanrouteInfodDao = null;
    private Dao<MstMonthtargetInfo, String> mstMonthtargetInfoDao = null;
    private Dao<MstCmpagencyInfo, String> mstCmpagencyInfoDao = null;
    private Dao<MstProductshowM, String> mstProductshowMDao = null;
    private Dao<MstShowpicInfo, String> mstShowpicInfoDao = null;
    private Dao<MstPictypeM, String> mstpictypeMDao = null;
    private Dao<MstAgencyKFM, String> mstAgencyKFMDao = null;
    public Dao<MstTermLedgerInfo, String> mstTermLedgerInfoDao = null;
    public Dao<MstGroupproductM, String> mstGroupproductMDao = null;

    public MainService(Context context, Handler handler) {
        super(context, handler);
        this.context = context;
        this.handler = handler;
        helper = DatabaseHelper.getHelper(context);
        dBManager = DBManager.getDBManager(context);

        try {
            mstVisitmemoInfoDao = helper.getMstVisitmemoInfoDao();
            mstQuestionsanswersInfoDao = helper.getMstQuestionsanswersInfoDao();
            mstVisitMDao = helper.getMstVisitMDao();
            mstWorksummaryInfoDao = helper.getMstWorksummaryInfoDao();
            mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
            mstInvoicingInfoDao = helper.getMstInvoicingInfoDao();
            mstAgencytransferInfoDao = helper.getMstAgencytransferInfoDao();
            mstVistproductInfoDao = helper.getMstVistproductInfoDao();
            mstCheckexerecordInfoDao = helper.getMstCheckexerecordInfoDao();
            mstCollectionexerecordInfoDao = helper.getMstCollectionexerecordInfoDao();
            mstPromotermInfoDao = helper.getMstPromotermInfoDao();
            mstPlancollectionInfoDao = helper.getMstPlancollectionInfoDao();
            mstPlanforuserMDao = helper.getMstPlanforuserMDao();
            mstPlanWeekforuserMDao = helper.getMstPlanWeekforuserMDao();
            mstSynckvMDao = helper.getMstSynckvMDao();
            cmmBoardMDao = helper.getCmmBoardMDao();
            cmmDatadicMDao = helper.getCmmDatadicMDao();
            cmmAreaMDao = helper.getCmmAreaMDao();
            mstGridMDao = helper.getMstGridMDao();
            mstRouteMDao = helper.getMstRouteMDao();
            mstMarketareaMDao = helper.getMstMarketareaMDao();
            mstAgencysupplyInfoDao = helper.getMstAgencysupplyInfoDao();
            mstAgencyinfoMDao = helper.getMstAgencyinfoMDao();
            mstAgencygridInfoDao = helper.getMstAgencygridInfoDao();
            mstPromotionsMDao = helper.getMstPromotionsMDao();
            mstPromotionstypeMDao = helper.getMstPromotionstypeMDao();
            mstPromoproductInfoDao = helper.getMstPromoproductInfoDao();
            mstCmpbrandsMDao = helper.getMstCmpbrandsMDao();
            mstCmproductinfoMDao = helper.getMstCmproductinfoMDao();
            mstProductMDao = helper.getMstProductMDao();
            mstCheckmiddleInfoDao = helper.getMstCheckmiddleInfoDao();
            mstVisitauthorizeInfoDao = helper.getMstVisitauthorizeInfoDao();
            padChecktypeMDao = helper.getPadChecktypeMDao();
            padCheckstatusInfoDao = helper.getPadCheckstatusInfoDao();
            padCheckaccomplishInfoDao = helper.getPadCheckaccomplishInfoDao();
            padCheckproInfoDao = helper.getPadCheckproInfoDao();
            padPlantempcheckMDao = helper.getPadPlantempcheckMDao();
            padPlantempcollectionInfoDao = helper.getPadPlantempcollectionInfoDao();
            mstAgencyvisitMDao = helper.getMstAgencyvisitMDao();
            mstCmpcompanyMDao = helper.getMstCmpcompanyMDao();
            mstPowerfulterminalInfoDao = helper.getMstPowerfulterminalInfoDao();
            mstPowerfulchannelInfoDao = helper.getMstPowerfulchannelInfoDao();
            mstPriceMDao = helper.getMstPriceMDao();
            mstPricedetailsInfoDao = helper.getMstPricedetailsInfoDao();
            mstProductareaInfoDao = helper.getMstProductareaInfoDao();
            mstPlancheckInfoDao = helper.getMstPlancheckInfoDao();
            MstPlanTerminalMDao = helper.getMstPlanTerminalM();
            mstCmpsupplyInfoDao = helper.getMstCmpsupplyInfoDao();
            mstPlanrouteInfodDao = helper.getMstPlanrouteInfoDao();
            mstMonthtargetInfoDao = helper.getMstMonthtargetInfoDao();
            mstCmpagencyInfoDao = helper.getMstCmpagencyInfoDao();
            mstProductshowMDao = helper.getMstProductshowMDao();
            mstShowpicInfoDao = helper.getMstShowpicInfoDao();

            mstpictypeMDao = helper.getMstpictypeMDao();
            mstAgencyKFMDao = helper.getMstAgencyKFMDao();

            mstAgencyKFMDao = helper.getMstAgencyKFMDao();

            mstTermLedgerInfoDao = helper.getTermLedgerInfoDao();
            mstGroupproductMDao = helper.getMstGroupproductMDao();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    List<MstMarketareaM> mstMarketareaMs = null;
    List<MstGridM> mstGridMs = null;
    List<MstRouteM> mstRouteMs = null;

    /**
     * 图片类型表更新 注意本张表并不是差量插入,而是先全部删除,再全部插入 MST_PICTYPE_M 因为数量少
     *
     * @param json
     *            .
     */
    public void createOrUpdateTable(String json, String tablename, Class<?> cls) {
        Log.e(TAG, "createOrUpdateTable");
        AndroidDatabaseConnection connection = null;
        try {
            SQLiteDatabase database = helper.getWritableDatabase();
            connection = new AndroidDatabaseConnection(database, true);
            connection.setAutoCommit(false);
            Log.e(TAG, "createOrUpdateTable 1 transation");

            // 先将之前此表中的数据全部删除, 在做插入记录操作
            StringBuffer buffer = new StringBuffer();
            buffer.append("DELETE FROM "+tablename);
            database.execSQL(buffer.toString());

            // 更新 插入
            String mClass = cls.getName();
            if(mClass.contains("MstGridM")){
                mstGridMs= (List<MstGridM>) JsonUtil.parseList(json, cls);
                updateData(mstGridMDao, mstGridMs);
            }else if(mClass.contains("MstMarketareaM")){
                mstMarketareaMs = (List<MstMarketareaM>) JsonUtil.parseList(json, cls);
                updateData(mstMarketareaMDao, mstMarketareaMs);
            }else if(mClass.contains("MstRouteM")){
                mstRouteMs= (List<MstRouteM>) JsonUtil.parseList(json, cls);
                updateData(mstRouteMDao, mstRouteMs);
            }

            connection.commit(null);
            Log.e(TAG, "createOrUpdateTable 2 transation");

        } catch (SQLException e) {
            try {
                connection.rollback(null);
                Log.e(TAG, "createOrUpdateTable 3 transation", e);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException(" 更新表出错" + e.toString());
        }
    }

    /**
     * 更新数据
     *
     * @param dao
     * @throws SQLException
     */
    public <T> void updateData(Dao<T, String> dao, List<T> datas) throws SQLException {

        if (!CheckUtil.IsEmpty(datas)) {
            Log.i("updateData", "更新 " + dao.getDataClass().getName() + " updateData676 :" + datas.size());
            for (int i = 0; i < datas.size(); i++) {
                T data = datas.get(i);
                setPadisconsistentAndUploadFlag(data);
                dao.createOrUpdate(data);
            }
        } else {
            String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
            Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为：" + listStatus);
        }
    }

    private <T> void setPadisconsistentAndUploadFlag(T data) {
        // 反射将 padisconsistent , uploadFlag;
        Class<? extends Object> clazz = data.getClass();
        try {
            Method padisconsistentMethod = data.getClass().getMethod("setPadisconsistent", String.class);
            padisconsistentMethod.invoke(data, ConstValues.FLAG_1);

        } catch (Exception e) {
            // e.printStackTrace();
        }
        // 为什么分开写？ 不写在同一个try 里面 因为 setPadisconsistent
        // 方法不存在就抛异常跳出去了而setUploadFlag可能存在的造成改方法无法执行
        try {
            Method uploadMethod = clazz.getMethod("setUploadFlag", String.class);
            uploadMethod.invoke(data, ConstValues.FLAG_1);

        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

}
