package et.tsingtaopad.db.table;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.util.Date;

import et.tsingtaopad.db.dao.impl.MitValsupplyMDaoImpl;
import et.tsingtaopad.db.dao.impl.MitValterMDaoImpl;


/**
 * MitValterM entity. @author MyEclipse Persistence Tools
 */
//MIT_VALTER_M(追溯主表)
@DatabaseTable(tableName = "MIT_VALSUPPLY_M", daoClass = MitValsupplyMDaoImpl.class)
public class MitValsupplyM implements java.io.Serializable {

	// Fields
	@DatabaseField(canBeNull = false, id = true)
	private String id;
	@DatabaseField
	private String valterid;//
	@DatabaseField
	private String valaddagencysupply               ;//char(1)                        null,
	@DatabaseField
	private String valsuplyid           ;//char(1)                        null,
	@DatabaseField
	private String valsqd         ;//varchar2(300)                  null,
	@DatabaseField
	private String valsls             ;//char(1)                        null,
	@DatabaseField
	private String valsdd        ;// char(1)                        null,
	@DatabaseField
	private String valsrxl       ;//varchar2(300)                  null,
	@DatabaseField
	private String valsljk            ;//char(1)                        null,
	@DatabaseField
	private String valter        ;//char(1)                        null,
	@DatabaseField
	private String valpro       ;//varchar2(300)                  null,
	@DatabaseField
	private String valagency        ;//varchar2(20)                   null,
	@DatabaseField
	private String valproname       ;//varchar2(300)                  null,
	@DatabaseField
	private String valagencyname        ;//varchar2(20)                   null,
	@DatabaseField
	private String valagencysupplyflag    ;//char(1)                        null,
	@DatabaseField
	private String valproerror   ;//varchar2(300)                  null,
	@DatabaseField
	private String valagencyerror            ;//char(1)                        null,
	@DatabaseField
	private String valtrueagency            ;//char(1)                        null,
	@DatabaseField
	private String valdataerror        ;//char(1)                        null,
	@DatabaseField
	private String valiffleeing            ;// char(1)                        null,

	@DatabaseField
	private String valiffleeingremark;
	@DatabaseField
	private String valagencysupplyqd         ;//char(1)                        null,
	@DatabaseField
	private String valagencysupplyls       ;//varchar2(300)                  null,
	@DatabaseField
	private String valagencysupplydd        ;//char(1)                        null,
	@DatabaseField
	private String valagencysupplysrxl    ;//char(1)                        null,
	@DatabaseField
	private String valagencysupplyljk         ;//char(1)                        null,
	@DatabaseField
	private String valagencysupplyremark     ;//char(1)                        null,

	@DatabaseField
	private String creuser              ;//varchar2(128)                  null,
	@DatabaseField
	private Date credate              ;//date                           null,
	@DatabaseField
	private String updateuser           ;//varchar2(128)                  null,
	@DatabaseField
	private Date updatedate           ;//date                           null


	// Constructors

	/** default constructor */
	public MitValsupplyM() {
	}

	public MitValsupplyM(String id, String valterid, String valaddagencysupply, String valsuplyid, String valsqd, String valsls, String valsdd, String valsrxl, String valsljk, String valter, String valpro, String valagency, String valagencysupplyflag, String valproerror, String valagencyerror, String valtrueagency, String valdataerror, String valiffleeing, String valagencysupplyqd, String valagencysupplyls, String valagencysupplydd, String valagencysupplysrxl, String valagencysupplyljk, String valagencysupplyremark, String creuser, Date credate, String updateuser, Date updatedate) {

		this.id = id;
		this.valterid = valterid;
		this.valaddagencysupply = valaddagencysupply;
		this.valsuplyid = valsuplyid;
		this.valsqd = valsqd;
		this.valsls = valsls;
		this.valsdd = valsdd;
		this.valsrxl = valsrxl;
		this.valsljk = valsljk;
		this.valter = valter;
		this.valpro = valpro;
		this.valagency = valagency;
		this.valagencysupplyflag = valagencysupplyflag;
		this.valproerror = valproerror;
		this.valagencyerror = valagencyerror;
		this.valtrueagency = valtrueagency;
		this.valdataerror = valdataerror;
		this.valiffleeing = valiffleeing;
		this.valagencysupplyqd = valagencysupplyqd;
		this.valagencysupplyls = valagencysupplyls;
		this.valagencysupplydd = valagencysupplydd;
		this.valagencysupplysrxl = valagencysupplysrxl;
		this.valagencysupplyljk = valagencysupplyljk;
		this.valagencysupplyremark = valagencysupplyremark;
		this.creuser = creuser;
		this.credate = credate;
		this.updateuser = updateuser;
		this.updatedate = updatedate;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getValterid() {
		return valterid;
	}

	public void setValterid(String valterid) {
		this.valterid = valterid;
	}

	public String getValaddagencysupply() {
		return valaddagencysupply;
	}

	public void setValaddagencysupply(String valaddagencysupply) {
		this.valaddagencysupply = valaddagencysupply;
	}

	public String getValsuplyid() {
		return valsuplyid;
	}

	public void setValsuplyid(String valsuplyid) {
		this.valsuplyid = valsuplyid;
	}

	public String getValsqd() {
		return valsqd;
	}

	public void setValsqd(String valsqd) {
		this.valsqd = valsqd;
	}

	public String getValsls() {
		return valsls;
	}

	public void setValsls(String valsls) {
		this.valsls = valsls;
	}

	public String getValsdd() {
		return valsdd;
	}

	public void setValsdd(String valsdd) {
		this.valsdd = valsdd;
	}

	public String getValsrxl() {
		return valsrxl;
	}

	public void setValsrxl(String valsrxl) {
		this.valsrxl = valsrxl;
	}

	public String getValsljk() {
		return valsljk;
	}

	public void setValsljk(String valsljk) {
		this.valsljk = valsljk;
	}

	public String getValter() {
		return valter;
	}

	public void setValter(String valter) {
		this.valter = valter;
	}

	public String getValpro() {
		return valpro;
	}

	public void setValpro(String valpro) {
		this.valpro = valpro;
	}

	public String getValagency() {
		return valagency;
	}

	public void setValagency(String valagency) {
		this.valagency = valagency;
	}

	public String getValagencysupplyflag() {
		return valagencysupplyflag;
	}

	public void setValagencysupplyflag(String valagencysupplyflag) {
		this.valagencysupplyflag = valagencysupplyflag;
	}

	public String getValproerror() {
		return valproerror;
	}

	public void setValproerror(String valproerror) {
		this.valproerror = valproerror;
	}

	public String getValagencyerror() {
		return valagencyerror;
	}

	public void setValagencyerror(String valagencyerror) {
		this.valagencyerror = valagencyerror;
	}

	public String getValtrueagency() {
		return valtrueagency;
	}

	public void setValtrueagency(String valtrueagency) {
		this.valtrueagency = valtrueagency;
	}

	public String getValdataerror() {
		return valdataerror;
	}

	public void setValdataerror(String valdataerror) {
		this.valdataerror = valdataerror;
	}

	public String getValiffleeing() {
		return valiffleeing;
	}

	public void setValiffleeing(String valiffleeing) {
		this.valiffleeing = valiffleeing;
	}

	public String getValagencysupplyqd() {
		return valagencysupplyqd;
	}

	public void setValagencysupplyqd(String valagencysupplyqd) {
		this.valagencysupplyqd = valagencysupplyqd;
	}

	public String getValagencysupplyls() {
		return valagencysupplyls;
	}

	public void setValagencysupplyls(String valagencysupplyls) {
		this.valagencysupplyls = valagencysupplyls;
	}

	public String getValagencysupplydd() {
		return valagencysupplydd;
	}

	public void setValagencysupplydd(String valagencysupplydd) {
		this.valagencysupplydd = valagencysupplydd;
	}

	public String getValagencysupplysrxl() {
		return valagencysupplysrxl;
	}

	public void setValagencysupplysrxl(String valagencysupplysrxl) {
		this.valagencysupplysrxl = valagencysupplysrxl;
	}

	public String getValagencysupplyljk() {
		return valagencysupplyljk;
	}

	public void setValagencysupplyljk(String valagencysupplyljk) {
		this.valagencysupplyljk = valagencysupplyljk;
	}

	public String getValagencysupplyremark() {
		return valagencysupplyremark;
	}

	public void setValagencysupplyremark(String valagencysupplyremark) {
		this.valagencysupplyremark = valagencysupplyremark;
	}

	public String getCreuser() {
		return creuser;
	}

	public void setCreuser(String creuser) {
		this.creuser = creuser;
	}

	public Date getCredate() {
		return credate;
	}

	public void setCredate(Date credate) {
		this.credate = credate;
	}

	public String getUpdateuser() {
		return updateuser;
	}

	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}

	public Date getUpdatedate() {
		return updatedate;
	}

	public void setUpdatedate(Date updatedate) {
		this.updatedate = updatedate;
	}

	public String getValproname() {
		return valproname;
	}

	public void setValproname(String valproname) {
		this.valproname = valproname;
	}

	public String getValagencyname() {
		return valagencyname;
	}

	public void setValagencyname(String valagencyname) {
		this.valagencyname = valagencyname;
	}

	public String getValiffleeingremark() {
		return valiffleeingremark;
	}

	public void setValiffleeingremark(String valiffleeingremark) {
		this.valiffleeingremark = valiffleeingremark;
	}
}