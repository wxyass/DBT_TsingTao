package et.tsingtaopad.dd.ddxt.sayhi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstTerminalinfoMDao;
import et.tsingtaopad.db.dao.MstTerminalinfoMTempDao;
import et.tsingtaopad.db.dao.MstVisitMDao;
import et.tsingtaopad.db.dao.MstVisitMTempDao;
import et.tsingtaopad.db.dao.PadChecktypeMDao;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MstInvalidapplayInfo;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.db.table.PadChecktypeM;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.line.domain.MstRouteMStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtSayhiService extends XtShopVisitService {

    private final String TAG = "XtSayhiService";
    XtSayhiFragment.MyHandler handler;

    public XtSayhiService(Context context, XtSayhiFragment.MyHandler handler) {
        super(context, handler);
        this.handler = handler;
    }

    // 根据省市县编码 获取省市县名称
    public String getAreaName(String areacode) {

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "SELECT areaname  FROM CMM_AREA_M WHERE areacode = ?";
        Cursor cursor = db.rawQuery(querySql, new String[]{areacode});
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex("areaname");
        String areaname;
        try {
            areaname = cursor.getString(cursor.getColumnIndex("areaname"));
        } catch (Exception e) {
            areaname = "";
        }
        return areaname;
    }


    // 根据渠道编码,获取渠道名称
    public String getDatadicName(String diccode) {

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "SELECT dicname  FROM CMM_DATADIC_M WHERE diccode = ?";
        Cursor cursor = db.rawQuery(querySql, new String[]{diccode});
        cursor.moveToFirst();
        //int columnIndex = cursor.getColumnIndex("dicname");
        String areaname;
        try {
            areaname = cursor.getString(cursor.getColumnIndex("dicname"));
        } catch (Exception e) {
            areaname = "";
        }
        return areaname;
    }

    // 根据路线key,获取路线名称
    public String getRouteName(String diccode) {

        DatabaseHelper helper = DatabaseHelper.getHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();

        String querySql = "SELECT routename  FROM MST_ROUTE_M WHERE routekey = ?";
        Cursor cursor = db.rawQuery(querySql, new String[]{diccode});
        cursor.moveToFirst();
        //int columnIndex = cursor.getColumnIndex("dicname");
        String areaname;
        try {
            areaname = cursor.getString(cursor.getColumnIndex("routename"));
        } catch (Exception e) {
            areaname = "";
        }
        return areaname;
    }

    /**
     * 获取并初始化拜访线路
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MstRouteM> initMstRoute() {

        List<MstRouteM> mstRouteList = new ArrayList<MstRouteM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstRouteM, String> mstRouteMDao = helper.getMstRouteMDao();
            QueryBuilder<MstRouteM, String> qBuilder = mstRouteMDao.queryBuilder();
            Where<MstRouteM, String> where = qBuilder.where();
            where.and(where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0")),
                    where.or(where.isNull("routestatus"), where.eq("routestatus", "0"))
            );
            qBuilder.orderBy("orderbyno", true).orderBy("routename", true);
            mstRouteList = qBuilder.query();
        } catch (Exception e) {
            Log.e(TAG, "获取线路信息失败", e);
        }

        return mstRouteList;
    }

    /**
     * 获取并初始化拜访线路
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<MstRouteM> initXtMstRoute(String termid) {

        List<MstRouteM> mstRouteList = new ArrayList<MstRouteM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            SQLiteDatabase db = helper.getReadableDatabase();

            String querySql = " select ro.* from mst_route_m ro join  (select r.gridkey from mst_terminalinfo_m t " +
                    "  left join mst_route_m r on r.routekey = t.routekey " +
                    "  where t.terminalkey= ?) a on a.gridkey=ro.gridkey  " +
                    "  where ro.routestatus=0 order by ro.routename asc";
            Cursor cursor = db.rawQuery(querySql, new String[]{termid});
            MstRouteM item;
            while (cursor.moveToNext()) {
                item = new MstRouteM();
                item.setRoutekey(cursor.getString(cursor.getColumnIndex("routekey")));
                item.setGridkey(cursor.getString(cursor.getColumnIndex("gridkey")));
                item.setRoutecode(cursor.getString(cursor.getColumnIndex("routecode")));
                item.setRoutename(cursor.getString(cursor.getColumnIndex("routename")));
                item.setRoutedesc(cursor.getString(cursor.getColumnIndex("routedesc")));
                item.setRoutestatus(cursor.getString(cursor.getColumnIndex("routestatus")));
                item.setSisconsistent(cursor.getString(cursor.getColumnIndex("sisconsistent")));
                item.setPadisconsistent(cursor.getString(cursor.getColumnIndex("padisconsistent")));
                item.setComid(cursor.getString(cursor.getColumnIndex("comid")));
                item.setRemarks(cursor.getString(cursor.getColumnIndex("remarks")));
                item.setOrderbyno(cursor.getString(cursor.getColumnIndex("orderbyno")));
                item.setUpdateuser(cursor.getString(cursor.getColumnIndex("updateuser")));
                item.setDeleteflag(cursor.getString(cursor.getColumnIndex("deleteflag")));

                mstRouteList.add(item);
            }
        } catch (Exception e) {
            Log.e(TAG, "获取线路信息失败", e);
        }
        return mstRouteList;
    }

    /**
     * 初始化终端等级
     */
    public List<KvStc> initDataDicByTermLevel() {
        List<CmmDatadicM> dataDicLst = initDataDictionary();
        // 获取数据字典表中区域字典对应的父ID
        String termLevel = PropertiesUtil.getProperties("datadic_termLevel");

        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (termLevel.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode(),
                        item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        return kvLst;
    }

    /**
     * 初始化拜访对象职位(老板老板娘)
     */
    public List<KvStc> initVisitPosition() {
        List<CmmDatadicM> dataDicLst = initDataDictionary();
        // 获取数据字典表中区域字典对应的父ID
        String areaType=PropertiesUtil.getProperties("datadic_visitposition");

        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (areaType.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode()
                        , item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        return kvLst;
    }

    /**
     * 获取数据字典信息
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<CmmDatadicM> initDataDictionary() {
        List<CmmDatadicM> dataDicLst = new ArrayList<CmmDatadicM>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            QueryBuilder<CmmDatadicM, String> qBuilder =
                    helper.getCmmDatadicMDao().queryBuilder();
            Where<CmmDatadicM, String> where = qBuilder.where();
            where.or(where.isNull("deleteflag"), where.eq("deleteflag", "0"));
            qBuilder.orderBy("parentcode", true).orderBy("orderbyno", true);
            dataDicLst = qBuilder.query();
        } catch (SQLException e) {
            Log.e(TAG, "初始化数据字典失败", e);
        }

        return dataDicLst;
    }

    /**
     * 初始化终端区域类型
     */
    public List<KvStc> initDataDicByAreaType() {
        List<CmmDatadicM> dataDicLst = initDataDictionary();

        // 获取数据字典表中区域字典对应的父ID
        String areaType=PropertiesUtil.getProperties("datadic_areaType");

        List<KvStc> kvLst = new ArrayList<KvStc>();
        for (CmmDatadicM item : dataDicLst) {
            if (areaType.equals(item.getParentcode())) {
                kvLst.add(new KvStc(item.getDiccode()
                        , item.getDicname(), item.getParentcode()));
            } else if (kvLst.size() > 0) {
                break;
            }
        }
        return kvLst;
    }

    /**
     * 初始化销售渠道
     */
    public List<KvStc> initDataDicBySellChannel() {

        // 获取数据字典表中区域字典对应的父ID
        String sellChannel = PropertiesUtil.getProperties("datadic_sellChannel");
        List<KvStc> kvLst = new ArrayList<KvStc>();
        kvLst = queryChildForDataDic(sellChannel, 1);
        return kvLst;
    }

    /**
     *  递归遍历数据字典- 销售渠道
     *
     * @param parentCode    父ID
     * @param level         当前等级  1
     * @return
     */
    private List<KvStc> queryChildForDataDic(String parentCode, int level) {
        List<CmmDatadicM> dataDicLst = initDataDictionary();// 数据源
        List<KvStc> kvLst = null;
        if (level <= 3) {
            kvLst = new ArrayList<KvStc>();
            KvStc kvItem = new KvStc();
            int nextLevel = level + 1;
            for (CmmDatadicM dataItem : dataDicLst) {
                if (parentCode.equals(dataItem.getParentcode())) {
                    kvItem = new KvStc(dataItem.getDiccode(),dataItem.getDicname(), dataItem.getParentcode());
                    kvItem.setChildLst(queryChildForDataDic(dataItem.getDiccode(), nextLevel));
                    kvLst.add(kvItem);
                }
            }

            // 添加请选择
            /*if (level <= 3) {
                kvItem = new KvStc("-1", "请选择", "-1");
            }
            if (level <= 2) {
                kvItem.getChildLst().add(new KvStc("-1", "请选择", "-1"));
            }
            if (level <= 1) {
                kvItem.getChildLst().get(0).getChildLst().add(new KvStc("-1", "请选择", "-1"));
            }
            kvLst.add(0,kvItem);*/
        }
        return kvLst;
    }



    /**
     * 打招呼中无效终端的提示框
     * @author 吴欣伟
     * @param visitM 拜访记录
     * @param termInfo 终端档案信息
     */
    public void dialogInValidTerm(final MstVisitMTemp visitM, final MstTerminalinfoMTemp termInfo , String seeFlag) {

        // 加载弹出窗口layout
        final View itemForm = LayoutInflater.from(context).inflate(R.layout.shopvisit_sayhi_invalidterm,null);
        final AlertDialog invalidTermDialgo = new AlertDialog.Builder(context).setCancelable(false).create();
        invalidTermDialgo.setView(itemForm, 0, 0, 0, 0);
        invalidTermDialgo.show();
        // 确定
        Button sureBt = (Button)itemForm.findViewById(R.id.invalid_bt_baocun);
        if(ConstValues.FLAG_1.equals(seeFlag)){
            sureBt.setEnabled(false);
        }
        sureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //AndroidDatabaseConnection connection = null;
                try {
                    DatabaseHelper helper = DatabaseHelper.getHelper(context);
                    Dao<MstTerminalinfoM, String> terDao = helper.getMstTerminalinfoMDao();
                    Dao<MstTerminalinfoMTemp, String> termTempDao = helper.getMstTerminalinfoMTempDao();
                    Dao<MstInvalidapplayInfo, String> invalidDao = helper.getMstInvalidapplayInfoDao();
                        /*connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
                        connection.setAutoCommit(false);*/

                    //更改终端档案主表的状态:无效
                    //终端状态还是有效，但是申请表为未审核
                    termInfo.setStatus(ConstValues.FLAG_3);
                    termInfo.setPadisconsistent("0");

                    //添加无效申请
                    MstInvalidapplayInfo mstInvalid = new MstInvalidapplayInfo();
                    mstInvalid.setApplaykey(FunUtil.getUUID());
                    mstInvalid.setVisitkey(visitM.getVisitkey());
                    mstInvalid.setTerminalkey(termInfo.getTerminalkey());
                    mstInvalid.setStatus(ConstValues.FLAG_0);
                    mstInvalid.setVisitdate(visitM.getVisitdate());
                    mstInvalid.setApplaytype(ConstValues.FLAG_0);
                    RadioGroup rg = (RadioGroup) itemForm.findViewById(R.id.invalid_rg_reason);
                    RadioButton rb = (RadioButton) itemForm.findViewById(rg.getCheckedRadioButtonId());
                    mstInvalid.setApplaycause(rb.getText().toString());
                    EditText content = (EditText) itemForm.findViewById(R.id.invalid_et_reason);
                    mstInvalid.setContent(content.getText().toString());
                    //mstInvalid.setApplayuser(ConstValues.loginSession.getUserCode());
                    mstInvalid.setApplayuser(PrefUtils.getString(context, "userCode", ""));
                    mstInvalid.setApplaydate(DateUtil.formatDate(new Date(), "yyyyMMddHHmmss"));
                    mstInvalid.setPadisconsistent(ConstValues.FLAG_0);
                    mstInvalid.setCredate(new Date());
                    //mstInvalid.setCreuser(ConstValues.loginSession.getUserCode());
                    mstInvalid.setCreuser(PrefUtils.getString(context, "userCode", ""));
                    mstInvalid.setUpdatetime(new Date());
                    //mstInvalid.setUpdateuser(ConstValues.loginSession.getUserCode());
                    mstInvalid.setUpdateuser(PrefUtils.getString(context, "userCode", ""));

                    //terDao.createOrUpdate(termInfo);
                    termTempDao.createOrUpdate(termInfo);
                    invalidDao.create(mstInvalid);
                    DbtLog.logUtils(TAG, "无效终端记录保存成功");

                    //无效终端申请上传服务器
                    /*UploadDataService service = new UploadDataService(context, handler);
                    service.upload_terminal(false, termInfo.getTerminalkey(), ConstValues.WAIT2);
                    ViewUtil.sendMsg(context, R.string.agencyvisit_msg_oksave);*/
                    invalidTermDialgo.dismiss();

                    ((Activity)context).finish();
                    DbtLog.logUtils(TAG, "无效终端申请返回终端列表");

                    //connection.commit(null);
                } catch (Exception e) {
                    e.printStackTrace();
                    DbtLog.logUtils(TAG, "无效终端申请失败");
                    try {
                        //connection.rollback(null);
                        ViewUtil.sendMsg(context, R.string.agencyvisit_msg_failsave);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        // 取消
        Button cancelBt = (Button)itemForm.findViewById(R.id.invalid_bt_cancle);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(XtSayhiFragment.NOT_TERMSTATUS);
                invalidTermDialgo.dismiss();
            }
        });
    }

    /**
     * 更新终端信息
     *
     * @param termInfo      终端信息
     * @param prevSequence  修改前的拜访顺序
     */
    @SuppressWarnings("unchecked")
    public void updateXtTermInfo(MstTerminalinfoMTemp termInfo,String prevSequence) {

        int msgId = -1;
        if (CheckUtil.isBlankOrNull(termInfo.getTerminalname())) {
            msgId = R.string.termadd_msg_invaltermname;
        } else if ("-1".equals(termInfo.getRoutekey()) ||CheckUtil.isBlankOrNull(termInfo.getRoutekey())) {
            msgId = R.string.termadd_msg_invalbelogline;
        } else if ("-1".equals(termInfo.getTlevel()) ||CheckUtil.isBlankOrNull(termInfo.getTlevel())) {
            msgId = R.string.termadd_msg_invaltermlevel;
        } /*else if ("-1".equals(termInfo.getProvince()) || CheckUtil.isBlankOrNull(termInfo.getProvince())) {
            msgId = R.string.termadd_msg_invalprov;
        } else if ("-1".equals(termInfo.getCity()) ||CheckUtil.isBlankOrNull(termInfo.getCity())) {
            msgId = R.string.termadd_msg_invalcity;
        } else if ("-1".equals(termInfo.getCounty())) {
            msgId = R.string.termadd_msg_invalcountry;
        }*/ else if (CheckUtil.isBlankOrNull(termInfo.getAddress())) {
            msgId = R.string.termadd_msg_invaladdress;
        } else if (CheckUtil.isBlankOrNull(termInfo.getContact())) {
            msgId = R.string.termadd_msg_invalcontact;

        }
        //        else if (CheckUtil.isBlankOrNull(termInfo.getMobile())) {
        //            msgId = R.string.termadd_msg_invalmobile;
        //
        //        }
/*        else if (CheckUtil.isBlankOrNull(termInfo.getSequence())) {
            msgId = R.string.termadd_msg_invalsequence;

        } */
        else if ("-1".equals(termInfo.getSellchannel()) ||CheckUtil.isBlankOrNull(termInfo.getSellchannel())) {
            msgId = R.string.termadd_msg_invalsellchannel;

        } else if ("-1".equals(termInfo.getMainchannel()) ||CheckUtil.isBlankOrNull(termInfo.getMainchannel())) {
            msgId = R.string.termadd_msg_invalmainchannel;

        } else if ("-1".equals(termInfo.getMinorchannel()) ||
                CheckUtil.isBlankOrNull(termInfo.getMinorchannel())) {
            msgId = R.string.termadd_msg_invalminorchannel;
        }

        //TextView sureBt = (TextView)((Activity)context).findViewById(R.id.banner_navigation_bt_confirm);
        //sureBt.setTag(msgId);
        // 弹出提示信息
        if (msgId != -1) {
            //GlobalValues.isSayHiSure = false;
            //sendMsg(context, msgId, ConstValues.WAIT2);
            Message message = new Message();
            message.obj = msgId;
            message.what = XtSayhiFragment.DATA_ARROR;
            handler.handleMessage(message);
        } else {
            //GlobalValues.isSayHiSure = true;
            try {
                DatabaseHelper helper = DatabaseHelper.getHelper(context);
                MstTerminalinfoMTempDao dao = helper.getDao(MstTerminalinfoMTemp.class);
                /*
                SQLiteDatabase db = helper.getReadableDatabase();
                    List<MstTerminalinfoM> mstTerminalinfoMList = new ArrayList<MstTerminalinfoM>();
                    StringBuffer buffer = new StringBuffer();
                    buffer.append("select  t.terminalkey,t.sequence from mst_terminalinfo_m t ");
                    buffer.append("where routekey = '");
                    buffer.append(termInfo.getRoutekey());
                    buffer.append("' ");
                    buffer.append("order by (sequence+0) asc, orderbyno, terminalname");
                    Cursor cursor  = db.rawQuery(buffer.toString(), null);
                    MstTerminalinfoM mstTerminalinfoM;
                    while (cursor.moveToNext()) {
                        mstTerminalinfoM =new MstTerminalinfoM();
                        mstTerminalinfoM.setTerminalkey(cursor.getString(cursor.getColumnIndex("terminalkey")));
                        mstTerminalinfoM.setSequence(cursor.getString(cursor.getColumnIndex("sequence")));
                        mstTerminalinfoMList.add(mstTerminalinfoM);
                    }*/
                // 更新终端信息
                dao.update(termInfo);
            } catch (SQLException e) {
                Log.e(TAG, "获取终端表DAO对象失败", e);
            }
        }
    }

    /**
     * 保存拜访主表信息
     *
     * @param visitInfo
     */
    public void updateVisit(MstVisitMTemp visitInfo) {

        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstVisitMTempDao dao = helper.getDao(MstVisitMTemp.class);
            dao.update(visitInfo);

        } catch (SQLException e) {
            Log.e(TAG, "获取拜访主表DAO对象失败", e);
        }
    }


}
