package et.tsingtaopad.dd.ddzs.zsinvoicing.zsinvocingtz;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.db.table.MitValaddaccountproMTemp;
import et.tsingtaopad.dd.ddzs.zsinvoicing.ZsInvoicingFragment;
import et.tsingtaopad.dd.ddzs.zsinvoicing.zsinvocingtz.domain.ZsTzItemIndex;

/**
 * Created by yangwenmin on 2018/3/12.
 * <p>
 * 进销存 录入正确数据
 */

public class ZsInvoicingTzAmendFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    ZsInvoicingFragment.MyHandler handler;

    private et.tsingtaopad.view.NoScrollListView amend_lv;
    private Button sureBtn;

    protected ZsTzItemIndex zstzitemindex;//

    public ZsInvoicingTzAmendFragment() {

    }

    @SuppressLint("ValidFragment")
    public ZsInvoicingTzAmendFragment(ZsInvoicingFragment.MyHandler handler) {
        this.handler = handler;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_invoicing_tz_amend, container, false);
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

        amend_lv = (et.tsingtaopad.view.NoScrollListView) view.findViewById(R.id.zs_invoicing_tz_amend_lv);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);



        // 获取传递过来的数据
        Bundle bundle = getArguments();
        zstzitemindex = (ZsTzItemIndex) bundle.getSerializable("zstzitemindex");

        titleTv.setText(zstzitemindex.getValproname());

        initData();
    }

    private void initData() {

        List<MitValaddaccountproMTemp>  valaddaccountproMTemps = zstzitemindex.getIndexValueLst();
        amend_lv.setAdapter(new ZsInvoicingTzAmendAdapter(getActivity(), valaddaccountproMTemps));

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_tv_title:// 标题
                break;
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();// 取消
                break;


            case R.id.zdzs_invoicing_amend_dd_bt_save:// 确定
                saveValue();
                supportFragmentManager.popBackStack();
                break;

            default:
                break;
        }
    }

    private void saveValue() {


    }



}
