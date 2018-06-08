package et.tsingtaopad.sign.bean;

/**
 * Created by yangwenmin on 2018/4/8.
 */

public class SignStc {

    private String attenceid;
    private String attencetime;// 打卡时间
    private String attencetype;// 打卡类型
    private String lat;// 经纬度
    private String lon;// 经纬度
    private String address;// 地址
    private String remark;// 原因
    private String systime;// 系统时间

    public SignStc() {
    }

    public SignStc(String attenceid, String attencetime, String attencetype, String lat, String lon, String address, String remark, String systime) {
        this.attenceid = attenceid;
        this.attencetime = attencetime;
        this.attencetype = attencetype;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.remark = remark;
        this.systime = systime;
    }

    public SignStc( String attencetime, String attencetype,  String address, String remark) {
        this.attencetime = attencetime;
        this.attencetype = attencetype;
        this.address = address;
        this.remark = remark;
    }

    public String getAttenceid() {
        return attenceid;
    }

    public void setAttenceid(String attenceid) {
        this.attenceid = attenceid;
    }

    public String getAttencetime() {
        return attencetime;
    }

    public void setAttencetime(String attencetime) {
        this.attencetime = attencetime;
    }

    public String getAttencetype() {
        return attencetype;
    }

    public void setAttencetype(String attencetype) {
        this.attencetype = attencetype;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSystime() {
        return systime;
    }

    public void setSystime(String systime) {
        this.systime = systime;
    }
}
