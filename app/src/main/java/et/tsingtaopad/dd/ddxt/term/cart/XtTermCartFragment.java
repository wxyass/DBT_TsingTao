package et.tsingtaopad.dd.ddxt.term.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddxt.term.select.adapter.XtTermSelectAdapter;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtTermCartFragment extends BaseFragmentSupport implements View.OnClickListener{

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    private TextView lineTv;
    private TextView dateTv;
    private TextView dayTv;
    private EditText searchEt;
    private Button searchBtn;
    private Button updateBtn;
    private Button addBtn;
    private ListView termCartLv;

    private List<XtTermSelectMStc> termList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_termcart, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view){
        backBtn = (RelativeLayout)view.findViewById(R.id.top_navigation_rl_back);
        confirmBtn = (RelativeLayout)view.findViewById(R.id.top_navigation_rl_confirm);
        confirmTv = (AppCompatTextView)view.findViewById(R.id.top_navigation_bt_confirm);
        backTv = (AppCompatTextView)view.findViewById(R.id.top_navigation_bt_back);
        titleTv = (AppCompatTextView)view.findViewById(R.id.top_navigation_tv_title);
        confirmBtn.setVisibility(View.VISIBLE);
        confirmBtn.setOnClickListener(this);
        backBtn.setOnClickListener(this);

        lineTv = (TextView) view.findViewById(R.id.xtbf_termcart_tv_visitline);
        dateTv = (TextView) view.findViewById(R.id.xtbf_termcart_tv_visitline_date);
        dayTv = (TextView) view.findViewById(R.id.xtbf_termcart_tv_visitline_day);
        searchEt = (EditText) view.findViewById(R.id.xtbf_termcart_et_search);
        searchBtn = (Button) view.findViewById(R.id.xtbf_termcart_bt_search);
        updateBtn = (Button) view.findViewById(R.id.xtbf_termcart_bt_update);
        addBtn = (Button) view.findViewById(R.id.xtbf_termcart_bt_add);
        termCartLv = (ListView) view.findViewById(R.id.xtbf_termcart_lv);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText("今日拜访终端列表");
        initTermData();
        termCartLv.setAdapter(new XtTermSelectAdapter(getActivity(),termList,termList,confirmTv,null));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:
                Intent intent = new Intent(getActivity(),XtVisitShopActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    // 设置终端数据 假数据
    private void initTermData() {
        termList = new ArrayList<XtTermSelectMStc>();
        XtTermSelectMStc xtTermSelectMStc = new XtTermSelectMStc();
        xtTermSelectMStc.setTerminalkey("1-QWER1");
        xtTermSelectMStc.setTerminalname("北京建国门超级门店1");
        xtTermSelectMStc.setSequence("1");
        termList.add(xtTermSelectMStc);

        XtTermSelectMStc xtTermSelectMStc2 = new XtTermSelectMStc();
        xtTermSelectMStc2.setTerminalkey("1-QWER2");
        xtTermSelectMStc2.setTerminalname("北京建国门超级门店2");
        xtTermSelectMStc2.setSequence("2");
        termList.add(xtTermSelectMStc2);

        XtTermSelectMStc xtTermSelectMStc3 = new XtTermSelectMStc();
        xtTermSelectMStc3.setTerminalkey("1-QWER3");
        xtTermSelectMStc3.setTerminalname("北京建国门超级门店3");
        xtTermSelectMStc3.setSequence("3");
        termList.add(xtTermSelectMStc3);

        XtTermSelectMStc xtTermSelectMStc4 = new XtTermSelectMStc();
        xtTermSelectMStc4.setTerminalkey("1-QWER4");
        xtTermSelectMStc4.setTerminalname("北京建国门超级门店4");
        xtTermSelectMStc4.setSequence("4");
        termList.add(xtTermSelectMStc4);
    }
}
