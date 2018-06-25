package et.tsingtaopad.business.visit;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.business.visit.bean.AreaGridRoute;
import et.tsingtaopad.business.visit.bean.VisitContentStc;
import et.tsingtaopad.core.net.HttpUrl;
import et.tsingtaopad.core.net.RestClient;
import et.tsingtaopad.core.net.callback.IError;
import et.tsingtaopad.core.net.callback.IFailure;
import et.tsingtaopad.core.net.callback.ISuccess;
import et.tsingtaopad.core.net.domain.RequestHeadStc;
import et.tsingtaopad.core.net.domain.RequestStructBean;
import et.tsingtaopad.core.net.domain.ResponseStructBean;
import et.tsingtaopad.core.util.dbtutil.ConstValues;
import et.tsingtaopad.core.util.dbtutil.JsonUtil;
import et.tsingtaopad.core.util.dbtutil.NetStatusUtil;
import et.tsingtaopad.core.util.dbtutil.PrefUtils;
import et.tsingtaopad.core.util.dbtutil.PropertiesUtil;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnDismissListener;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.db.table.CmmAreaM;
import et.tsingtaopad.db.table.CmmDatadicM;
import et.tsingtaopad.db.table.MitValcheckterM;
import et.tsingtaopad.db.table.MstAgencyKFM;
import et.tsingtaopad.db.table.MstAgencyinfoM;
import et.tsingtaopad.db.table.MstAgencyvisitM;
import et.tsingtaopad.db.table.MstCmpbrandsM;
import et.tsingtaopad.db.table.MstCmpcompanyM;
import et.tsingtaopad.db.table.MstCmproductinfoM;
import et.tsingtaopad.db.table.MstGridM;
import et.tsingtaopad.db.table.MstInvoicingInfo;
import et.tsingtaopad.db.table.MstMarketareaM;
import et.tsingtaopad.db.table.MstPictypeM;
import et.tsingtaopad.db.table.MstProductM;
import et.tsingtaopad.db.table.MstPromoproductInfo;
import et.tsingtaopad.db.table.MstPromotionsM;
import et.tsingtaopad.db.table.MstRouteM;
import et.tsingtaopad.db.table.MstTerminalinfoMCart;
import et.tsingtaopad.db.table.MstTerminalinfoMZsCart;
import et.tsingtaopad.db.table.MstVisitauthorizeInfo;
import et.tsingtaopad.db.table.PadCheckstatusInfo;
import et.tsingtaopad.dd.ddaddterm.DdAddTermFragment;
import et.tsingtaopad.dd.ddagencycheck.DdAgencyCheckSelectFragment;
import et.tsingtaopad.dd.ddagencyres.DdAgencySelectFragment;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;
import et.tsingtaopad.dd.ddxt.term.select.XtTermSelectFragment;
import et.tsingtaopad.dd.ddzs.zsterm.zscart.ZsTermCartFragment;
import et.tsingtaopad.dd.ddzs.zsterm.zsselect.ZsTermSelectFragment;
import et.tsingtaopad.home.app.MainService;
import et.tsingtaopad.home.initadapter.GlobalValues;
import et.tsingtaopad.http.HttpParseJson;
import et.tsingtaopad.util.requestHeadUtil;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class VisitFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    LinearLayout button;
    RelativeLayout xtTermBtn;
    LinearLayout zdzsBtn;
    RelativeLayout zsTermBtn;
    RelativeLayout agencyresBtn;
    RelativeLayout agencycheckBtn;
    RelativeLayout addtermBtn;
    //RelativeLayout startSync;

    private TextView visit_xt_termname;
    private TextView visit_xt_nexttermname;
    private TextView visit_xt_ydname;
    private TextView visit_xt_upload;
    private TextView visit_xt_address;

    private TextView visit_zs_termname;
    private TextView visit_zs_nexttermname;
    private TextView visit_zs_ydname;
    private TextView visit_zs_upload;
    private TextView visit_zs_address;

    private int count;
    private List<String> tablenames;


    MyHandler handler;

    public static final int SYNC_SUCCSE = 1101;// 开始请求
    public static final int SYNC_START = 1102;// 弹出进度弹窗
    public static final int SYNC_CLOSE = 1103;// 请求结束 关闭进度条

    MainService service;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dd_visit, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {

        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        backBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        button = (LinearLayout) view.findViewById(R.id.dd_btn_xtbf);
        xtTermBtn = (RelativeLayout) view.findViewById(R.id.dd_btn_xt_term);
        zdzsBtn = (LinearLayout) view.findViewById(R.id.dd_btn_zdzs);
        zsTermBtn = (RelativeLayout) view.findViewById(R.id.dd_btn_zs_term);

        agencyresBtn = (RelativeLayout) view.findViewById(R.id.dd_btn_zs_agencyres);
        agencycheckBtn = (RelativeLayout) view.findViewById(R.id.dd_btn_zs_agencycheck);
        addtermBtn = (RelativeLayout) view.findViewById(R.id.dd_btn_zs_addterm);


        visit_xt_termname = (TextView)view. findViewById(R.id.visit_xt_termname);
        visit_xt_nexttermname = (TextView) view.findViewById(R.id.visit_xt_nexttermname);
        visit_xt_ydname = (TextView) view.findViewById(R.id.visit_xt_ydname);
        visit_xt_upload = (TextView) view.findViewById(R.id.visit_xt_upload);
        visit_xt_address = (TextView) view.findViewById(R.id.visit_xt_address);

        visit_zs_termname = (TextView) view.findViewById(R.id.visit_zs_termname);
        visit_zs_nexttermname = (TextView) view.findViewById(R.id.visit_zs_nexttermname);
        visit_zs_ydname = (TextView) view.findViewById(R.id.visit_zs_ydname);
        visit_zs_upload = (TextView) view.findViewById(R.id.visit_zs_upload);
        visit_zs_address = (TextView) view.findViewById(R.id.visit_zs_address);

        /*startSync = (RelativeLayout)view.findViewById(R.id.dd_btn_visit_sync_start);
        startSync.setOnClickListener(this);*/

        button.setOnClickListener(this);
        xtTermBtn.setOnClickListener(this);
        zdzsBtn.setOnClickListener(this);
        zsTermBtn.setOnClickListener(this);

        agencyresBtn.setOnClickListener(this);
        agencycheckBtn.setOnClickListener(this);
        addtermBtn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("拜访管理");
        confirmTv.setBackgroundResource(R.drawable.icon_visit_upload);
        handler = new MyHandler(this);
        service = new MainService(getActivity(), null);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dd_btn_xtbf:// 协同拜访
                changeHomeFragment(new XtTermSelectFragment(), "xttermlistfragment");
                break;
            case R.id.top_navigation_rl_confirm:// 同步数据
                changeHomeFragment(new SyncBasicFragment(), "syncbasicfragment");
                break;
            case R.id.dd_btn_xt_term:// 协同终端夹
                toXtTermCartFragment();
                break;
            case R.id.dd_btn_zdzs:// 终端追溯
                changeHomeFragment(new ZsTermSelectFragment(), "zstermselectfragment");
                break;

            case R.id.dd_btn_zs_term:// 追溯文件夹
                toZsTermCartFragment();
                break;

            case R.id.dd_btn_zs_agencyres:// 经销商资料库
                changeHomeFragment(new DdAgencySelectFragment(), "ddagencyselectfragment");
                break;
            case R.id.dd_btn_zs_agencycheck:// 经销商库存盘点
                changeHomeFragment(new DdAgencyCheckSelectFragment(), "ddagencycheckselectfragment");
                break;
            case R.id.dd_btn_zs_addterm:// 漏店补录
                changeHomeFragment(new DdAddTermFragment(), "ddaddtermfragment");
                break;
        }
    }

    // 跳转追溯终端文件夹
    private void toZsTermCartFragment() {
        List<MstTerminalinfoMZsCart> valueLst = service.getMstTerminalinfoMZsCartList();
        if(valueLst.size()>0){
            Bundle zsBundle = new Bundle();
            zsBundle.putSerializable("fromFragment", "VisitFragment");
            ZsTermCartFragment zsTermCartFragment = new ZsTermCartFragment();
            zsTermCartFragment.setArguments(zsBundle);
            changeHomeFragment(zsTermCartFragment, "zstermcartfragment");
        }else{
            // Toast.makeText(getActivity(),"协同终端夹暂无数据",Toast.LENGTH_SHORT).show();

            new AlertView("终端夹暂无数据是否去添加", null, null, new String[]{"取消","确定"}, null, getActivity(), AlertView.Style.Alert,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            //Toast.makeText(getApplicationContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                            if (0 == position) {// 取消

                            }else if(1 == position){// 确定
                                changeHomeFragment(new ZsTermSelectFragment(), "zstermselectfragment");
                            }
                        }
                    })
                    .setCancelable(true)
                    .setOnDismissListener(null)
                    .show();
        }
    }

    // 跳转协同终端文件夹
    private void toXtTermCartFragment() {
        List<MstTerminalinfoMCart> valueLst = service.getMstTerminalinfoMCartList();
        if(valueLst.size()>0){
            Bundle bundle = new Bundle();
            bundle.putSerializable("fromFragment", "VisitFragment");
            XtTermCartFragment xtTermCartFragment = new XtTermCartFragment();
            xtTermCartFragment.setArguments(bundle);
            changeHomeFragment(xtTermCartFragment, "xttermcartfragment");
        }else{
            // Toast.makeText(getActivity(),"追溯终端夹暂无数据",Toast.LENGTH_SHORT).show();
            new AlertView("终端夹暂无数据是否去添加", null, null, new String[]{"取消","确定"}, null, getActivity(), AlertView.Style.Alert,
                    new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            //Toast.makeText(getApplicationContext(), "点击了第" + position + "个", Toast.LENGTH_SHORT).show();
                            if (0 == position) {// 取消

                            }else if(1 == position){// 确定
                                changeHomeFragment(new XtTermSelectFragment(), "xttermlistfragment");
                            }
                        }
                    })
                    .setCancelable(true)
                    .setOnDismissListener(null)
                    .show();
        }
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<VisitFragment> fragmentRef;

        public MyHandler(VisitFragment fragment) {
            fragmentRef = new SoftReference<VisitFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            VisitFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }
            // 处理UI 变化
            switch (msg.what) {
                default:
                    break;
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //  最近协同
            List<VisitContentStc> visitContentStcs = service.queryLastVisitTerminal();
            if(visitContentStcs.size()>0){
                VisitContentStc contentStc = visitContentStcs.get(0);
                visit_xt_termname.setText(contentStc.getTerminalname());
                visit_xt_ydname.setText(contentStc.getUsername());
                if("1".equals(contentStc.getPadisconsistent())){
                    visit_xt_upload.setText("已上传");
                }else{
                    visit_xt_upload.setText("未上传");
                }
            }else{
                visit_xt_termname.setText("--");
                visit_xt_ydname.setText("--");
                visit_xt_upload.setText("--");
            }

            // 最近追溯
            List<VisitContentStc> zsContents = service.queryLastZsTerminal();
            if(zsContents.size()>0){
                VisitContentStc contentStc = zsContents.get(0);
                visit_zs_termname.setText(contentStc.getTerminalname());
                visit_zs_ydname.setText(contentStc.getUsername());
                if("1".equals(contentStc.getPadisconsistent())){
                    visit_zs_upload.setText("已上传");
                }else{
                    visit_zs_upload.setText("未上传");
                }
            }else{
                visit_zs_termname.setText("--");
                visit_zs_ydname.setText("--");
                visit_zs_upload.setText("--");
            }

            // 预计协同
            List<VisitContentStc> xtNextContents = service.queryNextVisitTerminal();
            if(xtNextContents.size()>0){
                VisitContentStc contentStc = xtNextContents.get(0);
                visit_xt_nexttermname.setText(contentStc.getTerminalname());
                visit_xt_address.setText(contentStc.getAddress());
            }else{
                visit_xt_nexttermname.setText("--");
                visit_xt_address.setText("--");
            }

            // 预计追溯
            List<VisitContentStc> zsNextContents = service.queryNextZsTerminal();
            if(zsNextContents.size()>0){
                VisitContentStc contentStc = zsNextContents.get(0);
                visit_zs_nexttermname.setText(contentStc.getTerminalname());
                visit_zs_address.setText(contentStc.getAddress());
            }else{
                visit_zs_nexttermname.setText("--");
                visit_zs_address.setText("--");
            }
        }
    }

}
