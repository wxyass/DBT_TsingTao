package et.tsingtaopad.home.delete;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.SoftReference;

import et.tsingtaopad.R;
import et.tsingtaopad.core.util.dbtutil.logutil.DbtLog;
import et.tsingtaopad.dd.ddxt.base.XtBaseVisitFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class ZhenshiFragment extends XtBaseVisitFragment implements View.OnClickListener {

    private final String TAG = "ZsFeesFragment";

    MyHandler handler;

    public static final int FEES_ZS_ADD_SUC = 71;//
    public static final int FEES_INIT_AMEND = 72;//



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_zdzs_invoicing, container, false);
        initView(view);
        return view;
    }

    // 初始化控件
    private void initView(View view) {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        handler = new MyHandler(this);

        // 初始化数据
        initData();

    }

    private void initData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            default:
                break;
        }
    }

    /**
     * 接收子线程消息的 Handler
     */
    public static class MyHandler extends Handler {

        // 软引用
        SoftReference<ZhenshiFragment> fragmentRef;

        public MyHandler(ZhenshiFragment fragment) {
            fragmentRef = new SoftReference<ZhenshiFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            ZhenshiFragment fragment = fragmentRef.get();
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
