package et.tsingtaopad.business.first.bean;

/**
 * Created by yangwenmin on 2018/4/8.
 */

public class GvTop {

    private String ydname;// 业代名称
    private String count;// 数量
    private String topname;// 冠军名称

    public GvTop() {
    }

    public GvTop(String ydname, String count, String topname) {
        this.ydname = ydname;
        this.count = count;
        this.topname = topname;
    }

    public String getYdname() {
        return ydname;
    }

    public void setYdname(String ydname) {
        this.ydname = ydname;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getTopname() {
        return topname;
    }

    public void setTopname(String topname) {
        this.topname = topname;
    }
}
