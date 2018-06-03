package et.tsingtaopad.dd.ddzs.zsfees;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.SoftReference;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.core.view.alertview.AlertView;
import et.tsingtaopad.core.view.alertview.OnItemClickListener;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;
import et.tsingtaopad.listviewintf.IClick;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZsAgreeFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private final String TAG = "ZsAgreeFragment";

    MyHandler handler;

    public static final int FEES_ZS_ADD_SUC = 71;//
    public static final int FEES_INIT_AMEND = 72;//


    private TextView tv_agreecode_con1;
    private TextView tv_agencyname_con1;
    private TextView rl_moneyagency_con1;
    private RelativeLayout rl_startdate;
    private TextView rl_startdate_con1;
    private TextView rl_startdate_statue;
    private RelativeLayout rl_enddate;
    private TextView rl_enddate_con1;
    private TextView rl_enddate_statue;
    private TextView rl_paytype_con1;
    private TextView rl_contact_con1;
    private TextView rl_mobile_con1;
    private RelativeLayout rl_notes;
    private TextView rl_notes_con1;
    private TextView rl_notes_statue;
    private ListView lv_cash;
    private ZsAgreeAdapter zsAgreeAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_agree, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

        tv_agreecode_con1 = (TextView) view.findViewById(R.id.zdzs_agree_tv_agreecode_con1);

        tv_agencyname_con1 = (TextView) view.findViewById(R.id.zdzs_agree_tv_agencyname_con1);

        rl_moneyagency_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_moneyagency_con1);

        rl_startdate = (RelativeLayout) view.findViewById(R.id.zdzs_agree_rl_startdate);
        rl_startdate_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_startdate_con1);
        rl_startdate_statue = (TextView) view.findViewById(R.id.zdzs_agree_rl_startdate_statue);

        rl_enddate = (RelativeLayout) view.findViewById(R.id.zdzs_agree_rl_enddate);
        rl_enddate_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_enddate_con1);
        rl_enddate_statue = (TextView) view.findViewById(R.id.zdzs_agree_rl_enddate_statue);

        rl_paytype_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_paytype_con1);

        rl_contact_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_contact_con1);

        rl_mobile_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_mobile_con1);

        rl_notes = (RelativeLayout) view.findViewById(R.id.zdzs_agree_rl_notes);
        rl_notes_con1 = (TextView) view.findViewById(R.id.zdzs_agree_rl_notes_con1);
        rl_notes_statue = (TextView) view.findViewById(R.id.zdzs_agree_rl_notes_statue);

        lv_cash = (ListView) view.findViewById(R.id.zdzs_agree_lv_cash);

        rl_startdate.setOnClickListener(this);
        rl_enddate.setOnClickListener(this);
        rl_notes.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);

        // 初始化数据
        initData();

    }

    private void initData() {

        //
        zsAgreeAdapter = new ZsAgreeAdapter(getActivity(), null, new IClick() {
            @Override
            public void listViewItemClick(int position, View v) {
                alertShow6(position);
            }
        });
        lv_cash.setAdapter(zsAgreeAdapter);

    }

    // 核查产品兑换信息
    public void alertShow6(final int posi) {
        new AlertView("请选择核查结果", null, null, null,
                new String[]{"正确","品种有误","承担金额有误","实际数量有误"},
                getActivity(), AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (0 == position) {// 正确

                        } else if(1 == position) {// 品种有误

                        }else if(2 == position) {// 承担金额有误

                        }else if(3 == position) {// 实际数量有误

                        }

                    }
                }).setCancelable(true).show();
    }

    // 核查协议基本信息
    public void alertShow5(final int type) {
        new AlertView("请选择核查结果", null, "取消", null,
                new String[]{"正确","错误"},
                getActivity(), AlertView.Style.ActionSheet,
                new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (0 == position) {// 正确
                            Toast.makeText(getActivity(),type+" 正确",Toast.LENGTH_SHORT).show();
                        } else if(1 == position) {// 错误
                            Toast.makeText(getActivity(),type+" 错误",Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setCancelable(true).show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.zdzs_agree_rl_startdate:
                alertShow5(1);
                break;
            case R.id.zdzs_agree_rl_enddate:
                alertShow5(1);
                break;
            case R.id.zdzs_agree_rl_notes:
                alertShow5(1);
                break;
            default:
                break;
        }
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<ZsAgreeFragment> fragmentRef;

        public MyHandler(ZsAgreeFragment fragment) {
            fragmentRef = new SoftReference<ZsAgreeFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZsAgreeFragment fragment = fragmentRef.get();
            if (fragment == null) {
                return;
            }

            // 处理UI 变化
            switch (msg.what) {
                case FEES_ZS_ADD_SUC:
                    //fragment.showAddProSuc(products, agency);
                    break;
                case FEES_INIT_AMEND: // 督导输入数据后
                    //fragment.showAdapter();
                    break;
            }
        }
    }


    @Override
    public void onPause() {
        super.onPause();
        DbtLog.logUtils(TAG, "onPause()");

        // 保存追溯 进销存数据  MitValsupplyMTemp
        //invoicingService.saveZsInvoicing(dataLst);
    }

}
