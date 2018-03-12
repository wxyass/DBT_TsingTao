package et.tsingtaopad.core.net.domain;

/**
 * Created by yangwenmin on 2018/1/8.
 */

public class RequestHeadStc implements java.io.Serializable{

    private static final long serialVersionUID = -4591676831315875574L;
    private String usercode;
    private String password;
    private String optcode;
    private String usertype;
    private String gridkey;
    private String version;
    private String bigareaid;
    private String secareaid;

    public RequestHeadStc() { /* compiled code */ }

    public String getUsercode() {
        return usercode;
    }

    public void setUsercode(String usercode) {
        this.usercode = usercode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOptcode() {
        return optcode;
    }

    public void setOptcode(String optcode) {
        this.optcode = optcode;
    }

    public String getUsertype() {
        return usertype;
    }

    public void setUsertype(String usertype) {
        this.usertype = usertype;
    }

    public String getGridkey() {
        return gridkey;
    }

    public void setGridkey(String gridkey) {
        this.gridkey = gridkey;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getBigareaid() {
        return bigareaid;
    }

    public void setBigareaid(String bigareaid) {
        this.bigareaid = bigareaid;
    }

    public String getSecareaid() {
        return secareaid;
    }

    public void setSecareaid(String secareaid) {
        this.secareaid = secareaid;
    }

}
