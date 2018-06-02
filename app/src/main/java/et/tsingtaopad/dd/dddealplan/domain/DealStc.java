package et.tsingtaopad.dd.dddealplan.domain;


import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：DayPlanStc.java</br>
 * 日计划
 */
public class DealStc implements Serializable{

	private String repairid;// 整顿计划主表主键
	private String repaircheckid;//  整改计划审核表  主键
	private String repairterid;//  整改计划终端表  主键
	private String content;//
	private String repairremark;//
	private String checkcontent;//
	private String gridkey;//
	private String gridname;//
	private String userid;//
	private String username;//
	private String status;//
	private String repairtime;//
	private String terminalkey;//
	private String terminalname;//


	public DealStc() {
	}

	public String getRepairid() {
		return repairid;
	}

	public void setRepairid(String repairid) {
		this.repairid = repairid;
	}

	public String getRepaircheckid() {
		return repaircheckid;
	}

	public void setRepaircheckid(String repaircheckid) {
		this.repaircheckid = repaircheckid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRepairremark() {
		return repairremark;
	}

	public void setRepairremark(String repairremark) {
		this.repairremark = repairremark;
	}

	public String getCheckcontent() {
		return checkcontent;
	}

	public void setCheckcontent(String checkcontent) {
		this.checkcontent = checkcontent;
	}

	public String getGridkey() {
		return gridkey;
	}

	public void setGridkey(String gridkey) {
		this.gridkey = gridkey;
	}

	public String getGridname() {
		return gridname;
	}

	public void setGridname(String gridname) {
		this.gridname = gridname;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRepairtime() {
		return repairtime;
	}

	public void setRepairtime(String repairtime) {
		this.repairtime = repairtime;
	}

	public String getTerminalkey() {
		return terminalkey;
	}

	public void setTerminalkey(String terminalkey) {
		this.terminalkey = terminalkey;
	}

	public String getTerminalname() {
		return terminalname;
	}

	public void setTerminalname(String terminalname) {
		this.terminalname = terminalname;
	}

	public String getRepairterid() {
		return repairterid;
	}

	public void setRepairterid(String repairterid) {
		this.repairterid = repairterid;
	}
}
