package et.tsingtaopad.dd.ddxt.base;

import android.os.Bundle;
import android.support.annotation.Nullable;

import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.dd.ddxt.term.select.domain.XtTermSelectMStc;

/**
 * Created by yangwenmin on 2018/3/26.
 */

public class XtBaseVisitFragment extends BaseFragmentSupport{

    protected String visitId="b61758cd1d81444da1c8390b0b944a73";// 拜访主表key
    protected String termId="1-AW46W7";// 终端key
    protected String termName="坏坏坏";// 终端名称
    protected String seeFlag="0";// 0:拜访  1:查看
    protected String visitDate;//
    protected String lastTime;//
    protected XtTermSelectMStc termStc;// 终端信息
    protected String channelId="39DD41A3991E8C68E05010ACE0016FCD";// 终端次渠道(废弃,因为由A->F后,可能在F中修改,但在另一个F中,还是用的A传递过来的,所以废弃)

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // 获取传递过来的数据
        Bundle bundle = getArguments();
        visitId = bundle.getString("visitKey");
        termId = bundle.getString("termId");
        termName = bundle.getString("termname");
        visitDate = bundle.getString("visitDate");
        lastTime = bundle.getString("lastTime");
        seeFlag = bundle.getString("seeFlag");
        channelId = bundle.getString("channelId");
        termStc = (XtTermSelectMStc) bundle.getSerializable("termStc");
    }
}
