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
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.dao.MstPictypeMDao;
import et.tsingtaopad.db.table.MstCameraInfoMTemp;
import et.tsingtaopad.dd.ddxt.shopvisit.XtShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;

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

    // 保存照片记录到数据库
    public String savePicData(CameraInfoStc cameraDataStc, String picname, String imagefileString, String termId, String visitKey) {

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
                mstCameraInfoMTemp.setLocalpath(picname);// 本地图片路径
                mstCameraInfoMTemp.setNetpath(picname);// 请求网址(原先ftp上传用,现已不用)
                mstCameraInfoMTemp.setCameradata(DateUtil.getDateTimeStr(1));// 拍照时间
                mstCameraInfoMTemp.setIsupload("0");// 是否已上传 0未上传  1已上传
                mstCameraInfoMTemp.setIstakecamera("1");//
                //mstCameraInfoMTemp.setPicindex();// 图片所在角标
                mstCameraInfoMTemp.setPictypename(cameraDataStc.getPictypename());// 图片类型(中文名称)
                mstCameraInfoMTemp.setSureup("0");// 确定上传 0确定上传 1未确定上传
                mstCameraInfoMTemp.setImagefileString(imagefileString);// 将图片文件转成String保存在数据库
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
                proItemTempDao.createOrUpdate(mstCameraInfoMTemp);
            }


        } catch (Exception e) {
            Log.e(TAG, "获取拜访拍照临时表DAO对象失败", e);
        }

        return mstCameraInfoMTemp.getCamerakey();
    }

}
