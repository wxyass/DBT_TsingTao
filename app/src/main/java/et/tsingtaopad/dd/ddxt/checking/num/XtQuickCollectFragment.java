package et.tsingtaopad.dd.ddxt.checking.num;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.ViewUtil;
import et.tsingtaopad.dd.ddxt.checking.domain.XtProItem;

/**
 * Created by yangwenmin on 2018/3/12.
 * <p>
 * 指标的现有量,变化量 填写界面
 */

public class XtQuickCollectFragment extends BaseFragmentSupport implements View.OnClickListener {

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;
    private ListView colitemLv;
    private List<XtProItem> tempLst;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_quickcollect, container, false);
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
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        colitemLv = (ListView) view.findViewById(R.id.xtbf_quickcollect_lv_colitem);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Bundle bundle = getArguments();
        tempLst = new ArrayList<XtProItem>();
        tempLst = (List<XtProItem>) bundle.getSerializable("tempLst");
        String proName = bundle.getString("proName");
        titleTv.setText(proName);

        XtCalculateIndexItemPuhuoAdapter xtCalculateIndexItemPuhuoAdapter = new XtCalculateIndexItemPuhuoAdapter(getActivity(), tempLst);
        colitemLv.setAdapter(xtCalculateIndexItemPuhuoAdapter);

        ViewUtil.setListViewHeight(colitemLv);*/

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:
                /*Intent intent = new Intent(getActivity(),XtVisitShopActivity.class);
                startActivity(intent);*/
                break;
            default:
                break;
        }
    }
}
