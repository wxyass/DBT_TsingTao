package et.tsingtaopad.home.app;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.benyoyo.manage.bs.IntStc.DataSynStc;
import et.tsingtaopad.business.first.bean.AreaGridRoute;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ZipUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DBManager;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstPictypeMDao;
import et.tsingtaopad.db.table.CmmAreaM;
import et.tsingtaopad.db.table.CmmBoardM;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MitValcheckterM;
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
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.home.initadapter.GlobalValues;
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
    private Dao<MitValcheckterM, String> mitValcheckterMDao = null;
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
            mitValcheckterMDao = helper.getMitValcheckterMDao();
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

    List<CmmDatadicM> mstDatadicMs = null;
    List<CmmAreaM> mstAreaMs = null;
    List<MstPromotionsM> mstPromotionsMs = null;
    List<MstPromoproductInfo> mstPromoproductInfos = null;
    List<MstPictypeM> mstPictypeMs = null;
    List<MstProductM> mstProductMs = null;

    List<MstCmpcompanyM> mstCmpcompanyMs = null;
    List<MstCmpbrandsM> mstCmpbrandsMs = null;
    List<MstCmproductinfoM> mstCmproductinfoMs = null;
    List<MstTerminalinfoM> mstTerminalinfoMs = null;

    List<MstAgencygridInfo> mstAgencygridInfos = null;
    List<MstAgencyinfoM> mstAgencyinfoMs = null;
    List<MstAgencysupplyInfo> mstAgencysupplyInfos = null;
    List<MstCheckexerecordInfo> mstCheckexerecordInfos = null;
    List<MstCmpsupplyInfo> mstCmpsupplyInfos = null;
    List<MstCollectionexerecordInfo> mstCollectionexerecordInfos = null;
    List<MstGroupproductM> mstGroupproductMs = null;
    List<MstPromotermInfo> mstPromotermInfos = null;
    List<MstVisitM> mstVisitMs = null;
    List<MstVistproductInfo> MstVistproductInfos = null;
    List<PadCheckstatusInfo> padCheckstatusInfos = null;
    List<MitValcheckterM> MitValcheckterMs = null;

    List<MstAgencyKFM> mstAgencyKFMs = null;
    List<MstAgencyvisitM> mstAgencyvisitMs = null;
    List<MstInvoicingInfo> mstInvoicingInfos = null;
    List<MstVisitauthorizeInfo> mstVisitauthorizeInfos = null;

    /**
     * 同步表数据
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


            // 先将之前此表中的数据全部删除(拜访的上次详情不删)  在做插入记录操作,
            /*String table = "MST_MARKETAREA_M,MST_GRID_M,MST_ROUTE_M,CMM_DATADIC_M,CMM_AREA_M,MST_PROMOTIONS_M," +
                    "MST_PROMOPRODUCT_INFO,MST_PICTYPE_M,MST_PRODUCT_M,MST_CMPCOMPANY_M,MST_CMPBRANDS_M," +
                    "MST_CMPRODUCTINFO_M,MST_TERMINALINFO_M";*/

            String table = "MST_TERMINALINFO_M";

            String upDataRoutetime = PrefUtils.getString(context, GlobalValues.ROUNTE_TIME,"");
            // 先将之前此表中的数据全部删除  在做插入记录操作,(终端表不删,之后做处理)
            if(table.equals(tablename)){
                if(!upDataRoutetime.equals(DateUtil.getDateTimeStr(7))){
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("DELETE FROM "+tablename);
                    database.execSQL(buffer.toString());
                }
            }else{
                StringBuffer buffer = new StringBuffer();
                buffer.append("DELETE FROM "+tablename);
                database.execSQL(buffer.toString());
            }



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
            else if(mClass.contains("CmmDatadicM")){
                mstDatadicMs= (List<CmmDatadicM>) JsonUtil.parseList(json, cls);
                updateData(cmmDatadicMDao, mstDatadicMs);
            }
            else if(mClass.contains("CmmAreaM")){
                mstAreaMs= (List<CmmAreaM>) JsonUtil.parseList(json, cls);
                updateData(cmmAreaMDao, mstAreaMs);
            }
            else if(mClass.contains("MstPromotionsM")){
                mstPromotionsMs= (List<MstPromotionsM>) JsonUtil.parseList(json, cls);
                updateData(mstPromotionsMDao, mstPromotionsMs);
            }
            else if(mClass.contains("MstPromoproductInfo")){
                mstPromoproductInfos= (List<MstPromoproductInfo>) JsonUtil.parseList(json, cls);
                updateData(mstPromoproductInfoDao, mstPromoproductInfos);
            }
            else if(mClass.contains("MstPictypeM")){
                mstPictypeMs= (List<MstPictypeM>) JsonUtil.parseList(json, cls);
                updateData(mstpictypeMDao, mstPictypeMs);
            }
            else if(mClass.contains("MstProductM")){
                mstProductMs= (List<MstProductM>) JsonUtil.parseList(json, cls);
                updateData(mstProductMDao, mstProductMs);
            }

            else if(mClass.contains("MstCmpcompanyM")){
                mstCmpcompanyMs= (List<MstCmpcompanyM>) JsonUtil.parseList(json, cls);
                updateData(mstCmpcompanyMDao, mstCmpcompanyMs);
            }
            else if(mClass.contains("MstCmpbrandsM")){
                mstCmpbrandsMs= (List<MstCmpbrandsM>) JsonUtil.parseList(json, cls);
                updateData(mstCmpbrandsMDao, mstCmpbrandsMs);
            }
            else if(mClass.contains("MstCmproductinfoM")){
                mstCmproductinfoMs= (List<MstCmproductinfoM>) JsonUtil.parseList(json, cls);
                updateData(mstCmproductinfoMDao, mstCmproductinfoMs);
            }
            else if(mClass.contains("MstTerminalinfoM")){
                mstTerminalinfoMs= (List<MstTerminalinfoM>) JsonUtil.parseList(json, cls);
                updateData(mstTerminalinfoMDao, mstTerminalinfoMs);
            }
            else if(mClass.contains("PadCheckstatusInfo")){
                padCheckstatusInfos= (List<PadCheckstatusInfo>) JsonUtil.parseList(json, cls);
                updateData(padCheckstatusInfoDao, padCheckstatusInfos);
            }
            // 上次终端拜访记录----------------------------------------------------------------------
            else if(mClass.contains("MstAgencygridInfo")){
                mstAgencygridInfos= (List<MstAgencygridInfo>) JsonUtil.parseList(json, cls);
                updateData(mstAgencygridInfoDao, mstAgencygridInfos);
            }
            else if(mClass.contains("MstAgencyinfoM")){
                mstAgencyinfoMs= (List<MstAgencyinfoM>) JsonUtil.parseList(json, cls);
                updateData(mstAgencyinfoMDao, mstAgencyinfoMs);
            }
            else if(mClass.contains("MstAgencysupplyInfo")){
                mstAgencysupplyInfos= (List<MstAgencysupplyInfo>) JsonUtil.parseList(json, cls);
                updateData(mstAgencysupplyInfoDao, mstAgencysupplyInfos);
            }
            else if(mClass.contains("MstCheckexerecordInfo")){
                mstCheckexerecordInfos= (List<MstCheckexerecordInfo>) JsonUtil.parseList(json, cls);
                updateData(mstCheckexerecordInfoDao, mstCheckexerecordInfos);
            }
            else if(mClass.contains("MstCmpsupplyInfo")){
                mstCmpsupplyInfos= (List<MstCmpsupplyInfo>) JsonUtil.parseList(json, cls);
                updateData(mstCmpsupplyInfoDao, mstCmpsupplyInfos);
            }
            else if(mClass.contains("MstCollectionexerecordInfo")){
                mstCollectionexerecordInfos= (List<MstCollectionexerecordInfo>) JsonUtil.parseList(json, cls);
                updateData(mstCollectionexerecordInfoDao, mstCollectionexerecordInfos);
            }
            else if(mClass.contains("MstGroupproductM")){
                mstGroupproductMs= (List<MstGroupproductM>) JsonUtil.parseList(json, cls);
                updateData(mstGroupproductMDao, mstGroupproductMs);
            }
            else if(mClass.contains("MstPromotermInfo")){
                mstPromotermInfos= (List<MstPromotermInfo>) JsonUtil.parseList(json, cls);
                updateData(mstPromotermInfoDao, mstPromotermInfos);
            }
            else if(mClass.contains("MstVisitM")){
                mstVisitMs= (List<MstVisitM>) JsonUtil.parseList(json, cls);
                updateData(mstVisitMDao, mstVisitMs);
            }
            else if(mClass.contains("MstVistproductInfo")){
                MstVistproductInfos= (List<MstVistproductInfo>) JsonUtil.parseList(json, cls);
                updateData(mstVistproductInfoDao, MstVistproductInfos);
            }
            else if(mClass.contains("MitValcheckterM")){
                MitValcheckterMs= (List<MitValcheckterM>) JsonUtil.parseList(json, cls);
                updateData(mitValcheckterMDao, MitValcheckterMs);
            }

            else if(mClass.contains("MstAgencyKFM")){
                mstAgencyKFMs= (List<MstAgencyKFM>) JsonUtil.parseList(json, cls);
                updateData(mstAgencyKFMDao, mstAgencyKFMs);
            }
            else if(mClass.contains("MstAgencyvisitM")){
                mstAgencyvisitMs= (List<MstAgencyvisitM>) JsonUtil.parseList(json, cls);
                updateData(mstAgencyvisitMDao, mstAgencyvisitMs);
            }
            else if(mClass.contains("MstInvoicingInfo")){
                mstInvoicingInfos= (List<MstInvoicingInfo>) JsonUtil.parseList(json, cls);
                updateData(mstInvoicingInfoDao, mstInvoicingInfos);
            }
            else if(mClass.contains("MstVisitauthorizeInfo")){
                mstVisitauthorizeInfos= (List<MstVisitauthorizeInfo>) JsonUtil.parseList(json, cls);
                updateData(mstVisitauthorizeInfoDao, mstVisitauthorizeInfos);
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

    // 保存指标模板
    public void  parsePadCheckType(String strPAD_CHECKTYPE_M){

        Log.e(TAG, "createOrUpdateTable");
        AndroidDatabaseConnection connection = null;
        try {
            SQLiteDatabase database = helper.getWritableDatabase();
            connection = new AndroidDatabaseConnection(database, true);
            connection.setAutoCommit(false);
            Log.e(TAG, "createOrUpdateTable 1 transation");

            if (strPAD_CHECKTYPE_M != null && !"".equals(strPAD_CHECKTYPE_M.trim())) {
                Map<String, Object> qudaoMap = JsonUtil.parseMap(strPAD_CHECKTYPE_M);// 有数
                Set<Map.Entry<String, Object>> qudaoEntrySet = qudaoMap.entrySet();
                Iterator<Map.Entry<String, Object>> iterator = qudaoEntrySet.iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, Object> next = iterator.next();
                    String minorchannel = next.getKey();// 取出所有渠道号
                    Log.i(TAG, "渠道号码" + minorchannel);
                    // 取出表数据更新数据
                    String childTableStr = (String) next.getValue();
                    Map<String, Object> qudaoChilidTableMaps = JsonUtil.parseMap(childTableStr);
                    String deleteFlag = (String) qudaoChilidTableMaps.get("DELETEFLAG");
                    Log.e(TAG, "deleteFlag=" + deleteFlag);

                    if ("1".equals(deleteFlag)) {// web端写死了恒等于1
                        // 根据渠道号删除数据
                        String sql_delete_PAD_CHECKPRO_INFO = "delete from PAD_CHECKPRO_INFO where minorchannel = ?";
                        String sql_delete_PAD_CHECKACCOMPLISH_INFO = "delete from PAD_CHECKACCOMPLISH_INFO where minorchannel = ?";
                        String sql_delete_PAD_CHECKTYPE_M = "delete from PAD_CHECKTYPE_M where minorchannel = ?";

                        String deleteArgs[] = { minorchannel };
                        if (database.isOpen()) {
                            database.execSQL(sql_delete_PAD_CHECKPRO_INFO, deleteArgs);
                            database.execSQL(sql_delete_PAD_CHECKACCOMPLISH_INFO, deleteArgs);
                            database.execSQL(sql_delete_PAD_CHECKTYPE_M, deleteArgs);
                        }

                    }

                    String childPAD_CHECKTYPE_M = (String) qudaoChilidTableMaps.get("PAD_CHECKTYPE_M");
                    List<PadChecktypeM> padChecktypeMs = JsonUtil.parseList(childPAD_CHECKTYPE_M,PadChecktypeM.class);
                    String strPAD_CHECKACCOMPLISH_INFO = (String) qudaoChilidTableMaps.get("PAD_CHECKACCOMPLISH_INFO");
                    List<PadCheckaccomplishInfo> padCheckaccomplishInfos = JsonUtil.parseList(strPAD_CHECKACCOMPLISH_INFO, PadCheckaccomplishInfo.class);
                    String strPAD_CHECKPRO_INFO = (String) qudaoChilidTableMaps.get("PAD_CHECKPRO_INFO");
                    List<PadCheckproInfo> padCheckproInfos = JsonUtil.parseList(strPAD_CHECKPRO_INFO,PadCheckproInfo.class);

                    // 更新主表
                    if (padChecktypeMs != null && !padChecktypeMs.isEmpty()) {
                        // updateData(padChecktypeMDao, padChecktypeMs);
                        saveData(padChecktypeMDao, padChecktypeMs, true);
                    }

                    // PAD_CHECKACCOMPLISH_INFO(各类指标状态达成设置)
                    if (padCheckaccomplishInfos != null && !padCheckaccomplishInfos.isEmpty()) {
                        // updateData(padCheckaccomplishInfoDao,
                        // padCheckaccomplishInfos);
                        saveData(padCheckaccomplishInfoDao, padCheckaccomplishInfos, true);
                    }
                    if (padCheckproInfos != null && !padCheckproInfos.isEmpty()) {
                        // PAD_CHECKPRO_INFO(PAD端指标关联产品表)
                        // updateData(padCheckproInfoDao,
                        // padCheckproInfos);
                        saveData(padCheckproInfoDao, padCheckproInfos, true);
                    }
                }

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

    /**
     * 保存数据
     *
     * @param dao
     * @param datas
     * @param isFull
     *            是否全量
     * @throws SQLException
     */
    private <T> void saveData(Dao<T, String> dao, List<T> datas, boolean isFull) throws SQLException {

        if (!CheckUtil.IsEmpty(datas)) {
            Log.i("updateData", "更新 " + dao.getDataClass().getName() + " size :" + datas.size());
            for (int i = 0; i < datas.size(); i++) {
                T data = datas.get(i);
                setPadisconsistentAndUploadFlag(data);
                try {
                    dao.create(data);
                } catch (Exception e) {
                    DbtLog.write(e.getMessage());
					/*MstCheckexerecordInfo ds =(MstCheckexerecordInfo)data;
					DbtLog.write(ds.getRecordkey());*/
                    e.printStackTrace();
                    dao.createOrUpdate(data);
                }
            }
        } else {
            String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
            Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为：" + listStatus);
        }
    }

    // 解析某个(或多个)终端上次拜访详情
    public void parseTermDetailInfoJson(String json) {
        // 解析区域定格路线信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String MST_AGENCYGRID_INFO = emp.getMST_AGENCYGRID_INFO();
        String MST_AGENCYINFO_M = emp.getMST_AGENCYINFO_M();
        String MST_AGENCYSUPPLY_INFO = emp.getMST_AGENCYSUPPLY_INFO();
        String MST_CHECKEXERECORD_INFO = emp.getMST_CHECKEXERECORD_INFO();
        String MST_CMPSUPPLY_INFO = emp.getMST_CMPSUPPLY_INFO();
        String MST_COLLECTIONEXERECORD_INFO = emp.getMST_COLLECTIONEXERECORD_INFO();
        String MST_GROUPPRODUCT_M = emp.getMST_GROUPPRODUCT_M();
        String MST_PROMOTERM_INFO = emp.getMST_PROMOTERM_INFO();
        String MST_VISIT_M = emp.getMST_VISIT_M();
        String MST_VISTPRODUCT_INFO = emp.getMST_VISTPRODUCT_INFO();

        createOrUpdateTable(MST_AGENCYGRID_INFO, "MST_AGENCYGRID_INFO", MstAgencygridInfo.class);
        createOrUpdateTable(MST_AGENCYINFO_M, "MST_AGENCYINFO_M", MstAgencyinfoM.class);
        createOrUpdateTable(MST_AGENCYSUPPLY_INFO, "MST_AGENCYSUPPLY_INFO", MstAgencysupplyInfo.class);
        createOrUpdateTable(MST_CHECKEXERECORD_INFO, "MST_CHECKEXERECORD_INFO", MstCheckexerecordInfo.class);
        createOrUpdateTable(MST_CMPSUPPLY_INFO, "MST_CMPSUPPLY_INFO", MstCmpsupplyInfo.class);
        createOrUpdateTable(MST_COLLECTIONEXERECORD_INFO, "MST_COLLECTIONEXERECORD_INFO", MstCollectionexerecordInfo.class);
        createOrUpdateTable(MST_GROUPPRODUCT_M, "MST_GROUPPRODUCT_M", MstGroupproductM.class);
        createOrUpdateTable(MST_PROMOTERM_INFO, "MST_PROMOTERM_INFO", MstPromotermInfo.class);
        createOrUpdateTable(MST_VISIT_M, "MST_VISIT_M", MstVisitM.class);
        createOrUpdateTable(MST_VISTPRODUCT_INFO, "MST_VISTPRODUCT_INFO", MstVistproductInfo.class);
    }

}
