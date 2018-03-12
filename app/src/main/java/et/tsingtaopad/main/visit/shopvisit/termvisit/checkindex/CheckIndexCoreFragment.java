package et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.AndroidDatabaseConnection;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.adapter.SpinnerKeyValueAdapter;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.ui.camera.CameraImageBean;
import et.tsingtaopad.core.util.callback.CallbackManager;
import et.tsingtaopad.core.util.callback.CallbackType;
import et.tsingtaopad.core.util.callback.IGlobalCallback;
import et.tsingtaopad.core.util.dbtutil.CheckUtil;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FileUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.util.file.FileTool;
import et.tsingtaopad.db.DatabaseHelper;
import et.tsingtaopad.db.dao.MstCameraiInfoMDao;
import et.tsingtaopad.db.table.MstGroupproductMTemp;
import et.tsingtaopad.db.table.MstTerminalinfoMTemp;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.ShopVisitService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.CameraService;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraDataStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.adapter.CaculateAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.adapter.QuicklyDialogItemAdapter;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndexValue;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.QuicklyProItem;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.listener.IClick;
import et.tsingtaopad.view.NoScrollListView;
import et.tsingtaopad.view.SlideSwitch;

/**
 * Created by yangwenmin on 2017/12/25.
 * 查指标
 */

public class CheckIndexCoreFragment extends BaseCoreFragment implements SlideSwitch.OnSwitchChangedListener,View.OnClickListener {

    public static final String TAG = "CheckIndexCoreFragment";
    private boolean isShow = false;// false:默认   true:加载完数据设为true

    private String seeFlag;
    private String termId;
    private String visitKey;
    private String isFirstVisit;
    private String termname;
    private String visitDate;
    private String lastTime;


    private CheckIndexService service;
    private String visitId;
    private String channelId;

    // 分期采集、采集项、快速采集、非关联产品指标、促销活动
    private List<ProIndex> calculateLst;
    private List<ProItem> proItemLst;
    private List<QuicklyProItem> quicklyProItemLst;
    private List<CheckIndexCalculateStc> noProIndexLst;
    private List<CheckIndexPromotionStc> promotionLst;

    private Button quickCollectBt;
    private ListView calculateLv;
    private NoScrollListView promotionLv;
    private CaculateAdapter calculateAdapter;
    private PromotionAdapter promotionAdapter;

    // 动态加载与产品无关指标采集数据相关组件
    private LinearLayout noProlayout;
    private LayoutInflater inflater;

    // 快速采集弹出框相关组件对象
    private AlertDialog quicklyDialog;
    private View itemForm;
    private LinearLayout quicklyDialogLv;
    private ScrollView dialogSv;
    //是否在加载数据
    //private boolean isLoadingData=true;
    // 是否本次拜访第一次点开此界面 第一次1 非第一次2
    private int isfrist = 1;
    private MstTerminalinfoMTemp termTemp;


    private int idsuccess = -1; // 0在本地保存成功 1在本地还没保存成功

    AlertDialog dialog;

    //MstGroupproductM vo = null;

    private CameraService cameraService;
    private List<CameraDataStc> piclst;
    private int position;

    //private TextView groupproductNameTv;
    //private RadioGroup groupRg ;
    private SlideSwitch groupStatusSw;
    private LinearLayout progroupLL;

    private SlideSwitch hezuoSw;
    private Spinner zhanyoulvSp;
    private SlideSwitch peisongSw;

    private int picindex ;
    private String pictypekey ;
    private String pictypename ;
    private LinearLayout promotionTitle;
    private LinearLayout promotionHead;



    // 当前时间 如:20110411
    private String currentdata;

    private MstGroupproductMTemp groupproTemp = null;

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            switch (msg.what) {

                // 自动计算
                case ConstValues.WAIT5:
                    String as= "";
                    //if(!isLoadingData){
                        String proId = "";
                        String indexId="";
                        if (bundle != null) {
                            proId =  FunUtil.isBlankOrNullTo(bundle.getString("proId"),  "-1");// 产品主键
                            indexId =  FunUtil.isBlankOrNullTo(bundle.getString("indexId"),  "-1");// 指标主键: ad3030fb-e42e-47f8-a3ec-4229089aab5d
                        }
                        service.calculateIndex(channelId, proItemLst, calculateLst, proId,indexId);
                        calculateAdapter.notifyDataSetChanged();
                        ViewUtil.setListViewHeight(calculateLv);
                    //}
                    break;
                // 自动计算
                case 2:
                    //Toast.makeText(getActivity(), "图片正在保存,请稍后", 0).show();
                    break;
                case 3:
                    // 弹出进度框
                    dialog = new AlertDialog.Builder(getActivity()).setCancelable(false).create();
                    dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.camera_progress, null), 0, 0, 0, 0);
                    dialog.setCancelable(false); // 是否可以通过返回键 关闭
                    dialog.show();
                    break;
                case 4:// 图片保存成功, 滚动条消失
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    break;
                case 6:// 图片保存失败, 滚动条消失,并提示
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    Toast.makeText(getActivity(), "图片保存失败,请重新拍照",Toast.LENGTH_SHORT).show();
                    break;

                default:
                    break;
            }
        }
    };


    public CheckIndexCoreFragment() {
    }

    public static CheckIndexCoreFragment newInstance() {
        DbtLog.logUtils(TAG, "newInstance()");
        CheckIndexCoreFragment fragment = new CheckIndexCoreFragment();
        return fragment;
    }

    public static CheckIndexCoreFragment newInstance(Bundle args) {
        DbtLog.logUtils(TAG, "newInstance(Bundle args)");
        //Bundle args = new Bundle();

        CheckIndexCoreFragment fragment = new CheckIndexCoreFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.shopvisit_checkindex, null);
        this.initView(view);
        return view;
    }

    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");

        //快速采集按钮
        quickCollectBt = (Button)view.findViewById(R.id.checkindex_bt_quickcollect);
        promotionTitle = (LinearLayout)view.findViewById(R.id.checkindex_ll_promotion_title);// 促销活动项
        promotionHead = (LinearLayout)view.findViewById(R.id.checkindex_ll_promotion_head);// 促销活动标题栏
        //采项分集listView
        calculateLv = (ListView)view.findViewById(R.id.checkindex_lv_calculate);
        //促销活动listView
        promotionLv = (NoScrollListView)view.findViewById(R.id.checkindex_lv_promotion);

        groupStatusSw = (SlideSwitch)view.findViewById(R.id.noproindex_sw_prostatus);
        progroupLL = (LinearLayout)view.findViewById(R.id.checkindex_ll_group);//产品组合是否达标所在的LL

        hezuoSw = (SlideSwitch)view.findViewById(R.id.noproindex_sw_hezuo);
        zhanyoulvSp = (Spinner)view.findViewById(R.id.noproindex_sp_zhanyoulv);
        peisongSw = (SlideSwitch)view.findViewById(R.id.noproindex_sw_peisong);

        // 获取页面中用于动态添加的容器组件及模板
        noProlayout = (LinearLayout)view.findViewById(R.id.checkindex_lo_noproindex);
        noProlayout.removeAllViews();
        inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        quickCollectBt.setOnClickListener(this);

        groupStatusSw.setOnSwitchChangedListener(this);
        hezuoSw.setOnSwitchChangedListener(this);
        peisongSw.setOnSwitchChangedListener(this);

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
        DbtLog.logUtils(TAG,"onSupportVisible");
        // 每次页面出现后都重新加载数据
        initData();
    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG,"initData");

        long time = new Date().getTime();
        service = new CheckIndexService(getActivity(), null);
        ConstValues.handler = handler;

        // 获取当前时间 如:20110411
        currentdata = DateUtil.getDateTimeStr(0);

        progroupLL.setVisibility(View.VISIBLE);// 产品组合是否达标所在的LL


        isfrist = PrefUtils.getInt(getActivity(), "isfrist"+termId, 1);
        //  获取时间
        if(isfrist==1){
            // 获取上次拜访时间(原因:根据时间设置促销活动隔天关闭)若上次时间为null,设为"1901-01-01 17:31"
            if("".equals(lastTime)&&lastTime.length()<=0){
                lastTime = "1901-01-01 01:01";
            }
            PrefUtils.putInt(getActivity(), "isfrist"+termId, 2);
        }else{
            // 获取最后一次拜访的信息
            ShopVisitService shopVisitService = new ShopVisitService(getActivity(), null);
            MstVisitM visitM = shopVisitService.findNewVisit(termId,false);
            if(visitM==null){// 若此终端从未拜访过 设置此终端的上次拜访时间为1901-01-01 01:01
                lastTime = "1901-01-01 01:01";// 2016-03-08 11:24
                // 读取上次拜访的时间 (查询拜访主表的关于此终端的所有拜访记录,取最新一次,不管是否上传成功过,查询此记录的拜访时间)
            }else{
                lastTime = DateUtil.formatDate(0, visitM.getVisitdate());// 2016-03-08 11:24
            }
        }


        // 获取最新的终端数据
        termTemp = service.findTermTempById(termId);
        channelId = termTemp.getMinorchannel();// 次渠道

        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        calculateLst = service.queryCalculateIndex(visitId, termId, channelId, seeFlag);
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        proItemLst = service.queryCalculateItem(visitId, channelId);
        //FaceAdapter---查指标的分项采集部分一级Adapter
        calculateAdapter = new CaculateAdapter(getContext(), calculateLst, GlobalValues.indexLst, proItemLst);// 上下文,指标对象集合,同步下来的指标集合,所有产品集合
        calculateLv.setAdapter(calculateAdapter);
        ViewUtil.setListViewHeight(calculateLv);

        //获取巡店拜访-查指标的促销活动页面部分的数据
        promotionLst = service.queryPromotion(visitId, termTemp.getSellchannel(), termTemp.getTlevel());

        // 若没有促销活动,隐藏标题及表头
        if(promotionLst.size()>0){
            promotionTitle.setVisibility(View.VISIBLE);
            promotionHead.setVisibility(View.VISIBLE);
        }else{
            promotionTitle.setVisibility(View.GONE);
            promotionHead.setVisibility(View.GONE);
        }

        // 获取后台配置的普通照片类型张数
        List<CameraInfoStc>  valueLst = service.queryPictypeMAll();

        // 根据图片类型表Mst_pictype_M  判断普通图片 是不是大区配置的
        String isbigerae = "0";  // 默认大区配置  0:大区配置   1:二级区域配置
        if(valueLst.size()>0){// 有普通照片
            // 查看普通照片中,是否有二级区域的记录
            if(PrefUtils.getString(getActivity(), "disId", "").equals(valueLst.get(0).getAreaid())){
                isbigerae = "1";// 1:二级区域配置
            }
        }else{// 没有普通照片,查看促销活动,
            for (CheckIndexPromotionStc pictypeDataStc : promotionLst) {
                // 活动表记录 是二级区域的且是拍照类型
                if(PrefUtils.getString(getActivity(), "disId", "").equals(pictypeDataStc.getAreaid())&&"1".equals(pictypeDataStc.getIspictype())){
                    isbigerae = "1";// 1:二级区域配置
                }
            }
        }

        // 获取当天已保存的图片 // 每次切换标签都会重新查询
        final List<CameraInfoStc>  cameraInfoStcs =  service.queryCurrentPicRecord(termId,  visitKey);

        // 对每个活动的 拍照按钮监听
        promotionAdapter = new PromotionAdapter(getActivity(), promotionLst,lastTime,valueLst,isbigerae,seeFlag,service,visitId, termId,new IClick() {

            @Override
            public void listViewItemClick(final int position, View v) {

                final String pictypekey = promotionLst.get((Integer) v.getTag()).getPromotKey();
                final String pictypename = promotionLst.get((Integer) v.getTag()).getPromotName();

                CallbackManager.getInstance().addCallback(CallbackType.ON_CROP, new IGlobalCallback() {
                    @Override
                    public void executeCallback(@Nullable Object args) {

                        // 弹出提示对话框
                        Message message = new Message();
                        message.what = 3;
                        handler.sendMessage(message);// 提示:图片正在保存,请稍后

                        // 在图片临时表记录中找到这张照片,若没有camerakey为null
                        CameraInfoStc cameraDataStc = new CameraInfoStc();
                        cameraDataStc.setTerminalkey(termId);
                        cameraDataStc.setVisitkey(visitKey);
                        cameraDataStc.setPictypekey(pictypekey);
                        cameraDataStc.setPictypename(pictypename);
                        for (CameraInfoStc camera : cameraInfoStcs){
                            if(pictypekey.equals(camera.getPictypekey())){
                                cameraDataStc.setCamerakey(camera.getCamerakey());
                            }
                        }

                        // 获取图片信息: 图片名称,图片Uri
                        CameraImageBean cameraImageBean = CameraImageBean.getInstance();
                        String picname = cameraImageBean.getPicname();
                        Uri uri = cameraImageBean.getmPath();
                        // 将图片转成字符串
                        String imagefileString = "";
                        try {
                            // 裁剪 压缩
                            Bitmap bitmap = getBitmapFormUri(getContext(), uri);
                            // 添加水印
                            bitmap = service.addWaterBitmap(bitmap,DateUtil.getDateTimeStr(3),"username",termname,cameraDataStc.getPictypename());
                            // 删除原图
                            FileUtil.deleteFile(new File(FileTool.CAMERA_PHOTO_DIR + picname));
                            // 保存小图(文件夹路径,图片源,图片名称,图片质量,图片大小K)
                            FunUtil.saveHeadImg(FileTool.CAMERA_PHOTO_DIR, bitmap, picname, 100, 25);
                            // 图片转成字符串
                            imagefileString = FileUtil.fileToString(FileTool.CAMERA_PHOTO_DIR + picname);
                            // 将图片记录保存到数据库
                            service.savePicData(cameraDataStc, picname, imagefileString,termId,visitKey);

                            // 更新UI界面 刷新适配器
                            Message message1 = new Message();
                            message1.what = 4;
                            handler.sendMessage(message1);// 刷新UI

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 拍照弹窗
                startCameraWithCheck();
            }
        });

        promotionLv.setAdapter(promotionAdapter);
        //        ViewUtil.setListViewHeight(promotionLv);

        // 获取与产品无关指标采集数据
        //noProIndexLst = service.queryNoProIndex(visitId, termTemp.getMinorchannel(), seeFlag);
        noProIndexLst = service.queryNoProIndex2(visitId, termTemp.getMinorchannel(), seeFlag);
        long time1 = new Date().getTime();
        Log.e("Optimization", "查指标执行数据库"+(time1-time));
        List<KvStc> tempLst = new ArrayList<KvStc>();

        // 读取上次产品是否达标数据 因为产品组合每次只有一条数据
        groupproTemp = (MstGroupproductMTemp)service.findMstGroupproductMTempByid(termTemp.getTerminalcode());

        // 初始化产品组合是否达标 // 上次拜访没有数据,或者是N,null
        if (CheckUtil.isBlankOrNull(groupproTemp.getIfrecstand()) || "N".equals(groupproTemp.getIfrecstand()) || "null".equals(groupproTemp.getIfrecstand())) {
            groupStatusSw.setStatus(false);
        } else {
            groupStatusSw.setStatus(true);
        }

        // 初始化 合作是否到位 占有率 高质量配送数据
        for (CheckIndexCalculateStc item : noProIndexLst) {
            // 重新查询指标关联指标值 //因为原先的这3个指标 不再关联指标值(pad_checktype_m不再同步这三个指标)
            // 交由pad直接初始化 // ywm 20160407
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(item.getIndexId())) {// 合作是否到位
                //tempLst = service.queryNoProIndexValueId1();// 此处报错
                if (CheckUtil.isBlankOrNull(item.getIndexValueId())) {
                    hezuoSw.setStatus(false);
                } else if ("9019cf03-4572-4559-9971-48a27a611c3d".equals(item.getIndexValueId())) {// 合作是否到位 是
                    hezuoSw.setStatus(true);
                } else if ("8d36d1e5-c776-452e-8893-589ad786d71d".equals(item.getIndexValueId())) {// 合作是否到位 否
                    hezuoSw.setStatus(false);
                }else{
                    hezuoSw.setStatus(false);
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(item.getIndexId())) {// 是否高质量配送
                //tempLst = service.queryNoProIndexValueId2();
                if (CheckUtil.isBlankOrNull(item.getIndexValueId())) {
                    peisongSw.setStatus(false);
                } else if ("460647a9-283a-44ea-b11f-42efe1fd62e4".equals(item.getIndexValueId())) {// 是否高质量配送
                    peisongSw.setStatus(true);
                } else if ("bf600cfe-f70d-4170-857d-65dd59740d57".equals(item.getIndexValueId())) {// 是否高质量配送
                    peisongSw.setStatus(false);
                }else{
                    peisongSw.setStatus(false);
                }

            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(item.getIndexId())) {// 我品占有率
                tempLst = service.queryNoProIndexValueId3();
                zhanyoulvSp.setAdapter(new SpinnerKeyValueAdapter(getActivity(), tempLst, new String[] { "key", "value" }, item.getIndexValueId()));
            }
        }

        isShow = true;
    }

    @Override
    public void onSwitchChanged(SlideSwitch obj, int status) {

        DbtLog.logUtils(TAG, "onSwitchChanged()");

        int i = obj.getId();
        if (i == R.id.noproindex_sw_prostatus) {// 产品组合是否达标
            if (status == SlideSwitch.SWITCH_ON) {
                groupStatusSw.setStatus(true);
            } else {
                groupStatusSw.setStatus(false);
            }
        } else if (i == R.id.noproindex_sw_hezuo) {// 合作是否到位

            if (status == SlideSwitch.SWITCH_ON) {
                hezuoSw.setStatus(true);
            } else {
                hezuoSw.setStatus(false);
            }
        }else if (i == R.id.noproindex_sw_peisong) {//

            if (status == SlideSwitch.SWITCH_ON) {
                peisongSw.setStatus(true);
            } else {
                peisongSw.setStatus(false);
            }
        }
    }

    @Override
    public void onClick(View v) {
        DbtLog.logUtils(TAG, "onClick");

        int i = v.getId();
        if(i== R.id.checkindex_bt_quickcollect){// 快速采集按钮
            if (ViewUtil.isDoubleClick(v.getId(), 2500)) return;
            qulicklyDialog();
        }
    }



    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);// hidden true:该界面已隐藏   false: 正在展示

        if(hidden && isShow){
            isShow = false;
            //Toast.makeText(getContext(),"正在保存查指标数据",Toast.LENGTH_SHORT).show();
            saveCheckIndexData();
        }
    }

    private void saveCheckIndexData(){

        DbtLog.logUtils(TAG, "saveCheckIndexData()");
        long time3 = new Date().getTime();
        // 如果是查看操作，则不做数据校验及数据处理
        if (ConstValues.FLAG_1.equals(seeFlag)) return;
        //if(isLoadingData) return;

        ProIndex indexItem;
        ProIndexValue valueItem;
        ListView indexValueLv;
        KvStc kvItem;
        RadioGroup resultRg;
        RadioButton resultRb;
        EditText resultEt;
        Spinner resultSp;

        // 遍历lv，获取各指标的指标值
        for (int i = 0; i < calculateLst.size(); i++) {
            if (calculateLv == null || calculateLv.getChildAt(i) == null || calculateLv.getChildAt(i).findViewById(R.id.caculate_lv_indexvalue) == null) continue;
            indexItem = calculateLst.get(i);
            indexValueLv = (ListView)calculateLv.getChildAt(i).findViewById(R.id.caculate_lv_indexvalue);
            if (indexValueLv == null) continue;
            for (int j = 0; j < indexItem.getIndexValueLst().size(); j++) {
                valueItem = indexItem.getIndexValueLst().get(j);
                if (valueItem == null) continue;
                // 判定显示方式及初始化显示数据 0:计算单选、 1:单选、 2:文本框、 3:数值、 4:下拉单选
                if (ConstValues.FLAG_1.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_rg_indexvalue) == null) continue;
                    resultRg = (RadioGroup)indexValueLv.getChildAt(j).findViewById(R.id.caculate_rg_indexvalue);
                    for (int k = 0; k < resultRg.getChildCount(); k++) {
                        resultRb = (RadioButton)resultRg.getChildAt(k);
                        if (resultRb.isChecked()) {
                            valueItem.setIndexValueId(resultRb.getHint().toString());
                            break;
                        }
                    }
                } else if (ConstValues.FLAG_2.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue) == null) continue;
                    resultEt = (EditText)indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue);
                    valueItem.setIndexValueId(resultEt.getText().toString());

                } else if (ConstValues.FLAG_3.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue_num) == null) continue;
                    resultEt = (EditText)indexValueLv.getChildAt(j).findViewById(R.id.caculate_et_indexvalue_num);
                    valueItem.setIndexValueId(resultEt.getText().toString());

                } else if (ConstValues.FLAG_4.equals(valueItem.getIndexType())|| ConstValues.FLAG_0.equals(valueItem.getIndexType())) {
                    if (indexValueLv.getChildAt(j).findViewById( R.id.caculate_bt_indexvalue) == null) continue;
                    valueItem.setIndexValueId(FunUtil.isBlankOrNullTo(indexValueLv.getChildAt(j).findViewById(R.id.caculate_bt_indexvalue).getTag(), "-1"));
                }
            }
        }

        // 遍历获取与产品无关指标采集数据部分
        CheckIndexCalculateStc itemStc;
        View itemV;
        // 根据页面上用户选的值  保存数据到表
        for(int i = 0; i < noProIndexLst.size(); i++) {
            itemStc = noProIndexLst.get(i);
            if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(itemStc.getIndexId())) {// 合作是否到位
                if (hezuoSw.getStatus()) {// 合作是否到位
                    itemStc.setIndexValueId("9019cf03-4572-4559-9971-48a27a611c3d");//合作是否到位 shi
                } else  {
                    itemStc.setIndexValueId("8d36d1e5-c776-452e-8893-589ad786d71d");		// 合作是否到位否															// 是
                }
            } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(itemStc.getIndexId())) {// 是否高质量配送
                if (peisongSw.getStatus()) {// 合作是否到位
                    itemStc.setIndexValueId("460647a9-283a-44ea-b11f-42efe1fd62e4");//是否高质量配送 shi
                } else  {
                    itemStc.setIndexValueId("bf600cfe-f70d-4170-857d-65dd59740d57");		// 是否高质量配送否															// 是
                }

            } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(itemStc.getIndexId())) {// 我品占有率
                kvItem = (KvStc)zhanyoulvSp.getSelectedItem();
                itemStc.setIndexValueId(kvItem.getKey());
            }
        }

        // 保存查指标页面的数据
        service.saveCheckIndex(visitId, termId, calculateLst, proItemLst, noProIndexLst);

        if(groupStatusSw.getStatus()){
            groupproTemp.setIfrecstand("Y");// 产品组合  未达标
        }else{
            groupproTemp.setIfrecstand("N");// 产品组合  达标
        }
        // 保存产品组合是否达标
        service.saveMstGroupproductM(groupproTemp);


        // 遍历活动状态的达成情况
        SlideSwitch statusSw;
        EditText reachNum;
        for (int i = 0; i < promotionLst.size(); i++) {
            itemV = promotionLv.getChildAt(i);
            if (itemV == null || itemV.findViewById(R.id.promotion_sw_status) == null) continue;
            statusSw = (SlideSwitch)itemV.findViewById(R.id.promotion_sw_status);
            reachNum = (EditText)itemV.findViewById(R.id.promotion_et_reachnum);
            if (statusSw.getStatus()) {
                promotionLst.get(i).setIsAccomplish(ConstValues.FLAG_1);//达成
            } else {
                promotionLst.get(i).setIsAccomplish(ConstValues.FLAG_0);//未达成

                // 根据是否有拍照,删除
                // 删除记录
                String pictypekey = promotionLst.get(i).getPromotKey();
                AndroidDatabaseConnection connection = null;
                try {
                    DatabaseHelper helper = DatabaseHelper.getHelper(getActivity());
                    MstCameraiInfoMDao dao = (MstCameraiInfoMDao) helper.getMstCameraiInfoMDao();
                    connection = new AndroidDatabaseConnection(helper.getWritableDatabase(), true);
                    connection.setAutoCommit(false);
                    // 删除该记录
                    dao.deletePicByCamerakeyATerminal(helper, pictypekey,termId);
                    connection.commit(null);

                } catch (Exception e) {
                    Log.e(TAG, "删除图片表记录失败", e);
                    try {
                        connection.rollback(null);
                    } catch (Exception e1) {
                        Log.e(TAG, "删除图片表记录失败", e1);
                    }
                }
                // 删除本地照片(以为每次确定上传都会删除文件夹,就不做操作了)
            }
            promotionLst.get(i).setReachNum(reachNum.getText().toString());

        }
        service.savePromotion(visitId, termId, promotionLst);
        long time4= new Date().getTime();
        Log.e("Optimization", "查指标执行数据库"+(time4-time3));

    }

    /**
     * 快速采集弹出窗口
     */
    public void qulicklyDialog() {
        DbtLog.logUtils(TAG, "qulicklyDialog()");
        // 如果弹出框已弹出，则不再弹出
        if (quicklyDialog != null && quicklyDialog.isShowing()) return;

        // 加载弹出窗口layout
        //itemForm = getContext().getLayoutInflater().inflate(R.layout.checkindex_quicklydialog,null);
        itemForm = LayoutInflater.from(getContext()).inflate(R.layout.checkindex_quicklydialog, null);

        quicklyDialog = new AlertDialog.Builder(getContext()).setCancelable(false).create();
        quicklyDialog.setView(itemForm, 0, 0, 0, 0);
        quicklyDialog.show();
        Window dialogWindow = quicklyDialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.height=600;
        dialogWindow.setAttributes(lp);
        // 获取快速采集的数据源
        quicklyDialogLv = (LinearLayout)itemForm.findViewById(R.id.quicklydialog_lv);
        quicklyDialogLv.removeAllViews();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                quicklyProItemLst = service.initQuicklyProItem(proItemLst);
                quicklyDialogLv.setOrientation(LinearLayout.VERTICAL);

                for(int i=0; i<quicklyProItemLst.size();i++){
                    View layout = inflater.inflate(R.layout.checkindex_quicklydialog_lvitem1,null);
                    TextView indexNameTv = (TextView)layout.findViewById(R.id.quicklydialog_tv_itemname);
                    ListView proItemLv = (ListView)layout.findViewById(R.id.quicklydialog_lv_pro);
                    QuicklyProItem item = quicklyProItemLst.get(i);
                    indexNameTv.setHint(item.getItemId());

                    indexNameTv.setText(item.getItemName());
                    proItemLv.setAdapter(new QuicklyDialogItemAdapter(getContext(), item.getProItemLst(),item.getItemId()));
                    ViewUtil.setListViewHeight(proItemLv);
                    quicklyDialogLv.addView(layout);
                }}
        }, 30);


        // 确定
        Button sureBt = (Button)itemForm.findViewById(R.id.quicklydialog_bt_sure);
        sureBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (ViewUtil.isDoubleClick(arg0.getId(), 2500)) return;
                QuicklyProItem itemI;
                ProItem itemJ;
                List<ProItem> itemJLst;
                ListView itemLv;
                EditText itemEt;
                EditText itemEt2;
                TextView itemTv;
                int isAllIn = 0;
                for (int i = 0; i < quicklyProItemLst.size(); i++) {
                    itemI = quicklyProItemLst.get(i);
                    itemJLst = itemI.getProItemLst();
                    itemLv =(ListView) quicklyDialogLv.getChildAt(i).findViewById(R.id.quicklydialog_lv_pro);
                    for (int j = 0; j < itemJLst.size(); j++) {
                        itemJ = itemJLst.get(j);

                        // 获取文本框的值
                        itemEt = (EditText)itemLv.getChildAt(j).findViewById(R.id.quicklydialog_et_changenum);
                        itemJ.setChangeNum((FunUtil.isBlankOrNullToDouble(itemEt.getText().toString())));
                        //itemJ.setChangeNum(Double.valueOf(FunUtil.isNullToZero(itemEt.getText().toString())));
                        itemJ.setBianhualiang(itemEt.getText().toString());// 现有量
                        itemEt2 = (EditText)itemLv.getChildAt(j).findViewById(R.id.quicklydialog_et_finalnum);
                        itemJ.setFinalNum((FunUtil.isBlankOrNullToDouble(itemEt2.getText().toString())));
                        //itemJ.setFinalNum(Double.valueOf(FunUtil.isNullToZero(itemEt2.getText().toString())));
                        itemJ.setXianyouliang(itemEt2.getText().toString());// 现有量
                        // 新鲜度
                        itemTv = (TextView)itemLv.getChildAt(j).findViewById(R.id.quicklydialog_et_xinxiandu);
                        itemJ.setFreshness(itemTv.getText().toString());

                        if("".equals(FunUtil.isNullSetSpace(itemEt.getText().toString()))||"".equals(FunUtil.isNullSetSpace(itemEt2.getText().toString()))){
                            isAllIn=1;
                        }
                    }
                }
                if (isAllIn == 1) {
                    Toast.makeText(getActivity(), "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                    return;
                }
                ViewUtil.hideSoftInputFromWindow(getActivity(),arg0);
                quicklyDialog.cancel();

                handler.sendEmptyMessage(ConstValues.WAIT5);
            }
        });

        // 取消
        Button cancelBt = (Button)itemForm.findViewById(R.id.quicklydialog_bt_cancel);
        cancelBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.hideSoftInputFromWindow(getActivity(),v);
                quicklyDialog.cancel();
            }
        });
    }

}
