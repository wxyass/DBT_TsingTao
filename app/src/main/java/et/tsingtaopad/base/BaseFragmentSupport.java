package et.tsingtaopad.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import et.tsingtaopad.R;

public class BaseFragmentSupport extends Fragment {

	public FragmentManager supportFragmentManager;
    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		supportFragmentManager = getActivity().getSupportFragmentManager();
	}

	@Override
	public void onPause() {
		super.onPause();
		try {
			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			View currentFocus = getActivity().getCurrentFocus();
			if (currentFocus != null) {
				IBinder windowToken = currentFocus.getWindowToken();
				imm.hideSoftInputFromWindow(windowToken, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public boolean allowBackPressed() {
		FragmentManager fm = getChildFragmentManager();
		//从fragment管理器中得到Fragment当前已加入Fragment回退栈中的fragment的数量。
		if (fm.getBackStackEntryCount() > 0) {
			fm.popBackStack();
			return true;
		}
		return false;
	}

	//
	public void changeHomeFragment(BaseFragmentSupport fragment, String tag) {
		// 获取Fragment管理者
		FragmentManager supportFragmentManager = getActivity().getSupportFragmentManager();
		// 开启事物
		FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
		// 当前要添加的Fragment需要放的容器位置
		// 要替换的fragment
		// 标记
		beginTransaction.replace(R.id.home_container, fragment, tag);
		beginTransaction.addToBackStack(null);
		// 提交事物
		beginTransaction.commit();
	}


}
