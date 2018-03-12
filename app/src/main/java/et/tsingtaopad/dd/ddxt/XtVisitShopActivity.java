package et.tsingtaopad.dd.ddxt;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;

import et.tsingtaopad.R;
import et.tsingtaopad.base.BaseActivity;
import et.tsingtaopad.home.homefragment.MainFragment;

/**
 * Created by yangwenmin on 2018/3/12.
 */

public class XtVisitShopActivity extends BaseActivity {
    AppCompatButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xtvisitshop);
        //一开始进入程序,就往容器中替换Fragment
    }

}
