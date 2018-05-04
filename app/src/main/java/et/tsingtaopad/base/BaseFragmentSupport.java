package et.tsingtaopad.base;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import et.tsingtaopad.R;
import et.tsingtaopad.home.initadapter.GlobalValues;

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

	// 权限相关 ↓--------------------------------------------------------------------------

	/**
	 * 判断是否有指定的权限
	 */
	public boolean hasPermission(String... permissions) {

		for (String permisson : permissions) {
			if (ContextCompat.checkSelfPermission(getActivity(), permisson)
					!= PackageManager.PERMISSION_GRANTED) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 申请指定的权限.
	 */
	public void requestPermission(int code, String... permissions) {

		if (Build.VERSION.SDK_INT >= 23) {
			requestPermissions(permissions, code);
		}
	}

	// 定义几个常量

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case GlobalValues.HARDWEAR_CAMERA_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doOpenCamera();
				}
				break;
			case GlobalValues.WRITE_READ_EXTERNAL_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doWriteSDCard();
				}
				break;
			case GlobalValues.LOCAL_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doLocation();
				}
				break;
			case GlobalValues.WRITE_LOCAL_CODE:
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					doCameraWriteSD();
				}else{
					Toast.makeText(getActivity(),"请先开启读取sd卡权限",Toast.LENGTH_SHORT).show();
				}
				break;
		}
	}

	public void doCameraWriteSD() {

	}

	// 定位
	public void doLocation() {
	}


	// 拍照
	public void doOpenCamera() {

	}

	// 读写SD卡业务逻辑,由具体的子类实现
	public void doWriteSDCard() {

	}

	// 权限相关 ↑--------------------------------------------------------------------------

}
