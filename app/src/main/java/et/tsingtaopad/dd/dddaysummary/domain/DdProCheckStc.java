package et.tsingtaopad.dd.dddaysummary.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DdProCheckStc implements Serializable{
    
    private static final long serialVersionUID = 354488046253065993L;

    private String dicname;
    private String proname;
    private String termratio;
    private String totalterm;
    private String trueterm;

    public String getDicname() {
        return dicname;
    }

    public void setDicname(String dicname) {
        this.dicname = dicname;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }

    public String getTermratio() {
        return termratio;
    }

    public void setTermratio(String termratio) {
        this.termratio = termratio;
    }

    public String getTotalterm() {
        return totalterm;
    }

    public void setTotalterm(String totalterm) {
        this.totalterm = totalterm;
    }

    public String getTrueterm() {
        return trueterm;
    }

    public void setTrueterm(String trueterm) {
        this.trueterm = trueterm;
    }
}
