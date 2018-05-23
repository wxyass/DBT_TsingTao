package et.tsingtaopad.dd.ddweekplan.domain;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import et.tsingtaopad.dd.ddxt.checking.domain.XtProIndexValue;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DayDetailStc.java</br>
 * 日计划详情
 */
public class DayDetailStc implements Serializable{

	private static final long serialVersionUID = 4156799315664212568L;

	// 周计划主键
	private String weekkey;
	// 日计划主键
	private String daykey;
	// 日计划单个详情主键
	private String detailkey;

	// 追溯区域
	private String valarea;
	// 追溯定格
	private String valgrid;
	// 追溯项key 集合
	private List<String> valcheckkeyLv;
	// 追溯项name 集合
	private List<String> valchecknameLv;
	// 追溯路线key 集合
	private List<String> valroutekeyLv;
	// 追溯路线name 集合
	private List<String> valroutenameLv;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getWeekkey() {
		return weekkey;
	}

	public void setWeekkey(String weekkey) {
		this.weekkey = weekkey;
	}

	public String getDaykey() {
		return daykey;
	}

	public void setDaykey(String daykey) {
		this.daykey = daykey;
	}

	public String getDetailkey() {
		return detailkey;
	}

	public void setDetailkey(String detailkey) {
		this.detailkey = detailkey;
	}

	public String getValarea() {
		return valarea;
	}

	public void setValarea(String valarea) {
		this.valarea = valarea;
	}

	public String getValgrid() {
		return valgrid;
	}

	public void setValgrid(String valgrid) {
		this.valgrid = valgrid;
	}

	public List<String> getValcheckkeyLv() {
		return valcheckkeyLv;
	}

	public void setValcheckkeyLv(List<String> valcheckkeyLv) {
		this.valcheckkeyLv = valcheckkeyLv;
	}

	public List<String> getValchecknameLv() {
		return valchecknameLv;
	}

	public void setValchecknameLv(List<String> valchecknameLv) {
		this.valchecknameLv = valchecknameLv;
	}

	public List<String> getValroutekeyLv() {
		return valroutekeyLv;
	}

	public void setValroutekeyLv(List<String> valroutekeyLv) {
		this.valroutekeyLv = valroutekeyLv;
	}

	public List<String> getValroutenameLv() {
		return valroutenameLv;
	}

	public void setValroutenameLv(List<String> valroutenameLv) {
		this.valroutenameLv = valroutenameLv;
	}
}
