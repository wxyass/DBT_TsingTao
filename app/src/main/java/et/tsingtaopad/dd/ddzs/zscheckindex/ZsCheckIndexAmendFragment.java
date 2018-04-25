package et.tsingtaopad.dd.ddzs.zscheckindex;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.dd.ddxt.checking.XtCheckIndexService;
import et.tsingtaopad.dd.ddxt.checking.domain.XtCheckIndexCalculateStc;
import et.tsingtaopad.main.visit.shopvisit.termvisit.checkindex.domain.CheckIndexPromotionStc;

/**
 * Created by yangwenmin on 2018/3/12.
 * <p>
 * 进销存 录入正确数据
 */

public class ZsCheckIndexAmendFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    ZsCheckIndexFragment.MyHandler handler;

    private Button sureBtn;

    protected XtCheckIndexCalculateStc checkIndexCalculateStc;//
    private XtCheckIndexService service;

    private TextView zdzs_check_index_tv_title;
    private TextView zdzs_check_index_tv_con1;

    private TextView zdzs_check_index_et_title;
    private EditText zdzs_check_index_et_con1;

    private EditText zdzs_check_index_et_report;

    public ZsCheckIndexAmendFragment() {

    }

    @SuppressLint("ValidFragment")
    public ZsCheckIndexAmendFragment(ZsCheckIndexFragment.MyHandler handler) {
        this.handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_check_index_amend, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {
        backBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout) view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView) view.findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.INVISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);


        zdzs_check_index_tv_title = (TextView) view.findViewById(R.id.zdzs_check_index_tv_title);
        zdzs_check_index_tv_con1 = (TextView) view.findViewById(R.id.zdzs_check_index_tv_con1);

        zdzs_check_index_et_title = (TextView) view.findViewById(R.id.zdzs_check_index_et_title);
        zdzs_check_index_et_con1 = (EditText) view.findViewById(R.id.zdzs_check_index_et_con1);

        zdzs_check_index_et_report = (EditText) view.findViewById(R.id.zdzs_check_index_et_report);
        sureBtn = (Button) view.findViewById(R.id.zdzs_check_index_bt_save);

        sureBtn.setOnClickListener(this);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("录入正确数据");

        // 获取传递过来的数据
        Bundle bundle = getArguments();
        checkIndexCalculateStc = (XtCheckIndexCalculateStc) bundle.getSerializable("checkIndexCalculateStc");

        service = new XtCheckIndexService(getActivity(), null);

        initData();
    }

    private void initData() {


        // 业代指标名称
        zdzs_check_index_tv_title.setText(checkIndexCalculateStc.getIndexName());

        // 业代指标值
        if ("666b74b3-b221-4920-b549-d9ec39a463fd".equals(checkIndexCalculateStc.getIndexId())) {// 合作是否到位
            //tempLst = service.queryNoProIndexValueId1();// 此处报错
            if ("9019cf03-4572-4559-9971-48a27a611c3d".equals(checkIndexCalculateStc.getIndexValueId())) {// 合作是否到位 是
                zdzs_check_index_tv_con1.setText("是");
            }else{
                zdzs_check_index_tv_con1.setText("否");
            }
        } else if ("df2e88c9-246f-40e2-b6e5-08cdebf8c281".equals(checkIndexCalculateStc.getIndexId())) {// 是否高质量配送
            //tempLst = service.queryNoProIndexValueId2();
            if ("460647a9-283a-44ea-b11f-42efe1fd62e4".equals(checkIndexCalculateStc.getIndexValueId())) {// 是否高质量配送
                zdzs_check_index_tv_con1.setText("是");
            }else{
                zdzs_check_index_tv_con1.setText("否");
            }
        } else if ("59802090-02ac-4146-9cc3-f09570c36a26".equals(checkIndexCalculateStc.getIndexId())) {// 我品占有率
            zdzs_check_index_tv_con1.setText(service.getCheckStatusName(checkIndexCalculateStc.getIndexValueId()));
        }

        // 督导数据
        //zdzs_check_promo_et_con1.setText(checkIndexPromotionStc.getValistruenumval());

        // 督导备注
        zdzs_check_index_et_report.setText(checkIndexCalculateStc.getDdremark());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_tv_title:// 标题
                break;
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();// 取消
                break;


            case R.id.zdzs_check_index_bt_save:// 确定
                saveValue();
                supportFragmentManager.popBackStack();
                break;

            default:
                break;
        }
    }


    private void saveValue() {

        checkIndexCalculateStc.setValchecktypeflag("N");// 达成组数正确与否

        // 督导达成组数
        //String qdinfo = zdzs_check_promo_et_con1.getText().toString();
        //checkIndexPromotionStc.setValistruenumval(qdinfo);

        // 备注
        String report = zdzs_check_index_et_report.getText().toString();
        checkIndexCalculateStc.setDdremark(report);

        handler.sendEmptyMessage(ZsCheckIndexFragment.INIT_HPZ_AMEND);

    }
}
