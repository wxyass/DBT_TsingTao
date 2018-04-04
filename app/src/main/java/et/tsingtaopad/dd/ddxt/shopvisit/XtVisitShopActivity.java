package et.tsingtaopad.dd.ddxt.shopvisit;

import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.db.table.MstVisitM;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.dd.ddxt.camera.XtCameraFragment;
import et.tsingtaopad.dd.ddxt.chatvie.XtChatvieFragment;
import et.tsingtaopad.dd.ddxt.checking.XtCheckIndexFragment;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndex;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;
import et.tsingtaopad.dd.ddxt.invoicing.XtInvoicingFragment;
import et.tsingtaopad.dd.ddxt.sayhi.XtSayhiFragment;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.initconstvalues.domain.KvStc;
import et.tsingtaopad.main.visit.shopvisit.term.domain.MstTermListMStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProIndex;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.ProItem;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtVisitShopActivity extends BaseActivity implements View.OnClickListener,TabHost.OnTabChangeListener {

    private final String TAG = "XtVisitShopActivity";

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;


    private AppCompatTextView mDataTv;// 上次拜访时间: 2018-03-12
    private AppCompatTextView mDayTv;// 距今几天: 8
    //private AppCompatTextView mTextTv;
    private FrameLayout mContentFl;// 需要替换的Fragment : 打招呼,聊竞品...
    private AppCompatImageView mMemoImg;// 客情备忘录

    private PopupWindow mPopWindow;

    private String seeFlag;// 1:查看 ,0:拜访
    private String isFirstVisit;//  是否第一次拜访  0:第一次拜访   1:重复拜访
    private XtTermSelectMStc termStc;

    private int fragmentType;
    private XtShopVisitService xtShopVisitService;
    private String prevVisitId;// 获取上次拜访主键
    private String prevVisitDate; // 上次拜访日期

    List<XtProIndex> calculateLst = new ArrayList<XtProIndex>();
    List<KvStc> indexValuelst = new ArrayList<KvStc>();
    List<XtProItem> proItemLst = new ArrayList<XtProItem>();
    List<XtCheckIndexCalculateStc> noProIndexLst = new ArrayList<XtCheckIndexCalculateStc>();

    private RadioButton xtvisit_rb_sayhi;
    private RadioButton xtvisit_rb_invoicing;
    private RadioButton xtvisit_rb_checkindex;
    private RadioButton xtvisit_rb_chatvie;
    private RadioButton xtvisit_rb_camera;
    private RadioButton xtvisit_rb_other;

    private String visitId;
    private String channelId;

    private FragmentTabHost tabHost;
    private Class fragmentArray[] = { XtSayhiFragment.class,
            XtInvoicingFragment.class, XtCheckIndexFragment.class,
            XtChatvieFragment.class ,XtCameraFragment.class};

    private int imageViewArray[] = { R.drawable.bt_shopvisit_sayhi,
            R.drawable.bt_shopvisit_invoicing,
            R.drawable.bt_shopvisit_checkindex, R.drawable.bt_shopvisit_chatvie,
            R.drawable.bt_shopvisit_camera };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtvisitshop);
        initView();
        initData();
    }

    // 初始化控件
    private void initView() {
        backBtn = (RelativeLayout) findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);
        //titleTv.setOnClickListener(this);


        mDataTv = (AppCompatTextView) findViewById(R.id.xtvisit_tv_date);
        mDayTv = (AppCompatTextView) findViewById(R.id.xtvisit_tv_day);
        //mTextTv = (AppCompatTextView) findViewById(R.id.xtvisit_tv_textView2);
        mContentFl = (FrameLayout) findViewById(R.id.xtvisit_fl_content);
        mMemoImg = (AppCompatImageView) findViewById(R.id.xtvisit_bt_memo);

        xtvisit_rb_sayhi = (RadioButton) findViewById(R.id.xtvisit_rb_sayhi);
        xtvisit_rb_invoicing = (RadioButton) findViewById(R.id.xtvisit_rb_invoicing);
        xtvisit_rb_checkindex = (RadioButton) findViewById(R.id.xtvisit_rb_checkindex);
        xtvisit_rb_chatvie = (RadioButton) findViewById(R.id.xtvisit_rb_chatvie);
        xtvisit_rb_camera = (RadioButton) findViewById(R.id.xtvisit_rb_camera);
        xtvisit_rb_other = (RadioButton) findViewById(R.id.xtvisit_rb_other);
        xtvisit_rb_sayhi.setOnClickListener(this);
        xtvisit_rb_invoicing.setOnClickListener(this);
        xtvisit_rb_checkindex.setOnClickListener(this);
        xtvisit_rb_chatvie.setOnClickListener(this);
        xtvisit_rb_camera.setOnClickListener(this);
        xtvisit_rb_other.setOnClickListener(this);


    }

    // 初始化数据
    private void initData() {

        // 获取参数“终端信息”
        Bundle bundle = getIntent().getExtras();
        seeFlag = bundle.getString("seeFlag");
        isFirstVisit = bundle.getString("isFirstVisit");
        termStc = (XtTermSelectMStc) bundle.getSerializable("termStc");

        titleTv.setText(termStc.getTerminalname());

        xtShopVisitService = new XtShopVisitService(getApplicationContext(), null);
        // 从拜访主表中获取最后一次拜访数据
        MstVisitM preMstVisitM = xtShopVisitService.findNewLastVisit(termStc.getTerminalkey(), false);
        // 获取上次拜访主键
        prevVisitId = preMstVisitM.getVisitkey();
        prevVisitDate = preMstVisitM.getVisitdate();
        // 复制各个临时表
        configVisitData(bundle);

        // 展示打招呼页面
        /*XtSayhiFragment xtSayhiFragment = new XtSayhiFragment();
        xtSayhiFragment.setArguments(returnBundle());
        changeFragment(xtSayhiFragment, "xtsayhifragment");*/
         /*xtCameraFragmentf = new XtCameraFragment();
        xtCameraFragmentf.setArguments(returnBundle());
        changeFragment(xtCameraFragmentf, "xtcamerafragment");
        fragmentType = 5;*/

        initBandleDate(returnBundle());
    }

    // 初始化顶部TabHost
    private void initBandleDate(Bundle bundle) {
        // 初始化TabHost
        tabHost = (FragmentTabHost) findViewById(R.id.tabhost);

        tabHost.setup(XtVisitShopActivity.this, getSupportFragmentManager(), R.id.xtvisit_fl_content);
        tabHost.getTabWidget().setDividerDrawable(null);
        for (int i = 0; i < fragmentArray.length; i++) {
            TabHost.TabSpec tabSpec = tabHost.newTabSpec(String.valueOf(i)).setIndicator(getTabItemView(i));
            tabHost.addTab(tabSpec, fragmentArray[i], bundle);
        }
        tabHost.setOnTabChangedListener(this);// 用来监听 打招呼、进销存、差指标、聊竞品 页面的操作事件
    }

    // 初始化顶部TabHost图片
    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.platform_tab_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(imageViewArray[index]);
        //imageView.setImageResource(R.drawable.bg_select_green);
        return view;
    }


    // 配置 拜访时 所需要的各个临时表数据
    private void configVisitData(Bundle bundle) {

        // 先删除
        xtShopVisitService.deleteData();

        // 再复制
        visitId = xtShopVisitService.toCopyData(termStc);
        channelId = termStc.getMinorchannel();

        // 保存初始数据(上面虽然复制了数据库,但有些表是没有复制的,在这里把一些数据)
        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 (比如:铺货状态,道具生动化,产品生动化,冰冻化 及各个对应的产品)
        // mst_vistproduct_info_temp左关联mst_checkexerecord_info_temp
        // 注意:是从拉链表临时表中取值的 (但当天第一次拜访 拉链表的临时表都为null(因为toCopyData没复制),所以没值)(若是查看从拉链表正表取值)
        calculateLst = xtShopVisitService.queryCalculateIndex(visitId, termStc.getTerminalkey(), channelId, seeFlag);
        // (查出所有采集项数据)
        proItemLst = xtShopVisitService.queryCalculateItem(visitId, channelId);
        // 获取与产品无关的指标, 从临时表中读取
        noProIndexLst = xtShopVisitService.queryNoProIndex12(visitId, channelId, seeFlag);
        // 保存查指标页面的数据
        xtShopVisitService.saveCheckIndex(visitId, termStc.getTerminalkey(), calculateLst, proItemLst, noProIndexLst);

        String sd = "sd";
        // 获取分项采集页面显示数据 // 获取指标采集前3列数据 // 铺货状态,道具生动化,产品生动化,冰冻化
        //calculateLst = xtShopVisitService.queryCalculateIndex("da85a369f6344b46a8f4579aba5e095a", "1-AW46W7","39DD41A3991E8C68E05010ACE0016FCD", "");
        // 获取分项采集部分的产品指标对应的采集项目数据 // 因为在ShopVisitActivity生成了供货关系,此时就能关联出各个产品的采集项,现有量变化量为0
        //proItemLst = service.queryCalculateItem("da85a369f6344b46a8f4579aba5e095a", "39DD41A3991E8C68E05010ACE0016FCD");

    }


    // 按钮点击 监听
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.top_navigation_rl_back:// 返回
                this.finish();
                break;
            case R.id.top_navigation_rl_confirm://

                break;


            case R.id.xtvisit_rb_sayhi:
                if (fragmentType == 1) {
                    return;
                } else {
                    fragmentType = 1;
                    XtSayhiFragment xtSayhiFragment = new XtSayhiFragment();
                    xtSayhiFragment.setArguments(returnBundle());
                    changeFragment(xtSayhiFragment, "xtsayhifragment");
                }
                break;
            case R.id.xtvisit_rb_invoicing:
                if (fragmentType == 2) {
                    return;
                } else {
                    fragmentType = 2;
                    XtInvoicingFragment xtInvoicingFragment = new XtInvoicingFragment();
                    xtInvoicingFragment.setArguments(returnBundle());
                    changeFragment(xtInvoicingFragment, "xtinvoicingfragment");
                }
                break;
            case R.id.xtvisit_rb_checkindex:
                if (fragmentType == 3) {
                    return;
                } else {
                    fragmentType = 3;
                    XtCheckIndexFragment xtCheckIndexFragment = new XtCheckIndexFragment();
                    xtCheckIndexFragment.setArguments(returnBundle());
                    changeFragment(xtCheckIndexFragment, "xtcheckindexfragment");
                }
                break;
            case R.id.xtvisit_rb_chatvie:
                if (fragmentType == 4) {
                    return;
                } else {
                    fragmentType = 4;
                    XtChatvieFragment xtChatvieFragment = new XtChatvieFragment();
                    xtChatvieFragment.setArguments(returnBundle());
                    changeFragment(xtChatvieFragment, "xtchatviefragment");
                }
                break;
            case R.id.xtvisit_rb_camera:
                if (fragmentType == 5) {
                    return;
                } else {
                    if(GlobalValues.isSayHiSure){
                        fragmentType = 5;
                        XtCameraFragment xtCameraFragment = new XtCameraFragment();
                        xtCameraFragment.setArguments(returnBundle());
                        changeFragment(xtCameraFragment, "xtcamerafragment");
                    }
                }
                break;
            default:
                break;
        }
    }

    // 替换XtVisitShopActivity中的Fragment布局
    public void changeFragment(BaseFragmentSupport fragment, String tag) {
        // 获取Fragment管理者
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 开启事物
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        // 当前要添加的Fragment需要放的容器位置
        // 要替换的fragment
        // 标记
        beginTransaction.replace(R.id.xtvisit_fl_content, fragment, tag);
        // 提交事物
        beginTransaction.commit();
    }

    // 替换整个XtVisitShopActivity布局
    public void changeXtvisitFragment(BaseFragmentSupport fragment, String tag) {
        // 获取Fragment管理者
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        // 开启事物
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        // 当前要添加的Fragment需要放的容器位置
        // 要替换的fragment
        // 标记
        beginTransaction.replace(R.id.xtvisit_container, fragment, tag);
        beginTransaction.addToBackStack(null);
        // 提交事物
        beginTransaction.commit();
    }

    // 组建打招呼、进销存、聊竞品、查指标页面所需参数
    private Bundle returnBundle() {

        // 组建打招呼、进销存、聊竞品、查指标页面所需参数
        Bundle bundle = new Bundle();
        bundle.putSerializable("termId", termStc.getTerminalkey());
        bundle.putSerializable("termname", termStc.getTerminalname());
        bundle.putSerializable("channelId", termStc.getMinorchannel());// 次渠道
        bundle.putSerializable("termStc", termStc);
        bundle.putSerializable("visitKey", visitId);//visitId
        bundle.putSerializable("seeFlag", seeFlag);// 默认0   0:拜访 1:查看

        bundle.putSerializable("visitDate", "");// visitDate上一次的拜访时间(用于促销活动 状态隔天关闭)
        bundle.putSerializable("lastTime", "");// lastTime上一次的拜访时间(用于促销活动 状态隔天关闭)
        return bundle;
    }

    @Override
    public void onTabChanged(String tabId) {

        if (tabId.equals("0")) {
            DbtLog.logUtils(TAG, "XtSayHiFragment:打招呼");
        } else if (tabId.equals("1")) {
            DbtLog.logUtils(TAG, "XtInvoicingFragment:进销存");
        } else if (tabId.equals("2")) {
            DbtLog.logUtils(TAG, "XtCheckIndexFragment:查指标");
        } else if (tabId.equals("3")) {
            DbtLog.logUtils(TAG, "XtChatVieFragment:聊竞品");
        }else if (tabId.equals("4")) {
            DbtLog.logUtils(TAG, "XtCameraFragment:拍照");
        }

        // 如果是查看操作，则不做数据校验
        if (ConstValues.FLAG_1.equals(seeFlag))
            return;

        BaseActivity view = (XtVisitShopActivity) tabHost.getCurrentView().getContext();
        EditText termNameTv = (EditText) view.findViewById(R.id.xtbf_sayhi_termname);
        if (termNameTv != null) {
            TextView belongLineSp = (TextView) view.findViewById(R.id.xtbf_sayhi_termroude);
            TextView levelSp = (TextView) view.findViewById(R.id.xtbf_sayhi_termlv);

            EditText addressEt = (EditText) view .findViewById(R.id.xtbf_sayhi_termaddress);
            EditText linkmanEt = (EditText) view.findViewById(R.id.xtbf_sayhi_termcontact);
            EditText telEt = (EditText) view.findViewById(R.id.xtbf_sayhi_termphone);
            EditText sequenceEt = (EditText) view .findViewById(R.id.xtbf_sayhi_termsequence);
            TextView sellChannelSp = (TextView) view.findViewById(R.id.xtbf_sayhi_termsellchannel);
            TextView mainChannelSp = (TextView) view.findViewById(R.id.xtbf_sayhi_termtmainchannel);
            TextView minorChannelSp = (TextView) view.findViewById(R.id.xtbf_sayhi_termminorchannel);

            int msgId = -1;
            if ("".equals(FunUtil.isNullSetSpace(termNameTv.getText()).toString())) {
                //termNameTv.requestFocus();
                msgId = R.string.termadd_msg_invaltermname;

            } else if ("".equals(FunUtil.isNullSetSpace(addressEt.getText()).toString())) {
                //addressEt.requestFocus();
                msgId = R.string.termadd_msg_invaladdress;

            } else if ("".equals(FunUtil.isNullSetSpace(linkmanEt.getText()).toString())) {
                //linkmanEt.requestFocus();
                msgId = R.string.termadd_msg_invalcontact;
            }
            if ("-1".equals(FunUtil.isBlankOrNullTo(belongLineSp.getText(), "-1"))) {
                msgId = R.string.termadd_msg_invalbelogline;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(levelSp.getText(),"-1"))) {
                msgId = R.string.termadd_msg_invaltermlevel;

            }  else if ("-1".equals(FunUtil.isBlankOrNullTo(sellChannelSp.getText(), "-1"))) {
                msgId = R.string.termadd_msg_invalsellchannel;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(mainChannelSp.getText(), "-1"))) {
                msgId = R.string.termadd_msg_invalmainchannel;

            } else if ("-1".equals(FunUtil.isBlankOrNullTo(minorChannelSp.getText(), "-1"))) {
                msgId = R.string.termadd_msg_invalminorchannel;
            }

            if (msgId != -1) {
                tabHost.setCurrentTab(0);
                //ViewUtil.sendMsg(getApplicationContext(), msgId);

            }
            // 结束按钮提示信息
            //sureBt.setTag(msgId);
        }


    }
}
