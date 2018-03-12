package et.tsingtaopad.main.visit.syncdata;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DBManager;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.table.CmmAreaM;
import et.tsingtaopad.db.table.CmmBoardM;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.db.table.MstAgencygridInfo;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencytransferInfo;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstCheckexerecordInfo;
import et.tsingtaopad.db.table.MstCheckmiddleInfo;
import et.tsingtaopad.db.table.MstCmpagencyInfo;
import et.tsingtaopad.db.table.MstCmpbrandsM;
import et.tsingtaopad.db.table.MstCmpcompanyM;
import et.tsingtaopad.db.table.MstCmproductinfoM;
import et.tsingtaopad.db.table.MstCmpsupplyInfo;
import et.tsingtaopad.db.table.MstCollectionexerecordInfo;
import et.tsingtaopad.db.table.MstGroupproductM;
import et.tsingtaopad.db.table.MstInvoicingInfo;
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
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：ParseLoadData.java</br> 作者：wangshiming </br>
 * 创建时间：2014年1月10日</br> 功能描述: 数据下载</br> 版本 V 1.0</br> 修改履历加功能 xxM_delete 得到ids
 * 删除主表数据 删除字表数据</br> 日期 原因 BUG号 修改人 修改版本</br>
 */
@SuppressLint({"DefaultLocale", "NewApi"})
public class DownLoadService {
    private final String TAG = "DownLoadService";

    private int threadCount;
    private Context context;
    private DatabaseHelper helper;
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
    private Dao<MstRouteM, String> mstRouteMDao = null;
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

    private List<MstSynckvM> synTables = new ArrayList<MstSynckvM>();
    private DBManager dBManager = null;
    // MST_CMPAGENCY_INFO(竞品供应商管理信息表)
    // MST_PRODUCTSHOW_M(产品展示信息主表)
    // MST_SHOWPIC_INFO(展示图片信息表)
    private long defaultDelayMillis = 1000;
    private String synctime;// 点击同步记录时间
    private Handler handler;
    private String zipPath;
    private boolean zipFlag = false;
    /*** 是否同步过操作 ***/
    private boolean isDownLoadFlag = false;

    public DownLoadService(Context context, Handler handler) {
        this.context = context;
        this.handler = handler;
        helper = DatabaseHelper.getHelper(context);
        dBManager = DBManager.getDBManager(context);
        Date date = new Date();
        synctime = DateUtil.formatDate(date, "yyyy-MM-dd HH:mm:ss");
        String padxinghao = android.os.Build.MODEL;// 根据pad型号,设置同步数据时的线程数(老pad起一个线程)
        if (padxinghao.contains("2207") || padxinghao.contains("2007") || padxinghao.contains("3000")) {
            threadCount = 1;
        } else {
            threadCount = 1;
        }
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
            mstRouteMDao = helper.getMstRouteMDao();
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

    long start;

    // 同步下载
    public void asyndatas() {
        // 获取上次同步成功日期  PrefUtils.
        String datasynTime = PrefUtils.getString(context, "datasynTime", "");
        // 判断字符串是否为空或者null
        if (!CheckUtil.isBlankOrNull(datasynTime)) {
            isDownLoadFlag = true;
        }
        DbtLog.write("同步时间：" + datasynTime + ",是否同步：" + isDownLoadFlag);
        start = System.currentTimeMillis();

        try {
            synTables = mstSynckvMDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // 下载数据
        downloadDate(synTables);

    }

    public void downloadDate(List<MstSynckvM> synTables) {
        // 更新前时间
        final Long beforeTime = new Date().getTime();
        for (final MstSynckvM adatasyn : synTables) {
            short exceptionCount = 0; // 连接次数 连接失败后连接3次
            Map<String, String> requestHashMap = new HashMap<String, String>();
            requestHashMap.put("userId", PrefUtils.getString(context, "userGongHao", ""));
            requestHashMap.put("areaId", PrefUtils.getString(context, "disId", ""));
            requestHashMap.put("gridKey", PrefUtils.getString(context, "gridId", ""));
            requestHashMap.put("tablename", adatasyn.getTablename());// 表名
            requestHashMap.put("syncDay", adatasyn.getSyncDay());// 要更新的数据时间埃范围(天)
            requestHashMap.put("synctime", adatasyn.getSynctime());// 表最新一次单击更新按钮并成功更新的时间,格式：yyyy-MM-dd HH:mm:ss
            requestHashMap.put("remarks", adatasyn.getRemarks());
            requestHashMap.put("updatetime", adatasyn.getUpdatetime());// 表中最新数据的updatetime，格式：yyyy-MM-dd HH:mm:ss

            String json = JsonUtil.toJson(requestHashMap);

            if (json.contains("PAD_CHECKTYPE_M")) {

            }
            // 组建请求Json
            RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(context);
            requestHeadStc.setOptcode("get_date_3");
            RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, json);

            // 压缩请求数据
            String jsonZip = HttpParseJson.parseRequestJson(reqObj);

            RestClient.builder()
                    .url(HttpUrl.IP_END)
                    .params("data", jsonZip)
                    //.loader(context)
                    .success(new ISuccess() {
                        @Override
                        public void onSuccess(String response) {
                            String json  = HttpParseJson.parseJsonResToString(response);
                            ResponseStructBean resObj = new ResponseStructBean();
                            resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                            // 解析表名
                            String tableName = resObj.getResHead().getContant();
                            // 同步保存数据
                            saveDataFromNet(tableName,resObj);
                        }
                    })
                    .error(new IError() {
                        @Override
                        public void onError(int code, String msg) {
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                        }
                    })
                    .failure(new IFailure() {
                        @Override
                        public void onFailure() {
                            Toast.makeText(context, "请求失败", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .builde()
                    .post();

        }
    }

    //  同步保存数据
    private void saveDataFromNet(String tableName, ResponseStructBean resObj) {
        String json = resObj.getResBody().getContent();
        // 路线表 新增或更新
        if("MST_ROUTE_M".equals(tableName)){
            createOrUpdateMstRouteM(tableName,json);
        }
        // 终端表 新增或更新
        else if("MST_TERMINALINFO_M".equals(tableName)){
            createOrUpdateMstTerminalinfoM(tableName,json);
        }
    }

    // 路线表 新增或更新
    private void createOrUpdateMstRouteM(String tableName,String content) {
        AndroidDatabaseConnection connection = null;
        try {
            SQLiteDatabase database = helper.getWritableDatabase();
            connection = new AndroidDatabaseConnection(database, true);
            connection.setAutoCommit(false);
            // 对象列表
            List<MstRouteM> mstRouteMs = JsonUtil.parseList(content, MstRouteM.class);
            // 删除表中的数据
            StringBuffer buffer = new StringBuffer();
            buffer.append("DELETE FROM MST_ROUTE_M WHERE padisconsistent =  ?  ");
            database.execSQL(buffer.toString(), new String[]{"1"});
            // 更新 新的数据
            saveData(mstRouteMDao, mstRouteMs, true);
            int a = 10/0;
            // 发送成功的标记
            sendProgressMessage(tableName);

            connection.commit(null);
        }catch (Exception e) {
            try {
                connection.rollback(null);
                sendFailureMessage("路线表同步出错");
                Log.e(TAG, "createOrUpdateMstRouteM rollback transataion", e);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            //throw new RuntimeException("更新路线表出现错误" + e.toString());
        }
    }

    // 新增或更新终端表
    private void createOrUpdateMstTerminalinfoM(String tableName,String content) {
        AndroidDatabaseConnection connection = null;
        try {
            SQLiteDatabase database = helper.getWritableDatabase();
            connection = new AndroidDatabaseConnection(database, true);
            connection.setAutoCommit(false);
            // 对象列表
            List<MstTerminalinfoM> mstTerminalinfoMs = JsonUtil.parseList(content, MstTerminalinfoM.class);
            // 删除表中的数据
            StringBuffer buffer = new StringBuffer();
            buffer.append("DELETE FROM MST_TERMINALINFO_M WHERE padisconsistent =  ?  ");
            database.execSQL(buffer.toString(), new String[]{"1"});
            // 更新 新的数据
            saveData(mstTerminalinfoMDao, mstTerminalinfoMs, true);
            // 发送成功的标记
            sendProgressMessage(tableName);

            connection.commit(null);
        }catch (Exception e) {
            try {
                connection.rollback(null);
                sendFailureMessage("路线表同步出错");
                Log.e(TAG, "createOrUpdateTerminal rollback transataion", e);
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            throw new RuntimeException("更新终端档案表出现错误" + e.toString());
        }
    }

    /**
     * 保存数据
     *
     * @param dao
     * @param datas
     * @param isFull 是否全量
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
                    e.printStackTrace();
                    dao.createOrUpdate(data);
                }
            }
        } else {
            String listStatus = datas == null ? "传入对象为：null" : "数据size = 0";
            Log.i("updateData", "更新list " + dao.getDataClass().getName() + "为：" + listStatus);
        }
    }

    @SuppressWarnings("unused")
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

    int progress = 1;
    /**
     * 向界面发送进度条信息
     */
    private synchronized void sendProgressMessage(String tableName) {
        Log.e("tablename", tableName);
        for (MstSynckvM mstSynckv : synTables) {
            if (mstSynckv.getTablename().equals(tableName)) {
                // 根据表名找出是什么表
                tableName = mstSynckv.getTableDesc();
            }
        }
        Log.d(TAG, "progress:" + progress);

        DbtLog.write("progress:" + progress + ",synTables.size():" + synTables.size());
        if (progress == synTables.size()) {
            // 保存同步成功的时间
            String dates = DateUtil.formatDate(new Date(), DateUtil.DEFAULT_DATE_FORMAT);
            PrefUtils.putString(context, "datasynTime", dates);
            // 成功 界面改变
            sendSuccessMessage();
        }else{
            Message msg = new Message();
            Bundle bundler = new Bundle();
            bundler.putInt("progress", progress++);
            bundler.putInt("result", 0);
            bundler.putString("progressStr", tableName);

            msg.setData(bundler);
            msg.what = DownLoadCoreFragment.SYNDATA_PROGRESS;
            handler.sendMessageDelayed(msg, defaultDelayMillis);
        }
    }

    /**
     * 向界面发送手动同步成功的信息
     */
    private void sendSuccessMessage() {
        Message message = new Message();
        message.what = DownLoadCoreFragment.SYNDATA_RESULT_Success;// 发送结束标志
        message.obj = "同步数据完成";
        handler.sendMessage(message);
    }

    /**
     * 向界面发送失败信息
     */
    private void sendFailureMessage(String msg) {
        Message message = new Message();
        message.obj = msg;
        message.what = DownLoadCoreFragment.SYNDATA_RESULT_Failure;
        handler.sendMessage(message);
    }

}
