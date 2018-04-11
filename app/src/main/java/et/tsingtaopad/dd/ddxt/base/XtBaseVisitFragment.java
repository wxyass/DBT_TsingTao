package et.tsingtaopad.dd.ddxt.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import et.tsingtaopad.base.BaseFragmentSupport;
import et.tsingtaopad.core.util.dbtutil.FunUtil;
import et.tsingtaopad.db.table.MstTerminalinfoM;
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

    /**
     * 通过uri获取图片并进行压缩
     *
     * @param uri
     */
    public static Bitmap getBitmapFormUri(Context ac, Uri uri) throws FileNotFoundException, IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        //图片分辨率以480x800为标准
        float hh = 640f;//这里设置高度为640f
        float ww = 480f;//这里设置宽度为480f
        //缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    /**
     * 质量压缩方法
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 25) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 1;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }
}
