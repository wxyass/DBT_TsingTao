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


import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FileUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstAgencyKFMDao;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
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
    private Dao<MstQuestionsanswersInfo, String> mstQuestionsanswersInfoDao = null;
    private Dao<MstWorksummaryInfo, String> mstWorksummaryInfoDao = null;
    private Dao<MstTerminalinfoM, String> mstTerminalinfoMDao = null;
    private Dao<MstInvoicingInfo, String> mstInvoicingInfoDao = null;
    private Dao<MstAgencytransferInfo, String> mstAgencytransferInfoDao = null;
    private Dao<MstVistproductInfo, String> mstVistproductInfoDao = null;
    private Dao<MstCheckexerecordInfo, String> mstCheckexerecordInfoDao = null;
    private Dao<MstInvalidapplayInfo, String> mstInvalidapplayInfoDao = null;
    private Dao<MstPromotermInfo, String> mstPromotermInfoDao = null;
    private Dao<MstCollectionexerecordInfo, String> mstCollectionexerecordInfoDao = null;
    private Dao<MstPlancheckInfo, String> mstPlancheckInfoDao = null;
    private Dao<MstPlancollectionInfo, String> mstPlancollectionInfoDao = null;
    private Dao<MstPlanforuserM, String> mstPlanforuserMDao = null;
    private Dao<MstPlanWeekforuserM, String> mstPlanWeekforuserMDao = null;
    private Dao<MstAgencysupplyInfo, String> mstAgencysupplyInfoDao = null;
    private Dao<MstAgencyvisitM, String> mstAgencyvisitMDao = null;
    private Dao<MstCmpsupplyInfo, String> mstCmpsupplyInfoDao = null;
    private Dao<MstPlanrouteInfo, String> mstPlanrouteInfodDao = null;
    private MstCameraiInfoMDao mstCameraiInfoMDao = null;
    private MstAgencyKFMDao mstAgencyKFMDao = null;
    private Dao<MstPlanTerminalM, String> mstPlanTerminalMDao = null;
    private Dao<MstGroupproductM, String> mstGroupproductMDao = null;

    

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
            mstWorksummaryInfoDao = helper.getMstWorksummaryInfoDao();
            mstTerminalinfoMDao = helper.getMstTerminalinfoMDao();
            mstInvoicingInfoDao = helper.getMstInvoicingInfoDao();
            mstAgencytransferInfoDao = helper.getMstAgencytransferInfoDao();
            mstVistproductInfoDao = helper.getMstVistproductInfoDao();
            mstCheckexerecordInfoDao = helper.getMstCheckexerecordInfoDao();
            mstInvalidapplayInfoDao = helper.getMstInvalidapplayInfoDao();
            mstCollectionexerecordInfoDao = helper.getMstCollectionexerecordInfoDao();
            mstPromotermInfoDao = helper.getMstPromotermInfoDao();
            mstPlancheckInfoDao = helper.getMstPlancheckInfoDao();
            mstPlanTerminalMDao = helper.getMstPlanTerminalM();
            
            mstPlancollectionInfoDao = helper.getMstPlancollectionInfoDao();
            mstPlanforuserMDao = helper.getMstPlanforuserMDao();
            mstPlanWeekforuserMDao = helper.getMstPlanWeekforuserMDao();
            mstAgencysupplyInfoDao = helper.getMstAgencysupplyInfoDao();
            mstAgencyvisitMDao = helper.getMstAgencyvisitMDao();
            mstCmpsupplyInfoDao = helper.getMstCmpsupplyInfoDao();
            mstPlanrouteInfodDao = helper.getMstPlanrouteInfoDao();
            mstCameraiInfoMDao = (MstCameraiInfoMDao)helper.getMstCameraiInfoMDao();
            mstAgencyKFMDao = (MstAgencyKFMDao)helper.getMstAgencyKFMDao();
            mstGroupproductMDao = helper.getMstGroupproductMDao();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }


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
	List<MstGroupproductM> mMstGroupproductMs = new ArrayList<MstGroupproductM>();
	
	

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
                    
                    String visitsss = JsonUtil.toJson(mstVisitm);
                    //savFile(visitsss, "MstVisits");// 采集项记录表
                    //FileUtil.writeTxt(visitsss,FileUtil.getSDPath()+"/MstVisits2.txt");//上传巡店拜访的json
                    childDatas.put("MST_VISIT_M", JsonUtil.toJson(mstVisitm));
                    
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
                    childDatas.put("MST_TERMINALINFO_M",JsonUtil.toJson(childTerminalinfoMs));
                    /*String childTerminalinfoMsqwe = JsonUtil.toJson(childTerminalinfoMs);
                    FileUtil.writeTxt(childTerminalinfoMsqwe, FileUtil.getSDPath()+"/childTerminalinfoMsqwe.txt");*/
                    
                    List<MstGroupproductM> childMstGroupproductMs = new ArrayList<MstGroupproductM>();
                    for (MstGroupproductM mstgroupproductm : mMstGroupproductMs) {
                        if (terminalcode.equals(mstgroupproductm.getTerminalcode())) {
                        	mstgroupproductm.setPadisconsistent("1");
                            childMstGroupproductMs.add(mstgroupproductm);
                        }
                    }
                    childDatas.put("MST_GROUPPRODUCT_M",JsonUtil.toJson(childMstGroupproductMs));

                    List<MstCmpsupplyInfo> childCmpsupplyInfos = new ArrayList<MstCmpsupplyInfo>();
                    for (MstCmpsupplyInfo childCmpsupplyInfo : cmpsupplyInfos) {
                        if (terminalkey.equals(childCmpsupplyInfo
                                .getTerminalkey())) {
                            childCmpsupplyInfo.setPadisconsistent("1");
                            childCmpsupplyInfos.add(childCmpsupplyInfo);
                        }
                    }
                    childDatas.put("MST_CMPSUPPLY_INFO", JsonUtil.toJson(childCmpsupplyInfos));

                    // 终端档案申请表
                    List<MstInvalidapplayInfo> childInvalidapplayInfos = new ArrayList<MstInvalidapplayInfo>();
                    for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_visit) {
                        if (visitkey.equals(mInvalidapplayInfo.getVisitkey())) {
                            mInvalidapplayInfo.setPadisconsistent("1");
                            childInvalidapplayInfos.add(mInvalidapplayInfo);
                        }
                    }
                    childDatas.put("MST_INVALIDAPPLAY_INFO",JsonUtil.toJson(childInvalidapplayInfos));

                    // MST_PROMOTERM_INFO(终端参加活动信息表)
                    List<MstPromotermInfo> childPromotermInfos = new ArrayList<MstPromotermInfo>();
                    for (MstPromotermInfo mPromotermInfo : mPromotermInfos) {
                        if (visitkey.equals(mPromotermInfo.getVisitkey())) {
                            mPromotermInfo.setPadisconsistent("1");
                            childPromotermInfos.add(mPromotermInfo);
                        }
                    }
                    childDatas.put("MST_PROMOTERM_INFO", JsonUtil.toJson(childPromotermInfos));

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
                    childDatas.put("MST_VISTPRODUCT_INFO",JsonUtil.toJson(childMstVistproductInfos));

                    // MstAgencysupplyInfo MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)
                    List<MstAgencysupplyInfo> childMstAgencysupplyInfos = new ArrayList<MstAgencysupplyInfo>();
                    for (MstAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                        if (terminalkey.equals(mAgencysupplyInfo.getLowerkey())) {
                            mAgencysupplyInfo.setPadisconsistent("1");
                            mAgencysupplyInfo.setOrderbyno("1");// 上传时将今天新增的供货关系标记改为1
                            childMstAgencysupplyInfos.add(mAgencysupplyInfo);
                        }
                    }
                    childDatas.put("MST_AGENCYSUPPLY_INFO", JsonUtil.toJson(childMstAgencysupplyInfos));

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
                    childDatas.put("MST_COLLECTIONEXERECORD_INFO",JsonUtil.toJson(childMstCollectionexerecordInfos));

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
                    childDatas.put("MST_CHECKEXERECORD_INFO",JsonUtil.toJson(childMstCheckexerecordInfos));

                    mainDatas.add(childDatas);
                }
                
                // 添加
                String json = JsonUtil.toJson(mainDatas);
                FileUtil.writeTxt(json,FileUtil.getSDPath()+"/shopvisit1016.txt");//上传巡店拜访的json
                // System.out.println("巡店拜访"+json);
                Log.i(TAG, "巡店拜访send list size" + mainDatas.size() + json);


                /*httpUtil.send("opt_save_visit", json, new RequestCallBack<String>() {
                            @Override
                            public void onSuccess(ResponseInfo<String> responseInfo) {
                                ResponseStructBean resObj = HttpUtil .parseRes(responseInfo.result);
                                Log.e(TAG, "巡店拜访"
                                        + resObj.getResBody().getContent());
                                // 获取请求服务器返回状态
                                if (ConstValues.SUCCESS.equals(resObj
                                        .getResHead().getStatus())) {

                                    // 更新表数据
                                    try {
                                        for (MstVisitM visit : visitsall) {
                                            visit.setPadisconsistent("1");
                                            mstVisitMDao.createOrUpdate(visit);

                                        }
                                        // 更新MST_VISTPRODUCT_INFO(拜访产品-竞品我品记录表)MstVistproductInfo
                                        for (MstVistproductInfo mVistproductInfo : mVistproductInfos) {
                                            mVistproductInfo
                                                    .setPadisconsistent("1");
                                            mstVistproductInfoDao
                                                    .createOrUpdate(mVistproductInfo);

                                        }
                                        // 更新MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                                        for (MstInvalidapplayInfo mInvalidapplayInfo : mInvalidapplayInfos_visit) {
                                            mInvalidapplayInfo
                                                    .setPadisconsistent("1");
                                            mstInvalidapplayInfoDao
                                                    .createOrUpdate(mInvalidapplayInfo);

                                        }
                                       // 更新MST_INVALIDAPPLAY_INFO(终端申请表)MstInvalidapplayInfo
                                        for (MstGroupproductM mMstGroupproductM : mMstGroupproductMs) {
                                        	mMstGroupproductM
                                                    .setPadisconsistent("1");
                                            mstGroupproductMDao
                                                    .createOrUpdate(mMstGroupproductM);

                                        }
                                        // 更新MST_PROMOTERM_INFO(终端参加活动信息表)MstPromotermInfo
                                        for (MstPromotermInfo mPromotermInfo : mPromotermInfos) {
                                            mPromotermInfo
                                                    .setPadisconsistent("1");
                                            mstPromotermInfoDao
                                                    .createOrUpdate(mPromotermInfo);

                                        }// 更新MST_TERMINALINFO_M(终端档案主表)MstTerminalinfoM
                                        for (MstTerminalinfoM mTerminalinfoM : mTerminalinfoMs_visit) {
                                            mTerminalinfoM
                                                    .setPadisconsistent("1");
                                            mstTerminalinfoMDao
                                                    .createOrUpdate(mTerminalinfoM);

                                        }
                                        // MST_CHECKEXERECORD_INFO(拜访指标执行记录表)MstCheckexerecordInfo
                                        Map<String, Object> siebelMap = JsonUtil
                                                .parseMap(resObj.getResBody()
                                                        .getContent());
                                        for (MstCheckexerecordInfo mCheckexerecordInfo : mCheckexerecordInfos) {
                                            if (siebelMap != null
                                                    && CheckUtil
                                                            .isBlankOrNull(mCheckexerecordInfo
                                                                    .getSiebelid())) {
                                                mCheckexerecordInfo.setSiebelid(FunUtil.isBlankOrNullTo(
                                                        siebelMap
                                                                .get(mCheckexerecordInfo
                                                                        .getRecordkey()),
                                                        null));
                                            }
                                            if (null != visits) {
                                                if (visits.size() > 0) {
                                                    mCheckexerecordInfo
                                                            .setUpdateuser(visits
                                                                    .get(0)
                                                                    .getUserid());
                                                }
                                            }
                                            mCheckexerecordInfo
                                                    .setPadisconsistent("1");
                                            mstCheckexerecordInfoDao
                                                    .createOrUpdate(mCheckexerecordInfo);

                                        }
                                        // 更新MST_COLLECTIONEXERECORD_INFO(拜访指标执行采集项记录表)MstCollectionexerecordInfo
                                        for (MstCollectionexerecordInfo mCollectionexerecordInfo : mCollectionexerecordInfos) {
                                            mCollectionexerecordInfo
                                                    .setPadisconsistent("1");
                                            mstCollectionexerecordInfoDao
                                                    .createOrUpdate(mCollectionexerecordInfo);

                                        }
                                        // 更新MST_AGENCYSUPPLY_INFO(经销商供货关系信息表)MstAgencysupplyInfo
                                        for (MstAgencysupplyInfo mAgencysupplyInfo : mAgencysupplyInfos) {
                                            if (siebelMap != null
                                                    && CheckUtil
                                                            .isBlankOrNull(mAgencysupplyInfo
                                                                    .getSiebelkey())) {
                                                mAgencysupplyInfo.setSiebelkey(FunUtil.isBlankOrNullTo(
                                                        siebelMap
                                                                .get(mAgencysupplyInfo
                                                                        .getAsupplykey()),
                                                        null));
                                            }
                                            mAgencysupplyInfo.setPadisconsistent("1");
                                            mAgencysupplyInfo.setOrderbyno("1");// 上传成功将今天新增的供货关系标记改为1
                                            mstAgencysupplyInfoDao.createOrUpdate(mAgencysupplyInfo);

                                        }
                                        // 上传客情备忘录
                                        upload_memos(false, null);
                                    } catch (SQLException e) {
                                        e.printStackTrace();
                                    }

                                    // 上传所以巡店拜访数据
                                    DbtLog.logUtils("退出上传", "上传所有的巡店拜访成功Success");
                                    onSuccessSendMessage(whatId, isNeedExit);
                                } else {
                                    String msg_timeoutString = resObj
                                            .getResBody().getContent();
                                    if (resObj.getResHead().getStatus()
                                            .equals("N")) {
                                        onFailureSendMessage_timeOut(
                                                isNeedExit, visitKey,
                                                msg_timeoutString.substring(0,
                                                        msg_timeoutString
                                                                .indexOf("[")));
                                    } else {
                                    	DbtLog.logUtils("退出上传", "上传所有的巡店拜访E");
                                        onFailureSendMessage(isNeedExit,
                                                visitKey);
                                    }
                                    // String content =
                                    // resObj.getResBody().getContent();
                                    List<String> ids = JsonUtil.parseList(
                                            msg_timeoutString
                                                    .substring(msg_timeoutString
                                                            .indexOf("[")),
                                            String.class);
                                    // 部分更新成功的时候,根据主/外 键更新数据
                                    updateInids("MST_VISIT_M", "visitkey", ids);
                                }
                            }

                            @Override
                            public void onFailure(HttpException error,String msg) {
                                Log.e(TAG, msg, error);
                                DbtLog.logUtils("退出上传", "上传所有的巡店拜访Fail");
                                onFailureSendMessage(isNeedExit, visitKey);
                            }
                        });*/
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
