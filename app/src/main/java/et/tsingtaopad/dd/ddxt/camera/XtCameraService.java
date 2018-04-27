package et.tsingtaopad.dd.ddxt.camera;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.j256.ormlite.android.AndroidDatabaseConnection;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstPictypeMDao;
import et.tsingtaopad.db.table.MitValpicMTemp;
import et.tsingtaopad.db.table.MstCameraInfoMTemp;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.sayhi.domain.MstTerminalInfoMStc;

/**
 * 文件名：XtShopVisitService.java</br>
 * 功能描述: </br>
 */
public class XtCameraService extends XtShopVisitService {

    private final String TAG = "XtCameraService";

    public XtCameraService(Context context, Handler handler) {
        super(context, handler);
    }

    /***
     * 查询图片类型表中所有记录
     */
    public List<CameraInfoStc> queryPictypeMAll() {
        // 事务控制
        AndroidDatabaseConnection connection = null;
        List<CameraInfoStc> valueLst = new ArrayList<CameraInfoStc>();
        try {

            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPictypeMDao dao = (MstPictypeMDao) helper.getMstpictypeMDao();

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            valueLst = dao.queryAllPictype(helper);
            connection.commit(null);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "查询图片类型表中所有记录", e);
        }
        return valueLst;
    }
    /***
     * 查询图片类型表中所有记录 用于追溯
     */
    public List<MitValpicMTemp> queryZsPictypeMAll() {
        // 事务控制
        AndroidDatabaseConnection connection = null;
        List<MitValpicMTemp> valueLst = new ArrayList<MitValpicMTemp>();
        try {

            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstPictypeMDao dao = (MstPictypeMDao) helper.getMstpictypeMDao();

            connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
            connection.setAutoCommit(false);

            valueLst = dao.queryZsAllPictype(helper);
            connection.commit(null);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e(TAG, "查询图片类型表中所有记录", e);
        }
        return valueLst;
    }

    /**
     * 获取当天拍照记录
     *
     * @param terminalkey 终端主键
     * @param visitKey    拜访主键
     * @return
     */
    public List<CameraInfoStc> queryCurrentPicRecord(String terminalkey, String visitKey) {
        DbtLog.logUtils(TAG, "queryCurrentPicRecord()-获取当天拍照记录");
        List<CameraInfoStc> lst = new ArrayList<CameraInfoStc>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
            lst = dao.queryCurrentCameraLst(helper, terminalkey, visitKey);

        } catch (Exception e) {
            Log.e(TAG, "获取拜访拍照临时表DAO对象失败", e);
        }
        return lst;
    }
    /**
     * 获取当天追溯拍照记录
     *
     * @param terminalkey 终端主键
     * @param valterid    追溯主键
     * @return
     */
    public List<MitValpicMTemp> queryZsCurrentPicRecord(String terminalkey, String valterid) {
        DbtLog.logUtils(TAG, "queryCurrentPicRecord()-获取当天拍照记录");
        List<MitValpicMTemp> lst = new ArrayList<MitValpicMTemp>();
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
            lst = dao.queryZsCurrentCameraLst(helper, terminalkey, valterid);

        } catch (Exception e) {
            Log.e(TAG, "获取拜访拍照临时表DAO对象失败", e);
        }
        return lst;
    }

    // 保存照片记录到数据库
    public String savePicData(CameraInfoStc cameraDataStc, String picname, String imagefileString,
                              String termId, MstTerminalinfoMTemp termTemp, String visitKey,MstTerminalInfoMStc mstTerminalInfoMStc) {

        DbtLog.logUtils(TAG, "queryCurrentPicRecord()-获取当天拍照记录");
        List<CameraInfoStc> lst = new ArrayList<CameraInfoStc>();
        MstCameraInfoMTemp mstCameraInfoMTemp = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MstCameraInfoMTemp, String> proItemTempDao = helper.getMstCameraInfoMTempDao();

            if (CheckUtil.isBlankOrNull(cameraDataStc.getCamerakey()) || "".equals(cameraDataStc.getCamerakey())) {
                mstCameraInfoMTemp = new MstCameraInfoMTemp();
                mstCameraInfoMTemp.setCamerakey(FunUtil.getUUID());// 图片主键
                mstCameraInfoMTemp.setTerminalkey(termId);// 终端主键
                mstCameraInfoMTemp.setVisitkey(visitKey); // 拜访主键
                mstCameraInfoMTemp.setPictypekey(cameraDataStc.getPictypekey());// 图片类型主键(UUID)
                mstCameraInfoMTemp.setPicname(picname);// 图片名称
                mstCameraInfoMTemp.setLocalpath(termTemp.getTerminalname());// 本地图片路径 -> 改为终端名称
                mstCameraInfoMTemp.setNetpath(picname);// 请求网址
                mstCameraInfoMTemp.setCameradata(DateUtil.getDateTimeStr(1));// 拍照时间
                mstCameraInfoMTemp.setIsupload("0");// 是否已上传 0未上传  1已上传
                mstCameraInfoMTemp.setIstakecamera("1");//
                //mstCameraInfoMTemp.setPicindex();// 图片所在角标
                mstCameraInfoMTemp.setPictypename(cameraDataStc.getPictypename());// 图片类型(中文名称)
                mstCameraInfoMTemp.setSureup("0");// 确定上传 0确定上传 1未确定上传
                mstCameraInfoMTemp.setImagefileString(imagefileString);// 将图片文件转成String保存在数据库
                mstCameraInfoMTemp.setAreaid(mstTerminalInfoMStc.getAreaid());
                mstCameraInfoMTemp.setAreapid(mstTerminalInfoMStc.getAreapid());
                mstCameraInfoMTemp.setGridkey(mstTerminalInfoMStc.getGridkey());
                mstCameraInfoMTemp.setRoutekey(mstTerminalInfoMStc.getRoutekey());
                proItemTempDao.create(mstCameraInfoMTemp);
            } else {// 更新照片记录
                mstCameraInfoMTemp = new MstCameraInfoMTemp();
                mstCameraInfoMTemp.setCamerakey(cameraDataStc.getCamerakey());// 图片主键
                mstCameraInfoMTemp.setTerminalkey(cameraDataStc.getTerminalkey());// 终端主键
                mstCameraInfoMTemp.setVisitkey(cameraDataStc.getVisitkey()); // 拜访主键
                mstCameraInfoMTemp.setPictypekey(cameraDataStc.getPictypekey());// 图片类型主键(UUID)
                mstCameraInfoMTemp.setPicname(picname);// 图片名称
                mstCameraInfoMTemp.setLocalpath(picname);// 本地图片路径
                mstCameraInfoMTemp.setNetpath(picname);// 请求网址(原先ftp上传用,现已不用)
                mstCameraInfoMTemp.setCameradata(DateUtil.getDateTimeStr(1));// 拍照时间
                mstCameraInfoMTemp.setIsupload("0");// 是否已上传 0未上传  1已上传
                mstCameraInfoMTemp.setIstakecamera("1");//
                //mstCameraInfoMTemp.setPicindex();// 图片所在角标
                mstCameraInfoMTemp.setPictypename(cameraDataStc.getPictypename());// 图片类型(中文名称)
                mstCameraInfoMTemp.setSureup("0");// 确定上传 0确定上传 1未确定上传
                mstCameraInfoMTemp.setImagefileString(imagefileString);// 将图片文件转成String保存在数据库
                mstCameraInfoMTemp.setAreaid(mstTerminalInfoMStc.getAreaid());
                mstCameraInfoMTemp.setAreapid(mstTerminalInfoMStc.getAreapid());
                mstCameraInfoMTemp.setGridkey(mstTerminalInfoMStc.getGridkey());
                mstCameraInfoMTemp.setRoutekey(mstTerminalInfoMStc.getRoutekey());
                proItemTempDao.createOrUpdate(mstCameraInfoMTemp);
            }


        } catch (Exception e) {
            Log.e(TAG, "获取拜访拍照临时表DAO对象失败", e);
        }

        return mstCameraInfoMTemp.getCamerakey();
    }


    // 保存照片记录到追溯 数据库
    public String saveZsPicData(MitValpicMTemp valpicMTemp, String picname, String imagefileString,
                              String termId,  String valterid,MstTerminalInfoMStc mstTerminalInfoMStc) {

        DbtLog.logUtils(TAG, "queryCurrentPicRecord()-获取当天拍照记录");
        //List<CameraInfoStc> lst = new ArrayList<CameraInfoStc>();
        MitValpicMTemp mstCameraInfoMTemp = null;
        try {
            DatabaseHelper helper = DatabaseHelper.getHelper(context);
            Dao<MitValpicMTemp, String> proItemTempDao = helper.getMitValpicMTempDao();

            if (CheckUtil.isBlankOrNull(valpicMTemp.getId()) || "".equals(valpicMTemp.getId())) {
                mstCameraInfoMTemp = new MitValpicMTemp();
                mstCameraInfoMTemp.setId(FunUtil.getUUID());// 图片主键
                mstCameraInfoMTemp.setValterid(valterid); // 追溯主键
                mstCameraInfoMTemp.setPictypekey(valpicMTemp.getPictypekey());// 图片类型主键(UUID)
                mstCameraInfoMTemp.setPicname(picname);// 图片名称
                mstCameraInfoMTemp.setPictypename(valpicMTemp.getPictypename());// 图片类型(中文名称)
                mstCameraInfoMTemp.setImagefileString(imagefileString);// 将图片文件转成String保存在数据库
                mstCameraInfoMTemp.setAreaid(mstTerminalInfoMStc.getAreaid());// 二级区域
                mstCameraInfoMTemp.setGridkey(mstTerminalInfoMStc.getGridkey());//定格
                mstCameraInfoMTemp.setRoutekey(mstTerminalInfoMStc.getRoutekey());// 路线
                mstCameraInfoMTemp.setTerminalkey(termId);// 终端主键
                proItemTempDao.create(mstCameraInfoMTemp);
            } else {// 更新照片记录
                /*mstCameraInfoMTemp = new MitValpicMTemp();
                mstCameraInfoMTemp.setId(valpicMTemp.getId());// 图片主键
                mstCameraInfoMTemp.setValterid(valpicMTemp.getValterid()); // 追溯主键
                mstCameraInfoMTemp.setPictypekey(valpicMTemp.getPictypekey());// 图片类型主键(UUID)
                mstCameraInfoMTemp.setPicname(picname);// 图片名称
                mstCameraInfoMTemp.setPictypename(valpicMTemp.getPictypename());// 图片类型(中文名称)
                mstCameraInfoMTemp.setImagefileString(imagefileString);// 将图片文件转成String保存在数据库
                mstCameraInfoMTemp.setAreaid(mstTerminalInfoMStc.getAreaid());// 二级区域
                mstCameraInfoMTemp.setGridkey(mstTerminalInfoMStc.getGridkey());//定格
                mstCameraInfoMTemp.setRoutekey(mstTerminalInfoMStc.getRoutekey());// 路线
                mstCameraInfoMTemp.setTerminalkey(valpicMTemp.getTerminalkey());// 终端主键*/

                valpicMTemp.setPicname(picname);// 图片名称
                valpicMTemp.setImagefileString(imagefileString);// 将图片文件转成String保存在数据库
                proItemTempDao.createOrUpdate(mstCameraInfoMTemp);
            }
        } catch (Exception e) {
            Log.e(TAG, "获取拜访拍照临时表DAO对象失败", e);
        }

        return mstCameraInfoMTemp.getId();
    }

}
