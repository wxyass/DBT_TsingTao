package et.tsingtaopad.main.visit.shopvisit.termvisit.camera;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.ui.camera.CameraImageBean;
import et.tsingtaopad.core.util.callback.CallbackManager;
import et.tsingtaopad.core.util.callback.CallbackType;
import et.tsingtaopad.core.util.callback.IGlobalCallback;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FileUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.util.file.FileTool;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitMTemp;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.test.datatonext.SecondCoreFragment;
import et.tsingtaopad.util.DialogUtil;
import et.tsingtaopad.view.MyGridView;


/**
 * Created by yangwenmin on 2017/12/25.
 * 拍照
 */

public class CameraCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "CameraCoreFragment";
    private boolean isShow = false;// false:默认   true:加载完数据设为true

    private MyGridView picGv;
    private Bitmap photo;
    private Bitmap picphoto;

    private String visitId;// 终端主键,
    private String termId;
    private MstVisitMTemp visitM;
    private String seeFlag; // 拜访0 查看1
    private String visitKey;// 拜访主键
    private String isFirstVisit;
    private String termname;// 终端名称
    private String visitDate;
    private String lastTime;

    private AlertDialog dialog;

    // 后台配置拍照类型集合
    // private List<String> mPicDescLst;
    // 标记点击时条目位置
    private int positionTag;
    // 适配器
    private GridAdapter gridAdapter;
    // 拍照得到的图片集合(初始长度为0)
    //Map<Integer, Bitmap> cameraPicMap;
    // 拍过照的条目设置标记 0:该位置未拍过照 1:该位置拍过照
    Map<Integer, String> isCameraMap;
    // 本地图片路径
    private String filePath;


    // 读取数据库图片表已拍照记录
    private List<CameraInfoStc> piclst = new ArrayList<CameraInfoStc>();
    // 当前时间 如:20110411
    private String currentdata;
    // 查询数据库 要拍几张照片
    private List<CameraInfoStc> valueLst;

    // 查看时的提示
    TextView descTv;
    ScrollView descGv;

    private int idsuccess = -1; // 0在本地保存成功 1在本地还没保存成功
    //private Bitmap bitmap;// 拍照前默认图片
    private CameraService cameraService;

    // 图片保存路径
    private String path;
    private String name;

    private MstTerminalinfoMTemp termTemp;
    private String channelId;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (msg.what == 1) {// 拍照完成,刷新页面
                //
                if (dialog != null) {
                    dialog.dismiss();
                }
                gridAdapter.notifyDataSetChanged();
            }
            else if (msg.what == 2) {// 图片正在保存,请稍后
                Toast.makeText(getActivity(), "图片正在保存,请稍后", Toast.LENGTH_SHORT).show();
            }
            else if (msg.what == 3) {// 弹出滚动条, 替换2
                // 弹出进度框
                dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
                dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.camera_progress, null), 0, 0, 0, 0);
                dialog.setCancelable(false); // 是否可以通过返回键 关闭
                dialog.show();
            }
            else if (msg.what == 4) {// 拍照处理成功 取消滚动条, 拍照完成,刷新页面
                if (dialog != null) {
                    dialog.dismiss();
                }
                gridAdapter.notifyDataSetChanged();
            }
            else if (msg.what == 6) {// 拍照处理失败 取消滚动条, 拍照完成,刷新页面
                if (dialog != null) {
                    dialog.dismiss();
                }
                gridAdapter.notifyDataSetChanged();
                Toast.makeText(getActivity(), "图片保存失败,请重新拍照", Toast.LENGTH_SHORT).show();
                DbtLog.logUtils(TAG, "onActivityResult()-图片保存失败,请重新拍照");
            }
        }
    };

    public CameraCoreFragment() {
    }

    public static CameraCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        CameraCoreFragment fragment = new CameraCoreFragment();
        return fragment;
    }

    public static CameraCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, "newInstance(Bundle args)");
        //Bundle args = new Bundle();

        CameraCoreFragment fragment = new CameraCoreFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            termId = bundle.getString("termId");
            visitId = visitKey = bundle.getString("visitKey");
            seeFlag = FunUtil.isBlankOrNullTo(bundle.getString("seeFlag"), "");// 0:拜访   1:查看
            isFirstVisit = bundle.getString("isFirstVisit");
            termname = bundle.getString("termname");
            visitDate = bundle.getString("visitDate");// 上一次的拜访时间(用于促销活动 状态隔天关闭)
            lastTime = bundle.getString("lastTime");// 上一次的拜访时间(用于促销活动 状态隔天关闭)
        }
    }

    // 只用来初始化控件
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_camera, null);
        this.initView(view);

        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        dialog = DialogUtil.progressDialog(getContext(), R.string.camera_msg_progress);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        descGv = (ScrollView) view.findViewById(R.id.visitshop_sv_desc);
        picGv = (MyGridView) view.findViewById(R.id.gv_camera);
        descTv = (TextView) view.findViewById(R.id.visitshop_tv_desc);
    }

    // 用来初始化数据
    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");

    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG, "onSupportVisible");

        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG, "initData");

        // 如果不是查看,图片列表展示 提示消失
        if (!ConstValues.FLAG_1.equals(seeFlag)) {
            descGv.setVisibility(View.VISIBLE);
            descTv.setVisibility(View.GONE);
        } else { // 如果是查看,图片列表消失 提示展示
            descGv.setVisibility(View.GONE);
            descTv.setVisibility(View.VISIBLE);
            descTv.setText("图片上传后本地会自动删除,查看模式下不能显示,只能通过服务器后台查看");
        }

        cameraService = new CameraService(getActivity(), null);
        initpic();


        isShow = true;
    }

    // 初始化照片 及已拍照片
    private void initpic() {

        DbtLog.logUtils(TAG, "initpic");

        // 获取当前时间 如:20110411
        currentdata = DateUtil.getDateTimeStr(0);

        // 初始化需要拍几张图
        // 查询图片类型表,获取需要拍几张图片 valueLst = new ArrayList<PictypeDataStc>();
        valueLst = cameraService.queryPictypeMAll();
        // 获取最新的终端数据
        termTemp = cameraService.findTermTempById(termId);
        // 根据促销活动,添加促销活动照片类型,注意initData()中先走了一遍(?)--160927----
        List<CheckIndexPromotionStc> promotionLst = cameraService.queryPromotion(visitKey, termTemp.getSellchannel(), termTemp.getTlevel());

        // 根据图片类型表Mst_pictype_M  判断普通图片 是不是大区配置的  还是二级区域配置的
        String isbigerae = "0";  // 默认大区配置  0:大区配置   1:二级区域配置
        if (valueLst.size() > 0) {// 有普通照片
            // 查看普通照片中,是否有二级区域的记录
            if (PrefUtils.getString(getActivity(), "disId", "").equals(valueLst.get(0).getAreaid())) {
                isbigerae = "1";// 1:二级区域配置
            }
        } else {// 没有普通照片,查看促销活动,
            for (CheckIndexPromotionStc pictypeDataStc : promotionLst) {
                // 活动表记录 是二级区域的且是拍照类型
                if (PrefUtils.getString(getActivity(), "disId", "").equals(pictypeDataStc.getAreaid()) && "1".equals(pictypeDataStc.getIspictype())) {
                    isbigerae = "1";// 1:二级区域配置
                }
            }
        }

        String twoareaid = PrefUtils.getString(getActivity(), "disId", "");
        // 根据是不是大区或者二级区域 显示不同的促销活动拍照
        if ("1".equals(isbigerae)) {// 二级配置  0:大区  1:二级区域
            // 1:二级区域
            String picname = "";
            CameraInfoStc cameraInfoStc = null;
            for (int i = 0; i < promotionLst.size(); i++) {

                if ("1".equals(promotionLst.get(i).getIspictype())
                        && "1".equals(promotionLst.get(i).getIsAccomplish())
                        && twoareaid.equals(promotionLst.get(i).getAreaid())) {// 有达成的活动

                    // 截取促销活动名称前7位
                    picname = promotionLst.get(i).getPromotName();
                    if (promotionLst.get(i).getPromotName().length() > 7) {
                        picname = promotionLst.get(i).getPromotName().substring(0, 7);
                    }
                    // 参数: 图片类型主键(促销活动主键),清晰度,排序orderno,图片类型名称(促销活动名称)
                    cameraInfoStc = new CameraInfoStc();
                    cameraInfoStc.setPictypekey(promotionLst.get(i).getPromotKey());
                    cameraInfoStc.setFocus("1");
                    cameraInfoStc.setOrderno((i + 4) + "");
                    cameraInfoStc.setPictypename(picname);
                    valueLst.add(cameraInfoStc);
                }
            }
        } else {
            // 0:大区
            String picname = "";
            CameraInfoStc cameraInfoStc = null;
            for (int i = 0; i < promotionLst.size(); i++) {

                if ("1".equals(promotionLst.get(i).getIspictype()) && "1".equals(promotionLst.get(i).getIsAccomplish())
                        && (!twoareaid.equals(promotionLst.get(i).getAreaid()))) {// 有达成的活动

                    // 截取促销活动名称前7位
                    picname = promotionLst.get(i).getPromotName();
                    if (promotionLst.get(i).getPromotName().length() > 7) {
                        picname = promotionLst.get(i).getPromotName().substring(0, 7);
                    }
                    // 参数: 图片类型主键(促销活动主键),清晰度,排序orderno,图片类型名称(促销活动名称)
                    cameraInfoStc = new CameraInfoStc();
                    cameraInfoStc.setPictypekey(promotionLst.get(i).getPromotKey());
                    cameraInfoStc.setFocus("1");
                    cameraInfoStc.setOrderno((i + 4) + "");
                    cameraInfoStc.setPictypename(picname);
                    valueLst.add(cameraInfoStc);
                }
            }
        }

        // 没有图片类型时,给与提示
        if (valueLst == null || valueLst.size() <= 0) {
            descGv.setVisibility(View.GONE);
            descTv.setVisibility(View.VISIBLE);
            descTv.setText("普通图片类型后台未配置或促销活动未达成,不予显示");
        } else if (ConstValues.FLAG_0.equals(seeFlag) && valueLst != null && valueLst.size() > 0) {
            descGv.setVisibility(View.VISIBLE);
            descTv.setVisibility(View.GONE);
        }

        // 获取当天已保存的图片 照片临时表 // 每次切换标签都会重新查询
        piclst = cameraService.queryCurrentPicRecord(termId, visitKey);

        //cameraPicMap = new HashMap<Integer, Bitmap>();

        for (CameraInfoStc cameraDataStc : piclst) {// 已拍的
            for (CameraInfoStc stctc : valueLst) {// 需拍的
                if (cameraDataStc.getPictypekey().equals(stctc.getPictypekey())) {
                    stctc.setCamerakey(cameraDataStc.getCamerakey());
                    stctc.setLocalpath(cameraDataStc.getLocalpath());
                    stctc.setPicindex(cameraDataStc.getPicindex());
                    stctc.setVisitkey(cameraDataStc.getVisitkey());
                    stctc.setTerminalkey(cameraDataStc.getTerminalkey());
                }
            }
        }

        // 设置适配器
        gridAdapter = new GridAdapter(getContext(), valueLst);
        picGv.setAdapter(gridAdapter);

        // 设置item的点击监听
        picGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                CallbackManager.getInstance().addCallback(CallbackType.ON_CROP, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args) {

                        // 弹出提示对话框
                        /*if (dialog != null && !dialog.isShowing()) {
                            dialog.show();
                        }*/
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);// 提示:图片正在保存,请稍后

                        // 保存到表
                        CameraInfoStc cameraDataStc = valueLst.get(position);

                        CameraImageBean cameraImageBean = CameraImageBean.getInstance();
                        String picname = cameraImageBean.getPicname();
                        Uri uri = cameraImageBean.getmPath();
                        // 将图片转成字符串
                        String imagefileString = "";
                        try {
                            // 裁剪 压缩
                            Bitmap bitmap = getBitmapFormUri(getContext(), uri);
                            // 添加水印
                            bitmap = cameraService.addWaterBitmap(bitmap,DateUtil.getDateTimeStr(3),"username",termname,cameraDataStc.getPictypename());
                            // 删除原图
                            FileUtil.deleteFile(new File(FileTool.CAMERA_PHOTO_DIR + picname));
                            // 保存小图
                            FunUtil.saveHeadImg(FileTool.CAMERA_PHOTO_DIR, bitmap, picname, 100, 25);
                            // 图片转成字符串
                            imagefileString = FileUtil.fileToString(FileTool.CAMERA_PHOTO_DIR + picname);
                            // 将图片记录保存到数据库
                            cameraService.savePicData(cameraDataStc, picname, imagefileString,termId,visitKey);

                            // 更新UI界面 刷新适配器
                            Message message1 = new Message();
                            message1.what = 4;
                            handler.sendMessage(message1);// 刷新UI

                            gridAdapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 拍照弹窗
                startCameraWithCheck();
            }
        });

    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            pop();
            // 确定
        } else if (i == R.id.banner_navigation_rl_confirm) {
            getSupportDelegate().start(SecondCoreFragment.newInstance());
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);// hidden true:该界面已隐藏   false: 正在展示
        if (hidden && isShow) {
            isShow = false;
            Toast.makeText(getContext(), "正在保存拍照数据", Toast.LENGTH_SHORT).show();
        }
    }
}
