package et.tsingtaopad.dd.ddxt.term.select;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.view.dropdownmenu.DropBean;
import et.tsingtaopad.core.view.dropdownmenu.DropdownButton;
import et.tsingtaopad.db.table.MstTerminalinfoM;
import et.tsingtaopad.dd.ddxt.shopvisit.XtVisitShopActivity;
import et.tsingtaopad.dd.ddxt.term.cart.XtTermCartFragment;
import et.tsingtaopad.dd.ddxt.term.select.adapter.XtTermSelectAdapter;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtTermSelectFragment extends BaseFragmentSupport implements View.OnClickListener,AdapterView.OnItemClickListener{

    private RelativeLayout backBtn;
    private RelativeLayout confirmBtn;
    private AppCompatTextView confirmTv;
    private AppCompatTextView backTv;
    private AppCompatTextView titleTv;

    private DropdownButton areaBtn;
    private DropdownButton gridBtn;
    private DropdownButton routeBtn;
    private List<DropBean> areaList;
    private List<DropBean> gridList;
    private List<DropBean> routeList;
    private List<XtTermSelectMStc> termList;



    private LinearLayout termRouteLl;
    private ListView termRouteLv;

    private XtTermSelectService xtSelectService;
    private List<XtTermSelectMStc> selectedList = new ArrayList<XtTermSelectMStc>();// 当前adapter的数据

    private int TOFRAGMENT = 1;
    private int TOACTIVITY = 2;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xtbf_termselect, container, false);
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

        areaBtn = (DropdownButton) view.findViewById(R.id.xtbf_termselect_area);
        gridBtn = (DropdownButton) view.findViewById(R.id.xtbf_termselect_grid);
        routeBtn = (DropdownButton) view.findViewById(R.id.xtbf_termselect_route);
        termRouteLl = (LinearLayout) view.findViewById(R.id.xtbf_termselect_ll_lv);
        termRouteLv = (ListView) view.findViewById(R.id.xtbf_termselect_lv);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        titleTv.setText(R.string.xtbf_selectterm);

        xtSelectService = new XtTermSelectService(getActivity());
        //
        initSomeData();

        areaBtn.setData(areaList);
        gridBtn.setData(gridList);
        routeBtn.setData(routeList);

        areaBtn.setText("选择区域");
        gridBtn.setText("请先选择左侧");
        routeBtn.setText("请先选择左侧");

        // 区域选择
        areaBtn.setOnDropItemSelectListener(new DropdownButton.OnDropItemSelectListener() {
            @Override
            public void onDropItemSelect(int Postion) {
                Toast.makeText(getContext(),"您选择了 "+areaList.get(Postion).getName(),Toast.LENGTH_SHORT).show();
                if(Postion==0){
                    gridList.clear();
                    gridBtn.setText("请先选择左侧");
                    gridList.add(new DropBean("请选择定格"));
                    gridBtn.setData(gridList);
                    routeList.clear();
                    routeBtn.setText("请先选择左侧");
                    routeList.add(new DropBean("请选择路线"));
                    routeBtn.setData(routeList);
                }else{
                    gridList.clear();
                    gridList.add(new DropBean("请选择定格"));
                    gridList.add(new DropBean("1号定格"));
                    gridList.add(new DropBean("2号定格"));
                    gridList.add(new DropBean("3号定格"));
                    gridList.add(new DropBean("4号定格"));
                    gridList.add(new DropBean("5号定格"));
                    gridList.add(new DropBean("6号定格"));
                    gridBtn.setData(gridList);
                }
            }
        });

        // 定格选择
        gridBtn.setOnDropItemSelectListener(new DropdownButton.OnDropItemSelectListener() {
            @Override
            public void onDropItemSelect(int Postion) {
                Toast.makeText(getContext(),"您选择了 "+gridList.get(Postion).getName(),Toast.LENGTH_SHORT).show();
                if(Postion==0){
                    routeList.clear();
                    routeBtn.setText("请先选择左侧");
                    routeList.add(new DropBean("请选择路线"));
                    routeBtn.setData(routeList);
                }else{
                    routeList.clear();
                    routeList.add(new DropBean("请选择路线"));
                    routeList.add(new DropBean("1号路线"));
                    routeList.add(new DropBean("2号路线"));
                    routeList.add(new DropBean("3号路线"));
                    routeList.add(new DropBean("4号路线"));
                    routeBtn.setData(routeList);
                }
            }
        });

        // 路线选择
        routeBtn.setOnDropItemSelectListener(new DropdownButton.OnDropItemSelectListener() {
            @Override
            public void onDropItemSelect(int Postion) {
                Toast.makeText(getContext(),"您选择了 "+routeList.get(Postion).getName(),Toast.LENGTH_SHORT).show();
            }
        });

        // 设置终端数据 假数据
        initTermData();

    }

    // 设置终端数据 假数据
    private void initTermData() {

        termList = new ArrayList<XtTermSelectMStc>();
        // 绑定TermList数据
        List<String> routes = new ArrayList<String>();
        routes.add("1-63UNEX");// 不同路线
        termList.clear();
        termList = xtSelectService.queryTerminal(routes);

        // 设置适配器 加号按钮点击事件
        XtTermSelectAdapter selectAdapter =new XtTermSelectAdapter(getActivity(), termList,termList, confirmTv, null, new IXtTermSelectClick() {
            @Override
            public void imageViewClick(int position, View v) {
                ImageView imageView = (ImageView)v;
                XtTermSelectMStc item = termList.get(position);
                if (selectedList.contains(item)) {
                    selectedList.remove(item);
                    imageView.setImageResource(R.drawable.icon_visit_add);
                }else{
                    selectedList.add(item);
                    imageView.setImageResource(R.drawable.ico_terminal_syncflag);
                }
            }
        });
        // 设置适配器
        termRouteLv.setAdapter(selectAdapter);
        // 条目点击事件
        termRouteLv.setOnItemClickListener(this);
        // 终端列表显示,之后放到下拉选择后显示
        termRouteLl.setVisibility(View.VISIBLE);
    }

    // 下来菜单设置数据  设置区域数据
    private void initSomeData() {
        areaList = new ArrayList<>();
        gridList = new ArrayList<>();
        routeList = new ArrayList<>();


        int mouth = 10;
        areaList.add(new DropBean("请选择区域"));
        for (int i = 0; i < mouth; i++) {
            areaList.add(new DropBean("区域"+i));
        }

        gridList.add(new DropBean("请选择定格"));
        routeList.add(new DropBean("请选择路线"));

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回
            case R.id.top_navigation_rl_back:
                supportFragmentManager.popBackStack();
                break;
            case R.id.top_navigation_rl_confirm:// 确定
                // 生成临时表,并实现页面跳转
                breakNextLayout(TOACTIVITY,selectedList);
                break;
            default:
                break;
        }
    }

    // listview的条目点击事件
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        selectedList.clear();
        selectedList.add(termList.get(position));
        // 生成临时表,并实现页面跳转
        breakNextLayout(TOACTIVITY,selectedList);
    }

    /**
     * 生成临时表,并实现页面跳转
     *
     * @param type              1:跳转Fragment  2:跳转Activity
     * @param selectedList      用户选择的终端
     */
    public void breakNextLayout(int type,List<XtTermSelectMStc> selectedList){
        // 删除临时表表数据
        xtSelectService.deleteData("MST_TERMINALINFO_M_TEMP");
        // 复制终端临时表
        for (XtTermSelectMStc xtselect:selectedList){
            copyMstTerminalinfoMTemp(xtselect);
        }
        // 跳转
        if(TOFRAGMENT == type){
            //
            supportFragmentManager.popBackStack();
            // 跳转终端购物车
            changeHomeFragment(new XtTermCartFragment(), "xttermcartfragment");
        }else if(TOACTIVITY == type){
            //
            Intent intent = new Intent(getActivity(),XtVisitShopActivity.class);
            startActivity(intent);
        }
    }

    // 查找终端,并复制到终端临时表
    public void copyMstTerminalinfoMTemp(XtTermSelectMStc xtselect){
        MstTerminalinfoM term = xtSelectService.findTermByTerminalkey(xtselect.getTerminalkey());
        xtSelectService.toCopyMstTerminalinfoMData(term);
    }


}
