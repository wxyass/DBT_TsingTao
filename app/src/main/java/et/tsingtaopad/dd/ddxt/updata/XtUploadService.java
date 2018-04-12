package et.tsingtaopad.dd.ddxt.updata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;


import et.tsingtaopad.business.first.bean.AreaGridRoute;
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
import et.tsingtaopad.core.util.dbtutil.FileUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MitCameraInfoMDao;
import et.tsingtaopad.db.dao.MstAgencyKFMDao;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.table.MitAgencysupplyInfo;
import et.tsingtaopad.db.table.MitCameraInfoM;
import et.tsingtaopad.db.table.MitCheckexerecordInfo;
import et.tsingtaopad.db.table.MitCmpsupplyInfo;
import et.tsingtaopad.db.table.MitCollectionexerecordInfo;
import et.tsingtaopad.db.table.MitGroupproductM;
import et.tsingtaopad.db.table.MitPromotermInfo;
import et.tsingtaopad.db.table.MitTerminalinfoM;
import et.tsingtaopad.db.table.MitVisitM;
import et.tsingtaopad.db.table.MitVistproductInfo;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.db.table.MstAgencysupplyInfo;
import et.tsingtaopad.db.table.MstAgencytransferInfo;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstCheckexerecordInfo;
import et.tsingtaopad.db.table.MstCmpsupplyInfo;
import et.tsingtaopad.db.table.MstCollectionexerecordInfo;
import et.tsingtaopad.db.table.MstGroupproductM;
import et.tsingtaopad.db.table.MstInvalidapplayInfo;
import et.tsingtaopad.db.table.MstInvoicingInfo;
import et.tsingtaopad.db.table.MstPlanTerminalM;
import et.tsingtaopad.db.table.MstPlanWeekforuserM;
import et.tsingtaopad.db.table.MstPlancheckInfo;
import et.tsingtaopad.db.table.MstPlancollectionInfo;
import et.tsingtaopad.db.table.MstPlanforuserM;
import et.tsingtaopad.db.table.MstPlanrouteInfo;
import et.tsingtaopad.db.table.MstPromotermInfo;
import et.tsingtaopad.db.table.MstQuestionsanswersInfo;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVisitmemoInfo;
import et.tsingtaopad.db.table.MstVistproductInfo;
import et.tsingtaopad.db.table.MstWorksummaryInfo;

import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;
import et.tsingtaopad.view.TitleLayout;

/**
 * 
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：UploadDataService.java</br>
 * 作者：wangshiming   </br>
 * 创建时间：2013年12月20日</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class XtUploadService
{

    public DatabaseHelper helper;
    private final String TAG = "UploadDataService";
    private Context context;
    private Handler handler;
    private Dao<MstVisitmemoInfo, String> mstVisitmemoInfoDao = null;
    private Dao<MstVisitM, String> mstVisitMDao = null;
    private Dao<MitVisitM, String> mitVisitMDao = null;//1
    private Dao<MstQuestionsanswersInfo, String> mstQuestionsanswersInfoDao = null;
    private Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = null;
    private Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = null;
    private Dao<MitTerminalinfoM, String> mitTerminalinfoMDao = null;//2
    private Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = null;
    private Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = null;
    private Dao<MstVistproductInfo, String> mstVistproductInfoDao = null;
    private Dao<MitVistproductInfo, String> mitVistproductInfoDao = null;//3
    private Dao<MitCheckexerecordInfo, String> mitCheckexerecordInfoDao = null;//4
    private Dao<MstCheckexerecordInfo, String> mstCheckexerecordInfoDao = null;
    private Dao<MstInvalidapplayInfo, String> mstInvalidapplayInfoDao = null;
    private Dao<MstPromotermInfo, String> mstPromotermInfoDao = null;
    private Dao<MitPromotermInfo, String> mitPromotermInfoDao = null;//5
    private Dao<MstCollectionexerecordInfo, String> mstCollectionexerecordInfoDao = null;
    private Dao<MitCollectionexerecordInfo, String> mitCollectionexerecordInfoDao = null;//6
    private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao = null;
    private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao = null;
    private Dao<MstPlanforuserM, String> mstPlanforuserMDao = null;
    private Dao<MstPlanWeekforuserM, String> mstPlanWeekforuserMDao = null;
    private Dao<MitAgencysupplyInfo, String> mitAgencysupplyInfoDao = null;//7
    private Dao<MstAgencysupplyInfo, String> mstAgencysupplyInfoDao = null;
    private Dao<MstAgencyvisitM, String> mstAgencyvisitMDao = null;
    private Dao<MstCmpsupplyInfo, String> mstCmpsupplyInfoDao = null;//
    private Dao<MitCmpsupplyInfo, String> mitCmpsupplyInfoDao = null;//8
    private Dao<MstPlanrouteInfo, String> mstPlanrouteInfodDao = null;
    private MstCameraiInfoMDao mstCameraiInfoMDao = null;
    private MitCameraInfoMDao mitCameraInfoMDao = null;//9
    private MstAgencyKFMDao mstAgencyKFMDao = null;
    private Dao<MstPlanTerminalM, String> mstPlanTerminalMDao = null;
    private Dao<MstGroupproductM, String> mstGroupproductMDao = null;
    private Dao<MitGroupproductM, String> mitGroupproductMDao = null;//10

    

    public XtUploadService(Context context, Handler handler)
    {
        this.handler = handler;
        this.context = context;
        helper = DatabaseHelper.getHelper(context);
        try
        {
            mstVisitmemoInfoDao = helper.getMstVisitmemoInfoDao();
            mstQuestionsanswersInfoDao = helper.getMstQuestionsanswersInfoDao();
            mstVisitMDao = helper.getMstVisitMDao();
            mitVisitMDao = helper.getMitVisitMDao();
            mstWorksummaryInfoDao = helper.getMstWorksummaryInfoDao();
            mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
            mitTerminalinfoMDao = helper.getMitTerminalinfoMDao();
            mstInvoicingInfoDao = helper.getMstInvoicingInfoDao();
            mstAgencytransferInfoDao = helper.getMstAgencytransferInfoDao();
            mstVistproductInfoDao = helper.getMstVistproductInfoDao();
            mitVistproductInfoDao = helper.getMitVistproductInfoDao();
            mstCheckexerecordInfoDao = helper.getMstCheckexerecordInfoDao();
            mitCheckexerecordInfoDao = helper.getMitCheckexerecordInfoDao();
            mstInvalidapplayInfoDao = helper.getMstInvalidapplayInfoDao();
            mstCollectionexerecordInfoDao = helper.getMstCollectionexerecordInfoDao();
            mitCollectionexerecordInfoDao = helper.getMitCollectionexerecordInfoDao();
            mstPromotermInfoDao = helper.getMstPromotermInfoDao();
            mitPromotermInfoDao = helper.getMitPromotermInfoDao();
            mstPlancheckInfoDao = helper.getMstPlancheckInfoDao();
            mstPlanTerminalMDao = helper.getMstPlanTerminalM();
            
            mstPlancollectionInfoDao = helper.getMstPlancollectionInfoDao();
            mstPlanforuserMDao = helper.getMstPlanforuserMDao();
            mstPlanWeekforuserMDao = helper.getMstPlanWeekforuserMDao();
            mstAgencysupplyInfoDao = helper.getMstAgencysupplyInfoDao();
            mitAgencysupplyInfoDao = helper.getMitAgencysupplyInfoDao();
            mstAgencyvisitMDao = helper.getMstAgencyvisitMDao();
            mstCmpsupplyInfoDao = helper.getMstCmpsupplyInfoDao();
            mitCmpsupplyInfoDao = helper.getMitCmpsupplyInfoDao();
            mstPlanrouteInfodDao = helper.getMstPlanrouteInfoDao();
            mstCameraiInfoMDao = (MstCameraiInfoMDao)helper.getMstCameraiInfoMDao();
            mitCameraInfoMDao = (MitCameraInfoMDao)helper.getMitCameraInfoMDao();
            mstAgencyKFMDao = (MstAgencyKFMDao)helper.getMstAgencyKFMDao();
            mstGroupproductMDao = helper.getMstGroupproductMDao();
            mitGroupproductMDao = helper.getMitGroupproductMDao();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

/*
    List<MstVisitM> visits = new ArrayList<MstVisitM>();
    List<MstVisitM> visitsall = new ArrayList<MstVisitM>();
    List<MstVistproductInfo> mVistproductInfos = new ArrayList<MstVistproductInfo>();
    List<MstInvalidapplayInfo> mInvalidapplayInfos_visit = new ArrayList<MstInvalidapplayInfo>();
    List<MstPromotermInfo> mPromotermInfos = new ArrayList<MstPromotermInfo>();
    List<MstTerminalinfoM> mTerminalinfoMs_visit = new ArrayList<MstTerminalinfoM>();
    List<MstCheckexerecordInfo> mCheckexerecordInfos = new ArrayList<MstCheckexerecordInfo>();
    List<MstCollectionexerecordInfo> mCollectionexerecordInfos = new ArrayList<MstCollectionexerecordInfo>();
    List<MstAgencysupplyInfo> mAgencysupplyInfos = new ArrayList<MstAgencysupplyInfo>();
    List<MstCmpsupplyInfo> cmpsupplyInfos = new ArrayList<MstCmpsupplyInfo>();
    
    // 上传图片列表
	//List<UpCameraListMStc> upCameraListMStcs = new ArrayList<UpCameraListMStc>();
	// 上传产品组合是否达标
	List<MstGroupproductM> mMstGroupproductMs = new ArrayList<MstGroupproductM>();*/
	
	

    /**
     * 上传所有的巡店拜访
     *  @param isNeedExit true 的时候 上传数据后退出程序 ，不需要退出程序 请用false
     *  
     *  离线拜访修改: 
     *  	以前是针对一家终端多次离线拜访,所有边防数据都上传,但是数据都跟最后一次的数据相同
     *  	现在 针对同一家终端,一天内进行多次离线拜访,要求只上传最后一次数据
     *  	1 上传时,查询拜访主表,获取两个集合,
     *  	2 一个存放今天所有终端所有次数的拜访记录,另一个只存放所有终端的最新一次拜访记录(分组)
     *  	3 上传第二个集合对应的拜访记录数据,
     *  	4 成功上传后,依据第一个集合更改所有拜访记录的上传标记为已上传
     */
    /*
	public void upload_visit(final boolean isNeedExit, final String visitKey,final int whatId) {
        try {
            MstVisitM visit = null;
            if (visitKey != null && !visitKey.trim().equals("")) {
                visit = mstVisitMDao.queryForId(visitKey);
                if (visit != null) {
                    // String visitDate = DateUtil.formatDate(new Date(),"yyyyMMddHHmmss");
                    // visit.setEnddate(visitDate);
                    visits.add(visit);
                    visitsall.add(visit);
                    String terminalkey = visit.getTerminalkey();

                    Map<String, Object> visitKeyMap = new HashMap<String, Object>();
                    visitKeyMap.put("visitkey", visitKey);
                    visitKeyMap.put("padisconsistent", "0");

                    Map<String, Object> terminalKeyMap = new HashMap<String, Object>();
                    terminalKeyMap.put("terminalkey", terminalkey);
                    terminalKeyMap.put("padisconsistent", "0");

                    // MST_INVALIDAPPLAY_INFO(终端申请表)
                    mInvalidapplayInfos_visit = mstInvalidapplayInfoDao.queryForFieldValues(visitKeyMap);
                    // MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                    mPromotermInfos = mstPromotermInfoDao.queryForFieldValues(visitKeyMap);
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                    mVistproductInfos = mstVistproductInfoDao.queryForFieldValues(visitKeyMap);
                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                    mCollectionexerecordInfos = mstCollectionexerecordInfoDao.queryForFieldValues(visitKeyMap);
                    for (int i = 0; i < mCollectionexerecordInfos.size(); i++) {
                        if (null != mCollectionexerecordInfos.get(i).getUpdateuser()) {
                            if ("0-1".equals(mCollectionexerecordInfos.get(i).getUpdateuser().trim())) {
                                mCollectionexerecordInfos.get(i).setUpdateuser(visit.getUserid());
                            }
                        }
                    }
                    // MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                    mTerminalinfoMs_visit = mstTerminalinfoMDao.queryForEq("padisconsistent", "0");
                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    mCheckexerecordInfos = mstCheckexerecordInfoDao.queryForFieldValues(terminalKeyMap);
                    // mTerminalinfoMs_visit =
                    // mstTerminalinfoMDao.queryForFieldValues(terminalKeyMap);
                    
                    // MST_CMPSUPPLY_INFO(竞品供货关系表)
                    cmpsupplyInfos = mstCmpsupplyInfoDao.queryForFieldValues(terminalKeyMap);
                    
                    // MstGroupproductM (产品组合是否达标)
                    //mMstGroupproductMs = mstGroupproductMDao.queryForFieldValues(terminalKeyMap);
                    // 存放所有MstGroupproductM表的未上传记录
                    QueryBuilder<MstGroupproductM, String> MstGroupproductMQB1 = mstGroupproductMDao.queryBuilder();
                    Where<MstGroupproductM, String> mstGroupproductMWhere1 = MstGroupproductMQB1.where();
                    mstGroupproductMWhere1.eq("uploadflag", "1");
                    mstGroupproductMWhere1.and();
                    mstGroupproductMWhere1.eq("padisconsistent", "0");
                    MstGroupproductMQB1.orderBy("createdate", true);
                    mMstGroupproductMs = MstGroupproductMQB1.query();

                    Map<String, Object> lowerkeyMap = new HashMap<String, Object>();
                    lowerkeyMap.put("lowerkey", terminalkey);
                    lowerkeyMap.put("padisconsistent", "0");
                    // MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                    mAgencysupplyInfos = mstAgencysupplyInfoDao.queryForFieldValues(lowerkeyMap);
                }

            } else {
                // Map<String, Object> hashMap = new HashMap<String, Object>();
                // hashMap.put("uploadFlag", "1");
                // hashMap.put("padisconsistent", "0");
                // visits = mstVisitMDao.queryForFieldValues(hashMap);
            	
            	// 只存放所有终端的最新一次拜访记录(分组)
                QueryBuilder<MstVisitM, String> visitQB = mstVisitMDao.queryBuilder();
                Where<MstVisitM, String> visitWhere = visitQB.where();
                visitWhere.eq("uploadFlag", "1");
                visitWhere.and();
                visitWhere.eq("padisconsistent", "0");
                //productQb.groupBy(DBConst.PROD_PARENT_PRODVAR_ID); 
                // 离线拜访时,针对同一家终端多次拜访,只上传最后一次拜访数据
                visitQB.groupBy("terminalkey"); 
                visitQB.orderBy("terminalkey", true);
                visitQB.orderBy("visitdate", false);
                visits = visitQB.query();
                
                
                // 存放今天所有终端所有次数的拜访记录
                QueryBuilder<MstVisitM, String> visitQB1 = mstVisitMDao .queryBuilder();
                Where<MstVisitM, String> visitWhere1 = visitQB1.where();
                visitWhere1.eq("uploadFlag", "1");
                visitWhere1.and();
                visitWhere1.eq("padisconsistent", "0");
                //productQb.groupBy(DBConst.PROD_PARENT_PRODVAR_ID); 
                visitQB1.orderBy("terminalkey", true);
                visitQB1.orderBy("visitdate", false);
                visitsall = visitQB1.query();
                
                
                
                if (visits != null && !visits.isEmpty()) {
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                    mVistproductInfos = mstVistproductInfoDao.queryForEq("padisconsistent", "0");
                    // MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                    // 此处不上传终端申请失效数据
                    // mInvalidapplayInfos_visit =
                    // mstInvalidapplayInfoDao.queryForEq("padisconsistent",
                    // "0");
                    // MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                    // 只上传非终端申请失效数据
                    // mTerminalinfoMs_visit =
                    // mstTerminalinfoMDao.queryForEq("padisconsistent", "0");
                    QueryBuilder<MstTerminalinfoM, String> termQb = mstTerminalinfoMDao.queryBuilder();
                    Where<MstTerminalinfoM, String> termWhere = termQb.where();
                    termWhere.eq("padisconsistent", "0");
                    termWhere.and();
                    termWhere.ne("status", ConstValues.FLAG_3);
                    mTerminalinfoMs_visit = termQb.query();
                    // MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                    mPromotermInfos = mstPromotermInfoDao.queryForEq("padisconsistent", "0");
                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    mCheckexerecordInfos = mstCheckexerecordInfoDao.queryForEq("padisconsistent", "0");
                    if (null != visit) {
                        for (int i = 0; i < mCheckexerecordInfos.size(); i++) {
                            if (null != mCheckexerecordInfos.get(i).getUpdateuser()) {
                                if ("0-1".equals(mCheckexerecordInfos.get(i).getUpdateuser().trim())) {
                                    mCheckexerecordInfos.get(i).setUpdateuser(visit.getUserid());
                                }
                            }
                        }
                    }
                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                    mCollectionexerecordInfos = mstCollectionexerecordInfoDao.queryForEq("padisconsistent", "0");
                    // MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                    mAgencysupplyInfos = mstAgencysupplyInfoDao.queryForEq("padisconsistent", "0");
                    // MST_CMPSUPPLY_INFO(竞品供货关系表)
                    cmpsupplyInfos = mstCmpsupplyInfoDao.queryForEq("padisconsistent", "0");
                    
                    // 存放所有MstGroupproductM表的未上传记录
                    QueryBuilder<MstGroupproductM, String> MstGroupproductMQB1 = mstGroupproductMDao.queryBuilder();
                    Where<MstGroupproductM, String> mstGroupproductMWhere1 = MstGroupproductMQB1.where();
                    mstGroupproductMWhere1.eq("uploadflag", "1");
                    mstGroupproductMWhere1.and();
                    mstGroupproductMWhere1.eq("padisconsistent", "0");
                    MstGroupproductMQB1.orderBy("createdate", true);
                    mMstGroupproductMs = MstGroupproductMQB1.query();

                }
            }
            if (visits != null && !visits.isEmpty()) {
                List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
                // 根据表结果组织数据关系
                for (MstVisitM mstVisitm : visits) {
                    Map<String, String> childDatas = new HashMap<String, String>();
                    mstVisitm.setPadisconsistent("1");
                    List<MstVisitM> mstVisitMs = new ArrayList<MstVisitM>();
                    //String visitsss = JsonUtil.toJson(mstVisitm);
                    mstVisitMs.add(mstVisitm);
                    //savFile(visitsss, "MstVisits");// 采集项记录表
                    //FileUtil.writeTxt(visitsss,FileUtil.getSDPath()+"/MstVisits2.txt");//上传巡店拜访的json
                    //childDatas.put("MIT_VISIT_M", JsonUtil.toJson(mstVisitm));
                    childDatas.put("MIT_VISIT_M", JsonUtil.toJson(mstVisitMs));

                    String visitkey = mstVisitm.getVisitkey();
                    String terminalkey = mstVisitm.getTerminalkey();
                    String terminalcode = "";
                    List<MstTerminalinfoM> childTerminalinfoMs = new ArrayList<MstTerminalinfoM>();
                    for (MstTerminalinfoM mTerminalinfoM : mTerminalinfoMs_visit) {
                        if (terminalkey.equals(mTerminalinfoM.getTerminalkey())) {
                        	terminalcode = mTerminalinfoM.getTerminalcode();
                            mTerminalinfoM.setPadisconsistent("1");
                            childTerminalinfoMs.add(mTerminalinfoM);
                        }
                    }
                    childDatas.put("MIT_TERMINALINFO_M",JsonUtil.toJson(childTerminalinfoMs));
                    *//*String childTerminalinfoMsqwe = JsonUtil.toJson(childTerminalinfoMs);
                    FileUtil.writeTxt(childTerminalinfoMsqwe, FileUtil.getSDPath()+"/childTerminalinfoMsqwe.txt");*//*
                    
                    List<MstGroupproductM> childMstGroupproductMs = new ArrayList<MstGroupproductM>();
                    for (MstGroupproductM mstgroupproductm : mMstGroupproductMs) {
                        if (terminalcode.equals(mstgroupproductm.getTerminalcode())) {
                        	mstgroupproductm.setPadisconsistent("1");
                            childMstGroupproductMs.add(mstgroupproductm);
                        }
                    }
                    childDatas.put("MIT_GROUPPRODUCT_M",JsonUtil.toJson(childMstGroupproductMs));

                    List<MstCmpsupplyInfo> childCmpsupplyInfos = new ArrayList<MstCmpsupplyInfo>();
                    for (MstCmpsupplyInfo childCmpsupplyInfo : cmpsupplyInfos) {
                        if (terminalkey.equals(childCmpsupplyInfo.getTerminalkey())) {
                            childCmpsupplyInfo.setPadisconsistent("1");
                            childCmpsupplyInfos.add(childCmpsupplyInfo);
                        }
                    }
                    childDatas.put("MIT_CMPSUPPLY_INFO", JsonUtil.toJson(childCmpsupplyInfos));

                    // 终端档案申请表
                    List<MstInvalidapplayInfo> childInvalidapplayInfos = new ArrayList<MstInvalidapplayInfo>();
                    for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_visit) {
                        if (visitkey.equals(mInvalidapplayInfo.getVisitkey())) {
                            mInvalidapplayInfo.setPadisconsistent("1");
                            childInvalidapplayInfos.add(mInvalidapplayInfo);
                        }
                    }
                    childDatas.put("MIT_INVALIDAPPLAY_INFO",JsonUtil.toJson(childInvalidapplayInfos));

                    // MST_PROMOTERM_INFO(终端参加活动信息表)
                    List<MstPromotermInfo> childPromotermInfos = new ArrayList<MstPromotermInfo>();
                    for (MstPromotermInfo mPromotermInfo : mPromotermInfos) {
                        if (visitkey.equals(mPromotermInfo.getVisitkey())) {
                            mPromotermInfo.setPadisconsistent("1");
                            childPromotermInfos.add(mPromotermInfo);
                        }
                    }
                    childDatas.put("MIT_PROMOTERM_INFO", JsonUtil.toJson(childPromotermInfos));

                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)
                    List<MstVistproductInfo> childMstVistproductInfos = new ArrayList<MstVistproductInfo>();
                    for (MstVistproductInfo mVistproductInfo : mVistproductInfos) {
                        if (visitkey.equals(mVistproductInfo.getVisitkey())) {
                            mVistproductInfo.setPadisconsistent("1");
                            childMstVistproductInfos.add(mVistproductInfo);
                        }
                    }
                    String json = JsonUtil.toJson(childMstVistproductInfos);
                    //FileUtil.writeTxt(json, FileUtil.getSDPath()+"/mVistproductInfos0808.txt");
                    childDatas.put("MIT_VISTPRODUCT_INFO",JsonUtil.toJson(childMstVistproductInfos));

                    // MstAgencysupplyInfo MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)
                    List<MstAgencysupplyInfo> childMstAgencysupplyInfos = new ArrayList<MstAgencysupplyInfo>();
                    for (MstAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                        if (terminalkey.equals(mAgencysupplyInfo.getLowerkey())) {
                            mAgencysupplyInfo.setPadisconsistent("1");
                            mAgencysupplyInfo.setOrderbyno("1");// 上传时将今天新增的供货关系标记改为1
                            childMstAgencysupplyInfos.add(mAgencysupplyInfo);
                        }
                    }
                    childDatas.put("MIT_AGENCYSUPPLY_INFO", JsonUtil.toJson(childMstAgencysupplyInfos));

                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)
                    List<MstCollectionexerecordInfo> childMstCollectionexerecordInfos = new ArrayList<MstCollectionexerecordInfo>();

                    for (MstCollectionexerecordInfo mCollectionexerecordInfo : mCollectionexerecordInfos) {
                        if (visitkey.equals(mCollectionexerecordInfo.getVisitkey())) {
                            mCollectionexerecordInfo.setPadisconsistent("1");
                            childMstCollectionexerecordInfos .add(mCollectionexerecordInfo);
                        }
                    }
                    String collectionexerecord = JsonUtil.toJson(childMstCollectionexerecordInfos);
                    //FileUtil.writeTxt(collectionexerecord, FileUtil.getSDPath()+"/collectionexerecord1.txt");
                    childDatas.put("MIT_COLLECTIONEXERECORD_INFO",JsonUtil.toJson(childMstCollectionexerecordInfos));

                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    List<MstCheckexerecordInfo> childMstCheckexerecordInfos = new ArrayList<MstCheckexerecordInfo>();
                    for (MstCheckexerecordInfo mCheckexerecordInfo : mCheckexerecordInfos) {
                    	if(null != visit){
                    		mCheckexerecordInfo.setUpdateuser(visit.getUserid());
                    	}
                    	// 修改多家离线上传数据错误bug
                    	if(terminalkey.equals(mCheckexerecordInfo.getTerminalkey())){
                    		
                    		// 只上传离线拜访的最后一次
                            if("temp".equals(mCheckexerecordInfo.getVisitkey())){
                            	mCheckexerecordInfo.setPadisconsistent("1");
                                childMstCheckexerecordInfos.add(mCheckexerecordInfo);
                            }
                            if(visitkey.equals(mCheckexerecordInfo.getVisitkey())){
                            	mCheckexerecordInfo.setPadisconsistent("1");
                                childMstCheckexerecordInfos.add(mCheckexerecordInfo);
                            }
                    	}
                    }

                    //String checkexerecord = JsonUtil.toJson(childMstCheckexerecordInfos);
                    //savFile(checkexerecord, "MST_CHECKEXERECORD_INFO");// 拉链表
                    //FileUtil.writeTxt(checkexerecord, FileUtil.getSDPath()+"/checkexerecord3213.txt");
                    childDatas.put("MIT_CHECKEXERECORD_INFO",JsonUtil.toJson(childMstCheckexerecordInfos));

                    mainDatas.add(childDatas);
                }
                
                // 添加
                String json = JsonUtil.toJson(mainDatas);
                FileUtil.writeTxt(json,FileUtil.getSDPath()+"/shopvisit1016.txt");//上传巡店拜访的json
                // System.out.println("巡店拜访"+json);
                Log.i(TAG, "巡店拜访send list size" + mainDatas.size() + json);

                upVisitDataInService("opt_save_visit","",json);

            } else {
                if (isNeedExit) {
                	//上传所有的巡店拜访
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    */


    List<MitVisitM> visits = new ArrayList<MitVisitM>();
    List<MitVisitM> visitsall = new ArrayList<MitVisitM>();
    List<MitVistproductInfo> mVistproductInfos = new ArrayList<MitVistproductInfo>();
    List<MstInvalidapplayInfo> mInvalidapplayInfos_visit = new ArrayList<MstInvalidapplayInfo>();
    List<MitPromotermInfo> mPromotermInfos = new ArrayList<MitPromotermInfo>();
    List<MitTerminalinfoM> mTerminalinfoMs_visit = new ArrayList<MitTerminalinfoM>();
    List<MitCheckexerecordInfo> mCheckexerecordInfos = new ArrayList<MitCheckexerecordInfo>();
    List<MitCollectionexerecordInfo> mCollectionexerecordInfos = new ArrayList<MitCollectionexerecordInfo>();
    List<MitAgencysupplyInfo> mAgencysupplyInfos = new ArrayList<MitAgencysupplyInfo>();
    List<MitCmpsupplyInfo> cmpsupplyInfos = new ArrayList<MitCmpsupplyInfo>();

    // 上传图片列表
    //List<UpCameraListMStc> upCameraListMStcs = new ArrayList<UpCameraListMStc>();
    // 上传产品组合是否达标
    List<MitGroupproductM> mMstGroupproductMs = new ArrayList<MitGroupproductM>();
    public void upload_xt_visit(final boolean isNeedExit, final String visitKey,final int whatId) {
        try {
            MitVisitM visit = null;
            if (visitKey != null && !visitKey.trim().equals("")) {
                visit = mitVisitMDao.queryForId(visitKey);
                if (visit != null) {
                    // String visitDate = DateUtil.formatDate(new Date(),"yyyyMMddHHmmss");
                    // visit.setEnddate(visitDate);
                    visits.add(visit);
                    visitsall.add(visit);
                    String terminalkey = visit.getTerminalkey();

                    Map<String, Object> visitKeyMap = new HashMap<String, Object>();
                    visitKeyMap.put("visitkey", visitKey);
                    visitKeyMap.put("padisconsistent", "0");

                    Map<String, Object> terminalKeyMap = new HashMap<String, Object>();
                    terminalKeyMap.put("terminalkey", terminalkey);
                    terminalKeyMap.put("padisconsistent", "0");

                    // MST_INVALIDAPPLAY_INFO(终端申请表)
                    mInvalidapplayInfos_visit = mstInvalidapplayInfoDao.queryForFieldValues(visitKeyMap);
                    // MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                    mPromotermInfos = mitPromotermInfoDao.queryForFieldValues(visitKeyMap);
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                    mVistproductInfos = mitVistproductInfoDao.queryForFieldValues(visitKeyMap);
                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                    mCollectionexerecordInfos = mitCollectionexerecordInfoDao.queryForFieldValues(visitKeyMap);
                    for (int i = 0; i < mCollectionexerecordInfos.size(); i++) {
                        if (null != mCollectionexerecordInfos.get(i).getUpdateuser()) {
                            if ("0-1".equals(mCollectionexerecordInfos.get(i).getUpdateuser().trim())) {
                                mCollectionexerecordInfos.get(i).setUpdateuser(visit.getUserid());
                            }
                        }
                    }
                    // MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                    mTerminalinfoMs_visit = mitTerminalinfoMDao.queryForEq("padisconsistent", "0");
                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    mCheckexerecordInfos = mitCheckexerecordInfoDao.queryForFieldValues(terminalKeyMap);
                    // mTerminalinfoMs_visit =
                    // mstTerminalinfoMDao.queryForFieldValues(terminalKeyMap);

                    // MST_CMPSUPPLY_INFO(竞品供货关系表)
                    cmpsupplyInfos = mitCmpsupplyInfoDao.queryForFieldValues(terminalKeyMap);

                    // MstGroupproductM (产品组合是否达标)
                    //mMstGroupproductMs = mstGroupproductMDao.queryForFieldValues(terminalKeyMap);
                    // 存放所有MstGroupproductM表的未上传记录
                    QueryBuilder<MitGroupproductM, String> MstGroupproductMQB1 = mitGroupproductMDao.queryBuilder();
                    Where<MitGroupproductM, String> mstGroupproductMWhere1 = MstGroupproductMQB1.where();
                    mstGroupproductMWhere1.eq("uploadflag", "1");
                    mstGroupproductMWhere1.and();
                    mstGroupproductMWhere1.eq("padisconsistent", "0");
                    MstGroupproductMQB1.orderBy("createdate", true);
                    mMstGroupproductMs = MstGroupproductMQB1.query();

                    Map<String, Object> lowerkeyMap = new HashMap<String, Object>();
                    lowerkeyMap.put("lowerkey", terminalkey);
                    lowerkeyMap.put("padisconsistent", "0");
                    // MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                    mAgencysupplyInfos = mitAgencysupplyInfoDao.queryForFieldValues(lowerkeyMap);
                }

            } else {
                // Map<String, Object> hashMap = new HashMap<String, Object>();
                // hashMap.put("uploadFlag", "1");
                // hashMap.put("padisconsistent", "0");
                // visits = mstVisitMDao.queryForFieldValues(hashMap);

                // 只存放所有终端的最新一次拜访记录(分组)
                QueryBuilder<MitVisitM, String> visitQB = mitVisitMDao.queryBuilder();
                Where<MitVisitM, String> visitWhere = visitQB.where();
                visitWhere.eq("uploadFlag", "1");
                visitWhere.and();
                visitWhere.eq("padisconsistent", "0");
                //productQb.groupBy(DBConst.PROD_PARENT_PRODVAR_ID);
                // 离线拜访时,针对同一家终端多次拜访,只上传最后一次拜访数据
                visitQB.groupBy("terminalkey");
                visitQB.orderBy("terminalkey", true);
                visitQB.orderBy("visitdate", false);
                visits = visitQB.query();


                // 存放今天所有终端所有次数的拜访记录
                QueryBuilder<MitVisitM, String> visitQB1 = mitVisitMDao .queryBuilder();
                Where<MitVisitM, String> visitWhere1 = visitQB1.where();
                visitWhere1.eq("uploadFlag", "1");
                visitWhere1.and();
                visitWhere1.eq("padisconsistent", "0");
                //productQb.groupBy(DBConst.PROD_PARENT_PRODVAR_ID);
                visitQB1.orderBy("terminalkey", true);
                visitQB1.orderBy("visitdate", false);
                visitsall = visitQB1.query();



                if (visits != null && !visits.isEmpty()) {
                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                    mVistproductInfos = mitVistproductInfoDao.queryForEq("padisconsistent", "0");
                    // MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                    // 此处不上传终端申请失效数据
                    // mInvalidapplayInfos_visit =
                    // mstInvalidapplayInfoDao.queryForEq("padisconsistent",
                    // "0");
                    // MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                    // 只上传非终端申请失效数据
                    // mTerminalinfoMs_visit =
                    // mstTerminalinfoMDao.queryForEq("padisconsistent", "0");
                    QueryBuilder<MitTerminalinfoM, String> termQb = mitTerminalinfoMDao.queryBuilder();
                    Where<MitTerminalinfoM, String> termWhere = termQb.where();
                    termWhere.eq("padisconsistent", "0");
                    termWhere.and();
                    termWhere.ne("status", ConstValues.FLAG_3);
                    mTerminalinfoMs_visit = termQb.query();
                    // MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                    mPromotermInfos = mitPromotermInfoDao.queryForEq("padisconsistent", "0");
                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    mCheckexerecordInfos = mitCheckexerecordInfoDao.queryForEq("padisconsistent", "0");
                    if (null != visit) {
                        for (int i = 0; i < mCheckexerecordInfos.size(); i++) {
                            if (null != mCheckexerecordInfos.get(i).getUpdateuser()) {
                                if ("0-1".equals(mCheckexerecordInfos.get(i).getUpdateuser().trim())) {
                                    mCheckexerecordInfos.get(i).setUpdateuser(visit.getUserid());
                                }
                            }
                        }
                    }
                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                    mCollectionexerecordInfos = mitCollectionexerecordInfoDao.queryForEq("padisconsistent", "0");
                    // MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                    mAgencysupplyInfos = mitAgencysupplyInfoDao.queryForEq("padisconsistent", "0");
                    // MST_CMPSUPPLY_INFO(竞品供货关系表)
                    cmpsupplyInfos = mitCmpsupplyInfoDao.queryForEq("padisconsistent", "0");

                    // 存放所有MstGroupproductM表的未上传记录
                    QueryBuilder<MitGroupproductM, String> MstGroupproductMQB1 = mitGroupproductMDao.queryBuilder();
                    Where<MitGroupproductM, String> mstGroupproductMWhere1 = MstGroupproductMQB1.where();
                    mstGroupproductMWhere1.eq("uploadflag", "1");
                    mstGroupproductMWhere1.and();
                    mstGroupproductMWhere1.eq("padisconsistent", "0");
                    MstGroupproductMQB1.orderBy("createdate", true);
                    mMstGroupproductMs = MstGroupproductMQB1.query();

                }
            }
            if (visits != null && !visits.isEmpty()) {
                List<Map<String, String>> mainDatas = new ArrayList<Map<String, String>>();
                // 根据表结果组织数据关系
                for (MitVisitM mstVisitm : visits) {
                    Map<String, String> childDatas = new HashMap<String, String>();
                    mstVisitm.setPadisconsistent("1");
                    List<MitVisitM> mstVisitMs = new ArrayList<MitVisitM>();
                    //String visitsss = JsonUtil.toJson(mstVisitm);
                    mstVisitMs.add(mstVisitm);
                    //savFile(visitsss, "MstVisits");// 采集项记录表
                    //FileUtil.writeTxt(visitsss,FileUtil.getSDPath()+"/MstVisits2.txt");//上传巡店拜访的json
                    //childDatas.put("MIT_VISIT_M", JsonUtil.toJson(mstVisitm));
                    childDatas.put("MIT_VISIT_M", JsonUtil.toJson(mstVisitMs));

                    String visitkey = mstVisitm.getVisitkey();
                    String terminalkey = mstVisitm.getTerminalkey();
                    String terminalcode = "";
                    List<MitTerminalinfoM> childTerminalinfoMs = new ArrayList<MitTerminalinfoM>();
                    for (MitTerminalinfoM mTerminalinfoM : mTerminalinfoMs_visit) {
                        if (terminalkey.equals(mTerminalinfoM.getTerminalkey())) {
                            terminalcode = mTerminalinfoM.getTerminalcode();
                            mTerminalinfoM.setPadisconsistent("1");
                            childTerminalinfoMs.add(mTerminalinfoM);
                        }
                    }
                    childDatas.put("MIT_TERMINALINFO_M",JsonUtil.toJson(childTerminalinfoMs));
                    /*String childTerminalinfoMsqwe = JsonUtil.toJson(childTerminalinfoMs);
                    FileUtil.writeTxt(childTerminalinfoMsqwe, FileUtil.getSDPath()+"/childTerminalinfoMsqwe.txt");*/

                    List<MitGroupproductM> childMstGroupproductMs = new ArrayList<MitGroupproductM>();
                    for (MitGroupproductM mstgroupproductm : mMstGroupproductMs) {
                        if (terminalcode.equals(mstgroupproductm.getTerminalcode())) {
                            mstgroupproductm.setPadisconsistent("1");
                            childMstGroupproductMs.add(mstgroupproductm);
                        }
                    }
                    childDatas.put("MIT_GROUPPRODUCT_M",JsonUtil.toJson(childMstGroupproductMs));

                    List<MitCmpsupplyInfo> childCmpsupplyInfos = new ArrayList<MitCmpsupplyInfo>();
                    for (MitCmpsupplyInfo childCmpsupplyInfo : cmpsupplyInfos) {
                        if (terminalkey.equals(childCmpsupplyInfo.getTerminalkey())) {
                            childCmpsupplyInfo.setPadisconsistent("1");
                            childCmpsupplyInfos.add(childCmpsupplyInfo);
                        }
                    }
                    childDatas.put("MIT_CMPSUPPLY_INFO", JsonUtil.toJson(childCmpsupplyInfos));

                    // 终端档案申请表
                    List<MstInvalidapplayInfo> childInvalidapplayInfos = new ArrayList<MstInvalidapplayInfo>();
                    for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_visit) {
                        if (visitkey.equals(mInvalidapplayInfo.getVisitkey())) {
                            mInvalidapplayInfo.setPadisconsistent("1");
                            childInvalidapplayInfos.add(mInvalidapplayInfo);
                        }
                    }
                    childDatas.put("MIT_INVALIDAPPLAY_INFO",JsonUtil.toJson(childInvalidapplayInfos));

                    // MST_PROMOTERM_INFO(终端参加活动信息表)
                    List<MitPromotermInfo> childPromotermInfos = new ArrayList<MitPromotermInfo>();
                    for (MitPromotermInfo mPromotermInfo : mPromotermInfos) {
                        if (visitkey.equals(mPromotermInfo.getVisitkey())) {
                            mPromotermInfo.setPadisconsistent("1");
                            childPromotermInfos.add(mPromotermInfo);
                        }
                    }
                    childDatas.put("MIT_PROMOTERM_INFO", JsonUtil.toJson(childPromotermInfos));

                    // MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)
                    List<MitVistproductInfo> childMstVistproductInfos = new ArrayList<MitVistproductInfo>();
                    for (MitVistproductInfo mVistproductInfo : mVistproductInfos) {
                        if (visitkey.equals(mVistproductInfo.getVisitkey())) {
                            mVistproductInfo.setPadisconsistent("1");
                            childMstVistproductInfos.add(mVistproductInfo);
                        }
                    }
                    String json = JsonUtil.toJson(childMstVistproductInfos);
                    //FileUtil.writeTxt(json, FileUtil.getSDPath()+"/mVistproductInfos0808.txt");
                    childDatas.put("MIT_VISTPRODUCT_INFO",JsonUtil.toJson(childMstVistproductInfos));

                    // MstAgencysupplyInfo MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)
                    List<MitAgencysupplyInfo> childMstAgencysupplyInfos = new ArrayList<MitAgencysupplyInfo>();
                    for (MitAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                        if (terminalkey.equals(mAgencysupplyInfo.getLowerkey())) {
                            mAgencysupplyInfo.setPadisconsistent("1");
                            mAgencysupplyInfo.setOrderbyno("1");// 上传时将今天新增的供货关系标记改为1
                            childMstAgencysupplyInfos.add(mAgencysupplyInfo);
                        }
                    }
                    childDatas.put("MIT_AGENCYSUPPLY_INFO", JsonUtil.toJson(childMstAgencysupplyInfos));

                    // MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)
                    List<MitCollectionexerecordInfo> childMstCollectionexerecordInfos = new ArrayList<MitCollectionexerecordInfo>();

                    for (MitCollectionexerecordInfo mCollectionexerecordInfo : mCollectionexerecordInfos) {
                        if (visitkey.equals(mCollectionexerecordInfo.getVisitkey())) {
                            mCollectionexerecordInfo.setPadisconsistent("1");
                            childMstCollectionexerecordInfos .add(mCollectionexerecordInfo);
                        }
                    }
                    String collectionexerecord = JsonUtil.toJson(childMstCollectionexerecordInfos);
                    //FileUtil.writeTxt(collectionexerecord, FileUtil.getSDPath()+"/collectionexerecord1.txt");
                    childDatas.put("MIT_COLLECTIONEXERECORD_INFO",JsonUtil.toJson(childMstCollectionexerecordInfos));

                    // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                    List<MitCheckexerecordInfo> childMstCheckexerecordInfos = new ArrayList<MitCheckexerecordInfo>();
                    for (MitCheckexerecordInfo mCheckexerecordInfo : mCheckexerecordInfos) {
                        if(null != visit){
                            mCheckexerecordInfo.setUpdateuser(visit.getUserid());
                        }
                        // 修改多家离线上传数据错误bug
                        if(terminalkey.equals(mCheckexerecordInfo.getTerminalkey())){

                            // 只上传离线拜访的最后一次
                            if("temp".equals(mCheckexerecordInfo.getVisitkey())){
                                mCheckexerecordInfo.setPadisconsistent("1");
                                childMstCheckexerecordInfos.add(mCheckexerecordInfo);
                            }
                            if(visitkey.equals(mCheckexerecordInfo.getVisitkey())){
                                mCheckexerecordInfo.setPadisconsistent("1");
                                childMstCheckexerecordInfos.add(mCheckexerecordInfo);
                            }
                        }
                    }

                    //String checkexerecord = JsonUtil.toJson(childMstCheckexerecordInfos);
                    //savFile(checkexerecord, "MST_CHECKEXERECORD_INFO");// 拉链表
                    //FileUtil.writeTxt(checkexerecord, FileUtil.getSDPath()+"/checkexerecord3213.txt");
                    childDatas.put("MIT_CHECKEXERECORD_INFO",JsonUtil.toJson(childMstCheckexerecordInfos));

                    mainDatas.add(childDatas);
                }

                // 添加
                String json = JsonUtil.toJson(mainDatas);
                FileUtil.writeTxt(json,FileUtil.getSDPath()+"/shopvisit1016.txt");//上传巡店拜访的json
                // System.out.println("巡店拜访"+json);
                Log.i(TAG, "巡店拜访send list size" + mainDatas.size() + json);

                upVisitDataInService("opt_save_visit","",json);

            } else {
                if (isNeedExit) {
                    //上传所有的巡店拜访
                    handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求路线下的所有终端
     *
     * @param optcode   请求码
     * @param tableName 请求的表
     * @param content   请求json
     */
    void upVisitDataInService(final String optcode, final String tableName, String content) {

        // 组建请求Json
        RequestHeadStc requestHeadStc = requestHeadUtil.parseRequestHead(context);
        requestHeadStc.setOptcode(PropertiesUtil.getProperties(optcode));
        RequestStructBean reqObj = HttpParseJson.parseRequestStructBean(requestHeadStc, content);

        // 压缩请求数据
        String jsonZip = HttpParseJson.parseRequestJson(reqObj);

        RestClient.builder()
                .url(HttpUrl.IP_END)
                .params("data", jsonZip)
                //.loader(context)
                .success(new ISuccess() {
                    @Override
                    public void onSuccess(String response) {
                        String json = HttpParseJson.parseJsonResToString(response);
                        ResponseStructBean resObj = new ResponseStructBean();
                        resObj = JsonUtil.parseJson(json, ResponseStructBean.class);
                        // 保存登录信息
                        if (ConstValues.SUCCESS.equals(resObj.getResHead().getStatus())) {
                            //
                            Toast.makeText(context, "上传成功", Toast.LENGTH_SHORT).show();
                            // 处理上传后的数据,该删删,该处理
                            String formjson = resObj.getResBody().getContent();
                            parseUpData(formjson);

                        } else {
                            Toast.makeText(context, resObj.getResHead().getContent(), Toast.LENGTH_SHORT).show();
                        }
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

    private void parseUpData(String json) {
        // 解析区域定格路线信息
        AreaGridRoute emp = JsonUtil.parseJson(json, AreaGridRoute.class);
        String MIT_VISIT_M = emp.getMIT_VISIT_M();
        List<MitVisitM> mstVisitMs= (List<MitVisitM>) JsonUtil.parseList(json, MitVisitM.class);

        try {
            //DatabaseHelper helper = DatabaseHelper.getHelper(context);
            SQLiteDatabase db = helper.getWritableDatabase();

            // 修改协同的拜访主表 状态为上传成功
            for (MitVisitM visit : visitsall) {
                visit.setPadisconsistent("1");
                mitVisitMDao.createOrUpdate(visit);
            }
            // 修改协同我品供货关系 状态为上传成功
            for (MitAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                mAgencysupplyInfo.setPadisconsistent("1");
                mAgencysupplyInfo.setOrderbyno("1");// 上传成功将今天新增的供货关系标记改为1
                mitAgencysupplyInfoDao.createOrUpdate(mAgencysupplyInfo);
            }
            // 修改协同竞品供货关系 状态为上传成功
            for (MitCmpsupplyInfo mAgencysupplyInfo : cmpsupplyInfos) {
                mAgencysupplyInfo.setPadisconsistent("1");
                mAgencysupplyInfo.setOrderbyno("1");// 上传成功将今天新增的供货关系标记改为1
                mitCmpsupplyInfoDao.createOrUpdate(mAgencysupplyInfo);
            }

            // 修改协同拉链表 状态为上传成功
            for (MitCheckexerecordInfo mCheckexerecordInfo : mCheckexerecordInfos) {
                mCheckexerecordInfo.setPadisconsistent("1");
                mitCheckexerecordInfoDao .createOrUpdate(mCheckexerecordInfo);
            }
            // 修改协同终端表 状态为上传成功
            for (MitTerminalinfoM mTerminalinfoM : mTerminalinfoMs_visit) {
                mTerminalinfoM.setPadisconsistent("1");
                mitTerminalinfoMDao.createOrUpdate(mTerminalinfoM);
            }


            for(MitVisitM mitVisitM:mstVisitMs){

                String terminalkey = mitVisitM.getTerminalkey();
                String visitkey = mitVisitM.getVisitkey();
                // 删除协同该终端其余的拜访记录
                deleteMitVisitM(db,"MIT_VISIT_M",terminalkey,visitkey);


                // 根据VisitKey 删除图片表
                deleteTableInfoByVisitKey(db,"MIT_CAMERAINFO_M",terminalkey,visitkey);

                // 根据VisitKey 删除协同采集项
                deleteTableInfoByVisitKey(db,"MIT_COLLECTIONEXERECORD_INFO",terminalkey,visitkey);

                // 根据VisitKey 删除协同产品组合
                deleteTableInfoByVisitKey(db,"MIT_GROUPPRODUCT_M",terminalkey,visitkey);

                // 根据VisitKey 删除协同活动终端
                deleteTableInfoByVisitKey(db,"MIT_PROMOTERM_INFO",terminalkey,visitkey);

                // 根据VisitKey 删除协同拜访产品表
                deleteTableInfoByVisitKey(db,"MIT_VISTPRODUCT_INFO",terminalkey,visitkey);


                // 删除 协同我品供货关系
                deleteTableInfoByLowerkey(db,"MIT_AGENCYSUPPLY_INFO ",terminalkey,visitkey);

                // 删除协同竞品供货关系
                deleteTableInfoByTerminalkey(db,"MIT_CHECKEXERECORD_INFO",terminalkey,visitkey);

                // 删除协同拉链表
                deleteTableInfoByTerminalkey(db,"MIT_CMPSUPPLY_INFO",terminalkey,visitkey);

                // 删除协同终端表
                deleteTableInfoByTerminalkey(db,"MIT_TERMINALINFO_M",terminalkey,visitkey);

                // 删除协同 客情备忘
                //deleteTableInfoByTerminalkey(db,"MIT_VISITMEMO_INFO",terminalkey,visitkey);

            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    // 删除协同拜访主表(条件: terminalkey相等 visitkey不相等 padisconsistent=1 )
    private void deleteMitVisitM(SQLiteDatabase db, String tableName, String terminalkey, String visitkey) {

        // 删除表记录
        String sql = "delete from " + tableName +
                "  where terminalkey = '" + terminalkey +
                "' and visitkey <> '" + visitkey + "'" +
                "  and  padisconsistent = 1 ";
        db.execSQL(sql);
    }

    // 根据VisitKey 删除记录
    private void deleteTableInfoByVisitKey(SQLiteDatabase db,String tableName, String terminalkey, String visitkey) {
        // 删除表记录
        String sql = "delete from "+tableName+"  where visitkey = '"+visitkey+"'";
        db.execSQL(sql);
    }

    // 根据Terminalkey 删除记录
    private void deleteTableInfoByTerminalkey(SQLiteDatabase db,String tableName, String terminalkey, String visitkey) {
        // 删除表记录
        String sql = "delete from "+tableName+"  where terminalkey = '"+terminalkey+"' and padisconsistent = '1'";
        db.execSQL(sql);
    }

    // 根据lowerkey 删除记录
    private void deleteTableInfoByLowerkey(SQLiteDatabase db,String tableName, String terminalkey, String visitkey) {
        // 删除表记录
        String sql = "delete from "+tableName+"  where lowerkey = '"+terminalkey+"' and padisconsistent = '1'";
        db.execSQL(sql);
    }


    /**
     * 
     * @param obj 单个数据id或者对象
     * @param isNeedExit true:上传后退出系统
     */
    private void onFailureSendMessage(boolean isNeedExit, Object obj)
    {
        ViewUtil.sendMsg(context, "上传失败");
        //		if (obj != null) {
        //			ViewUtil.sendMsg(context, "上传失败");
        //		}else{
        //		    ViewUtil.sendMsg(context, "添加终端到Siebel系统失败");
        //		}
        //上传出错的情况下，发送消息阻止退出
        if (isNeedExit)
        {
            Message errmsg = new Message();
            errmsg.what = TitleLayout.UPLOAD_DATA;
            errmsg.obj = "error,上传出错了不允许退出";
            handler.sendMessage(errmsg);
        }
    }
    /**
     * 
     * @param obj 单个数据id或者对象
     * @param isNeedExit
     */
    private void onFailureSendMessage_timeOut(boolean isNeedExit, Object obj,String contents)
    {
        ViewUtil.sendMsg(context, contents);
        //		if (obj != null) {
        //			ViewUtil.sendMsg(context, "上传失败");
        //		}else{
        //		    ViewUtil.sendMsg(context, "添加终端到Siebel系统失败");
        //		}
        //上传出错的情况下，发送消息阻止退出
        if (isNeedExit)
        {
            Message errmsg = new Message();
            errmsg.what = TitleLayout.UPLOAD_DATA;
            errmsg.obj = "error,上传出错了不允许退出";
            handler.sendMessage(errmsg);
        }
    }
    /**
     * 上传成功后提示（待删除）
     * 
     * @param obj 单个数据id或者对象
     * @param isNeedExit
     */
    @Deprecated
    private void onSuccessSendMessage(boolean isNeedExit, Object obj)
    {
        if (isNeedExit)
        {
            handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
        }
        //单个数据 上传 结果发送出去
        if (obj != null)
        {
            ViewUtil.sendMsg(context, "上传成功");
            if (ConstValues.handler != null)
            {
                ConstValues.handler.sendEmptyMessage(ConstValues.WAIT2);//主要更新界面的状态位刷新listvie标志
            }
        }
    }

    /**
     * 用于网络上传成功后，发出提示信息
     * 
     * @param isNeedExit   
     * @param whatId       大于0的  
     */
    private void onSuccessSendMessage(int whatId, boolean isNeedExit)
    {
        if (isNeedExit)
        {
            handler.sendEmptyMessage(TitleLayout.UPLOAD_DATA);
        }
        //单个数据 上传 结果发送出去
        if (whatId > 0)
        {
            ViewUtil.sendMsg(context, "上传成功");
            if (ConstValues.handler != null)
            {
                ConstValues.handler.sendEmptyMessage(whatId);
            }
        }
    }
    
    

}
