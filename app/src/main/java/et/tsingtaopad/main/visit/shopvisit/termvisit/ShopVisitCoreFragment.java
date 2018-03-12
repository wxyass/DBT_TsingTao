package et.tsingtaopad.main.visit.shopvisit.termvisit;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.FileUtils;

import java.io.File;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.core.base.BaseCoreFragment;
import et.tsingtaopad.core.util.dbtutil.DateUtil;
import et.tsingtaopad.core.util.dbtutil.FileUtil;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.util.file.FileTool;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.CameraCoreFragment;
import et.tsingtaopad.main.visit.shopvisit.termvisit.camera.domain.CameraInfoStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.chatvie.ChatVieCoreFragment;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.CheckIndexCoreFragment;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;
import et.tsingtaopad.main.visit.shopvisit.termvisit.invoicing.InvoicingCoreFragment;
import et.tsingtaopad.main.visit.shopvisit.termvisit.sayhi.SayHiCoreFragment;

/**
 * Created by yangwenmin on 2017/12/25.
 * 终端拜访首页
 */

public class ShopVisitCoreFragment extends BaseCoreFragment implements View.OnClickListener {

    public static final String TAG = "ShopVisitCoreFragment";
    private TextView titleTv;
    private RelativeLayout backRl;
    private RelativeLayout confirmRl;

    private String seeFlag;// 1:查看 ,0:拜访
    private String isFirstVisit;//  是否第一次拜访  0:第一次拜访   1:重复拜访
    private MstTermListMStc termStc;
    private String visitDate;
    private String lastTime;
    private String visitDay;


    // 5个子页面
    public static final int FIRST = 0;
    public static final int SECOND = 1;
    public static final int THIRD = 2;
    public static final int FOUR = 3;
    public static final int FIVE = 4;
    private BaseCoreFragment[] mFragments = new BaseCoreFragment[5];

    // 顶部按钮容器
    private LinearLayout topBarLl;
    // 默认展示的条目位置
    private int mIndexDelegate = 0;
    // 当前正展示的条目的位置
    private int mCurrentDelegate = 0;
    // 顶部按钮资源
    private int imageViewArray[] = {R.drawable.bt_shopvisit_sayhi,
            R.drawable.bt_shopvisit_invoicing,
            R.drawable.bt_shopvisit_checkindex,
            R.drawable.bt_shopvisit_chatvie,
            R.drawable.bt_shopvisit_camera};

    // 退出弹窗
    private AlertDialog backDialog;
    // 上传弹窗
    private AlertDialog configDialog;

    private TextView visitDateTv;
    private TextView visitDayTv;
    private ImageView visitMemoIv;

    private String visitId;

    private ShopVisitService shopVisitService;

    private String channelId;// 该终端的 渠道

    private String prevVisitId;// 获取上次拜访主键
    private String prevVisitDate; // 上次拜访日期

    public ShopVisitCoreFragment() {
    }

    public static ShopVisitCoreFragment newInstance(Bundle bundle) {
        DbtLog.logUtils(TAG, "newInstance(Bundle bundle)");
        ShopVisitCoreFragment fragment = new ShopVisitCoreFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    // 在这个方法中 只处理从上个Fragment传递过来的数据,若没数据传递过来,可删除该方法
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DbtLog.logUtils(TAG, "onCreate");
        Bundle bundle = getArguments();
        if (bundle != null) {
            seeFlag = bundle.getString("seeFlag");// 0:拜访   1:查看
            isFirstVisit = bundle.getString("isFirstVisit");//  是否第一次拜访  0:第一次拜访   1:重复拜访
            termStc = (MstTermListMStc) bundle.getSerializable("termStc");// 终端信息
            visitDate = bundle.getString("visitDate");// 上次拜访时间,点击上传的
            visitDay = bundle.getString("visitDay");// 距今几天
            lastTime = bundle.getString("lastTime");// 上次拜访时间, 不管点没点上传 (可能进去返回了)
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        DbtLog.logUtils(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.visit_shopvisit_main, null);
        this.initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        DbtLog.logUtils(TAG, "initView");
        titleTv = (TextView) view.findViewById(R.id.banner_navigation_tv_title);
        backRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_back);
        confirmRl = (RelativeLayout) view.findViewById(R.id.banner_navigation_rl_confirm);

        visitDateTv = (TextView) view.findViewById(R.id.shopvisit_tv_visitterm_date);
        visitDayTv = (TextView) view.findViewById(R.id.shopvisit_tv_visitterm_day);
        visitMemoIv = (ImageView) view.findViewById(R.id.shopvisit_bt_memo);

        topBarLl = (LinearLayout) view.findViewById(R.id.visit_shopvisit_ll_top_bar);

        // 顶部加载5个按钮
        for (int i = 0; i < 5; i++) {
            // 填充每个按钮的布局 父容器
            LayoutInflater.from(getContext()).inflate(R.layout.visit_shopvisit_topitem_iv, topBarLl);
            final RelativeLayout item = (RelativeLayout) topBarLl.getChildAt(i);
            // 设置每个item的点击事件
            item.setTag(i);
            item.setOnClickListener(this);
            // 初始化控件
            final AppCompatImageView iconTextView = (AppCompatImageView) item.getChildAt(0);
            // 初始化控件数据
            iconTextView.setImageResource(imageViewArray[i]);
            // 将默认展示的条目,更改颜色
            if (i == mIndexDelegate) {
                iconTextView.setSelected(true);
            }
        }

        backRl.setOnClickListener(this);
        confirmRl.setOnClickListener(this);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DbtLog.logUtils(TAG, "onActivityCreated");

    }

    @Override
    public void onLazyInitView(@Nullable Bundle savedInstanceState) {
        super.onLazyInitView(savedInstanceState);
        DbtLog.logUtils(TAG, "onLazyInitView");


        initData();
        visitId = this.copyDatebase();
        initFragment();
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        DbtLog.logUtils(TAG, "onSupportVisible");
        // 每次页面出现后都重新加载数据


    }

    // 初始化数据
    private void initData() {
        DbtLog.logUtils(TAG, "initData");
        confirmRl.setVisibility(View.VISIBLE);
        titleTv.setText(termStc.getTerminalname());
        visitDateTv.setText(FunUtil.isBlankOrNullTo(visitDate, ""));
        visitDayTv.setText(FunUtil.isBlankOrNullTo(visitDay, "0"));

        //
        shopVisitService = new ShopVisitService(getContext(), null);
        // 从拜访主表中获取最后一次拜访数据
        MstVisitM preMstVisitM = shopVisitService.findNewVisit(termStc.getTerminalkey(), false);
        // 获取上次拜访主键
        prevVisitId = preMstVisitM.getVisitkey();
        prevVisitDate = preMstVisitM.getVisitdate();
    }

    // 复制临时表
    private String copyDatebase(){
        // 终端表 拜访表 我品供货关系表 拜访产品表 竞品供货关系表
        // 指标拉链表() 指标采集项 促销活动表 图片表 客情表 组合表

        // 删除临时表数据
        //shopVisitService.deleteData();

        // 复制临时表  本次拜访表的主键  // 注意 mst_collectionexerecord_info_temp 必须要复制mst_collectionexerecord_info_temp(当天第二次拜访的数据)
        visitId = shopVisitService.toCopyData(termStc);

        MstTerminalinfoM term = shopVisitService.findTermById(termStc.getTerminalkey());
        channelId = term.getMinorchannel();


        // 保存初始数据(上面虽然复制了数据库,但有些表是没有复制的,在这里把一些数据)
        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 (比如:铺货状态,道具生动化,产品生动化,冰冻化 及各个对应的产品)
        // mst_vistproduct_info_temp左关联mst_checkexerecord_info_temp
        // 注意:是从拉链表临时表中取值的 (但当天第一次拜访 拉链表的临时表都为null(因为toCopyData没复制),所以没值)(若是查看从拉链表正表取值)
        List<ProIndex> calculateLst  = shopVisitService.queryCalculateIndex(visitId, termStc.getTerminalkey(), channelId, seeFlag);
        // (查出所有采集项数据)
        List<ProItem> proItemLst = shopVisitService.queryCalculateItem(visitId, channelId);
        // 获取与产品无关的指标, 从临时表中读取
        List<CheckIndexCalculateStc> noProIndexLst=shopVisitService.queryNoProIndex2(visitId, channelId, seeFlag);
        // 保存查指标页面的数据
        shopVisitService.saveCheckIndex(visitId, termStc.getTerminalkey(), calculateLst, proItemLst, noProIndexLst);

        return visitId;
    }

    // 添加子Fragment
    private void initFragment() {
        BaseCoreFragment firstFragment = findChildFragment(SayHiCoreFragment.class);
        if (firstFragment == null) {
            Bundle bundle = new Bundle();

            bundle.putString("termId", termStc.getTerminalkey());
            bundle.putString("visitKey", visitId);
            bundle.putString("seeFlag", seeFlag);// 0:拜访   1:查看
            bundle.putString("isFirstVisit", isFirstVisit);//  是否第一次拜访  0:第一次拜访   1:重复拜访
            bundle.putString("termname", termStc.getTerminalname());
            bundle.putString("visitDate", visitDate);// 上一次的拜访时间(用于促销活动 状态隔天关闭)
            bundle.putString("lastTime", lastTime);// // 上次拜访时间, 不管点没点上传 (可能进去返回了) 上一次的拜访时间(用于促销活动 状态隔天关闭)

            mFragments[FIRST] = SayHiCoreFragment.newInstance(bundle);
            mFragments[SECOND] = InvoicingCoreFragment.newInstance(bundle);
            mFragments[THIRD] = CheckIndexCoreFragment.newInstance(bundle);
            mFragments[FOUR] = ChatVieCoreFragment.newInstance(bundle);
            mFragments[FIVE] = CameraCoreFragment.newInstance(bundle);

            getSupportDelegate().loadMultipleRootFragment(R.id.shopvisit_fl_container, FIRST,
                    mFragments[FIRST],
                    mFragments[SECOND],
                    mFragments[THIRD],
                    mFragments[FOUR],
                    mFragments[FIVE]);
        } else {
            // 这里库已经做了Fragment恢复,所有不需要额外的处理了, 不会出现重叠问题

            // 这里我们需要拿到mFragments的引用
            mFragments[FIRST] = firstFragment;
            mFragments[SECOND] = findChildFragment(InvoicingCoreFragment.class);
            mFragments[THIRD] = findChildFragment(CheckIndexCoreFragment.class);
            mFragments[FOUR] = findChildFragment(ChatVieCoreFragment.class);
            mFragments[FIVE] = findChildFragment(CameraCoreFragment.class);
        }
    }

    @Override
    public void onClick(View view) {
        DbtLog.logUtils(TAG, "onClick");
        int i = view.getId();
        // 返回
        if (i == R.id.banner_navigation_rl_back) {
            // 下面2行是为了保存临时数据 不能删
            getSupportDelegate().showHideFragment(mFragments[FIVE]);
            getSupportDelegate().showHideFragment(mFragments[mCurrentDelegate]);
            // 退出
            confirmBack();

        // 确定
        } else if (i == R.id.banner_navigation_rl_confirm) {
            // 下面2行是为了保存临时数据 不能删
            getSupportDelegate().showHideFragment(mFragments[FIVE]);
            getSupportDelegate().showHideFragment(mFragments[mCurrentDelegate]);

            if(checkTakeCamera()){// 拍照图片已成功保存到本地
                confirmUplad();
            }else if(!checkTakeCamera()){// 未拍照
                Toast.makeText(getContext(), "拍照任务未完成,不能上传", Toast.LENGTH_SHORT).show();
            }

            //getSupportDelegate().start(TermListCoreFragment.newInstance());
        } else { // 切换标签tag
            // 获取当前点击的按钮的tag
            final int tag = (int) view.getTag();
            // 重置所有按钮颜色置成灰色
            resetColor();
            // 获取当前点击的控件组合
            RelativeLayout item = (RelativeLayout) view;
            // 修改控件组合的颜色
            final AppCompatImageView iconTextView = (AppCompatImageView) item.getChildAt(0);
            iconTextView.setSelected(true);
            // 切换成按钮对应的Fragment  // 第一个参数是要展示的Fragment,第二个参数是要隐藏的Fragment
            getSupportDelegate().showHideFragment(mFragments[tag], mFragments[mCurrentDelegate]);
            // 将当前tag赋给mCurrentDelegate
            mCurrentDelegate = tag;
        }
    }

    // 将底部按钮全部置成灰色
    private void resetColor() {
        DbtLog.logUtils(TAG, "resetColor");
        final int count = topBarLl.getChildCount();
        for (int i = 0; i < count; i++) {
            // 获取当前位置孩子
            final RelativeLayout item = (RelativeLayout) topBarLl.getChildAt(i);
            // 获取这个孩子的控件组合,并修改颜色
            final AppCompatImageView iconTextView = (AppCompatImageView) item.getChildAt(0);
            iconTextView.setSelected(false);
        }
    }

    /**
     * 确定返回上一界面
     */
    private void confirmBack() {
        DbtLog.logUtils(TAG, "confirmBack");
        // 如果不是查看操作，返回需再次确定
        View view = LayoutInflater.from(getContext()).inflate(R.layout.visit_shopvisit_back_dialog, null);
        TextView title = (TextView) view.findViewById(R.id.agencyvisit_tv_over_title);
        TextView msg = (TextView) view.findViewById(R.id.agencyvisit_tv_over_msg);
        ImageView sure = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_sure);
        ImageView cancle = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_quxiao);
        title.setText(R.string.dialog_back);
        msg.setText(R.string.dialog_msg_backdesc);
        backDialog = new AlertDialog.Builder(getContext()).setCancelable(false).create();
        backDialog.setView(view, 0, 0, 0, 0);
        // 确定返回
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backDialog.dismiss();
                // 删除临时表数据
                shopVisitService.deleteData();
                // 删除DCIM/Camera文件夹下的所有照片
                FileUtil.deleteAllFiles(new File(FileTool.CAMERA_PHOTO_DIR));
                // 重新扫描磁盘
                scanFile();

                pop();
            }
        });
        // 取消返回
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backDialog.dismiss();
            }
        });
        backDialog.show();
    }

    /**
     * 确定是否要上传数据
     */
    private void confirmUplad() {
        super.onPause();
        DbtLog.logUtils(TAG, "结束拜访");

            if (configDialog != null && configDialog.isShowing()) return;
            View view = LayoutInflater.from(getContext()).inflate(R.layout.agencyvisit_total_overvisit_dialog, null);
            TextView title = (TextView) view.findViewById(R.id.agencyvisit_tv_over_title);
            TextView msg = (TextView) view.findViewById(R.id.agencyvisit_tv_over_msg);
            ImageView sure = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_sure);
            ImageView cancle = (ImageView) view.findViewById(R.id.agencyvisit_bt_over_quxiao);
            title.setText(R.string.dialog_title);
            msg.setText(R.string.dialog_msg_overshopvisit);
            configDialog = new AlertDialog.Builder(getContext()).setCancelable(false).create();
            configDialog.setView(view, 0, 0, 0, 0);
            sure.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (ViewUtil.isDoubleClick(v.getId(), 2500))
                        return;
                    DbtLog.logUtils(TAG, "结束拜访：是");

                    // 检测现有量变化量是否为空  true:全不为空 可以上传   false:有为空的 不可上传
                    if(!checkCollectionexrecord()){// 未填写现有量变化量
                        Toast.makeText(getContext(), "所有的现有量,变化量必须填值(没货填0)", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // 更新GPS坐标  更新到拜访临时表
                    //service.updateGps(visitId, longitude, latitude, "");

                    // 复制正表
                    //1  "MST_VISIT_M_TEMP"   原样复制create,并把enddate附上值   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传 有visitkey
                    //3  "MST_TERMINALINFO_M_TEMP"   原样复制createor   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传
                    //4  "MST_AGENCYSUPPLY_INFO_TEMP"  原样复制createor  注意padisconsistent字段的取值
                    //2  "MST_VISTPRODUCT_INFO_TEMP" 原样复制create   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传  有visitkey
                    //4  "MST_CMPSUPPLY_INFO_TEMP"   原样复制createor   注意padisconsistent字段的取值0
                    //1  "MST_CHECKEXERECORD_INFO_TEMP  视情况复制
                    //1  "MST_COLLECTIONEXERECORD_INFO_TEMP"   原样复制create   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传  有visitkey
                    //2  "MST_PROMOTERM_INFO_TEMP"   原样复制create   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传  有visitkey
                    //2  "MST_CAMERAINFO_M_TEMP"  原样复制create   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传  有visitkey
                    //  "MST_VISITMEMO_INFO_TEMP"
                    //5  "MST_GROUPPRODUCT_M_TEMP"  原样复制createor   是否已上传用padisconsistent字段控制  0:还未上传  1:已上传
                    //  "MST_CHECKGROUP_INFO"
                    //  "MST_CHECKGROUP_INFO_TEMP"

                    String visitEndDate = DateUtil.formatDate(new Date(), "yyyyMMddHHmmss");
                    // 开始复制 更新拜访离店时间及是否要上传标志 以及对去除拜访指标采集项重复(collectionexerecord表)
                    shopVisitService.confirmUpload(visitId, termStc.getTerminalkey(), visitEndDate, "1");


                    // 删除所有临时表数据
                    //shopVisitService.deleteData();

                    configDialog.dismiss();
                    pop();
                }
            });
            cancle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DbtLog.logUtils(TAG, "结束拜访：否");

                    configDialog.dismiss();
                }
            });
        configDialog.show();
    }

    // 重新扫描磁盘
    private  void scanFile(){
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE); // MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        String path = Environment.getExternalStorageDirectory() + "";
        final File tempFile = new File(path);
        Uri fileUri = null;
        // // 兼容7.0及以上的写法
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            final ContentValues contentValues = new ContentValues(1);// ?
            contentValues.put(MediaStore.Images.Media.DATA, tempFile.getPath());//?
            final Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            // 需要将Uri路径转化为实际路径?
            final File realFile = FileUtils.getFileByPath(FileTool.getRealFilePath(getContext(), uri));
            // 将File转为Uri
            fileUri = Uri.fromFile(realFile);
        }else{
            fileUri = Uri.fromFile(tempFile);// 将File转为Uri
        }
        intent.setData(fileUri);
        getContext().sendBroadcast(intent);
    }

    /**
     * 检测该终端本次拜访是否已拍照
     *
     * @return true:已拍照  false:未拍照
     */
    private boolean checkTakeCamera() {
        // 查询图片类型表,获取需要拍几张图片
        List<CameraInfoStc> cameraLst = shopVisitService.queryPictypeMAll();
        // 获取当天已保存的图片
        List<CameraInfoStc> lst  = shopVisitService.queryCurrentPicRecord(termStc.getTerminalkey(), visitId);
        if(cameraLst.size()>0 && lst.size() <= 0){
            return false;
        }else{
            return true;
        }
    }

    // 检测现有量变化量是否为空  true:全不为空 可以上传   false:有为空的 不可上传
    private boolean checkCollectionexrecord(){

        boolean isallIn = true;
        // (查出所有采集项)
        List<ProItem> proItemLst = shopVisitService.queryCalculateItem(visitId, channelId);
        for (ProItem proitem : proItemLst) {
            if("".equals(FunUtil.isNullSetSpace(proitem.getBianhualiang()))||"".equals(FunUtil.isNullSetSpace(proitem.getXianyouliang()))){
                isallIn = false;// 为空不能上传
                break;
            }
        }
        return isallIn;
    }

    // 监控返回键
    @Override
    public boolean onBackPressedSupport() {

        // 默认flase，继续向上传递
        //return super.onBackPressedSupport();

        // 设置横屏
        getSupportDelegate().getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 下面2行是为了保存临时数据 不能删
        getSupportDelegate().showHideFragment(mFragments[FIVE]);
        getSupportDelegate().showHideFragment(mFragments[mCurrentDelegate]);
        // 退出
        confirmBack();

        return true;// 消耗这个返回事件
    }
}
