package et.tsingtaopad.business.first.bean;

import java.util.List;

/**
 * Created by yangwenmin on 2018/4/8.
 */

public class AreaGridRoute {

    private String MST_GRID_M;// 定格表
    private String MST_MARKETAREA_M;// 区域表
    private String MST_ROUTE_M;// 路线表

    private String CMM_DATADIC_M;// 字典表
    private String CMM_AREA_M;// 省市县表
    private String MST_PROMOTIONS_M;// 活动表
    private String MST_PROMOPRODUCT_INFO;// 活动产品表
    private String MST_PICTYPE_M;// 图片类型表
    private String MST_PRODUCT_M;// 常用我品表

    private String MST_CMPCOMPANY_M;// 竞品公司表
    private String MST_CMPBRANDS_M;// 竞品品牌表
    private String MST_CMPRODUCTINFO_M;// 竞品产品表

    private String MST_CHECKSTATUS_INFO;// 指标达成表 对应 PAD_CHECKSTATUS_INFO
    private String MST_COLLECTIONTEMPLATE_M;//指标模板 对应PAD3张表

    private String MST_TERMINALINFO_M;// 终端表


    private String MST_AGENCYGRID_INFO;//
    private String MST_AGENCYINFO_M;//
    private String MST_AGENCYSUPPLY_INFO;//
    private String MST_CHECKEXERECORD_INFO;//
    private String MST_CMPSUPPLY_INFO;//
    private String MST_COLLECTIONEXERECORD_INFO;//
    private String MST_GROUPPRODUCT_M;//
    private String MST_PROMOTERM_INFO;//
    private String MST_VISIT_M;//
    private String MST_VISTPRODUCT_INFO;//



    public String getMST_GRID_M() {
        return MST_GRID_M;
    }

    public void setMST_GRID_M(String MST_GRID_M) {
        this.MST_GRID_M = MST_GRID_M;
    }

    public String getMST_MARKETAREA_M() {
        return MST_MARKETAREA_M;
    }

    public void setMST_MARKETAREA_M(String MST_MARKETAREA_M) {
        this.MST_MARKETAREA_M = MST_MARKETAREA_M;
    }

    public String getMST_ROUTE_M() {
        return MST_ROUTE_M;
    }

    public void setMST_ROUTE_M(String MST_ROUTE_M) {
        this.MST_ROUTE_M = MST_ROUTE_M;
    }

    public String getCMM_DATADIC_M() {
        return CMM_DATADIC_M;
    }

    public void setCMM_DATADIC_M(String CMM_DATADIC_M) {
        this.CMM_DATADIC_M = CMM_DATADIC_M;
    }

    public String getCMM_AREA_M() {
        return CMM_AREA_M;
    }

    public void setCMM_AREA_M(String CMM_AREA_M) {
        this.CMM_AREA_M = CMM_AREA_M;
    }

    public String getMST_PROMOTIONS_M() {
        return MST_PROMOTIONS_M;
    }

    public void setMST_PROMOTIONS_M(String MST_PROMOTIONS_M) {
        this.MST_PROMOTIONS_M = MST_PROMOTIONS_M;
    }

    public String getMST_PROMOPRODUCT_INFO() {
        return MST_PROMOPRODUCT_INFO;
    }

    public void setMST_PROMOPRODUCT_INFO(String MST_PROMOPRODUCT_INFO) {
        this.MST_PROMOPRODUCT_INFO = MST_PROMOPRODUCT_INFO;
    }

    public String getMST_PICTYPE_M() {
        return MST_PICTYPE_M;
    }

    public void setMST_PICTYPE_M(String MST_PICTYPE_M) {
        this.MST_PICTYPE_M = MST_PICTYPE_M;
    }

    public String getMST_PRODUCT_M() {
        return MST_PRODUCT_M;
    }

    public void setMST_PRODUCT_M(String MST_PRODUCT_M) {
        this.MST_PRODUCT_M = MST_PRODUCT_M;
    }

    public String getMST_CMPCOMPANY_M() {
        return MST_CMPCOMPANY_M;
    }

    public void setMST_CMPCOMPANY_M(String MST_CMPCOMPANY_M) {
        this.MST_CMPCOMPANY_M = MST_CMPCOMPANY_M;
    }

    public String getMST_CMPBRANDS_M() {
        return MST_CMPBRANDS_M;
    }

    public void setMST_CMPBRANDS_M(String MST_CMPBRANDS_M) {
        this.MST_CMPBRANDS_M = MST_CMPBRANDS_M;
    }

    public String getMST_CMPRODUCTINFO_M() {
        return MST_CMPRODUCTINFO_M;
    }

    public void setMST_CMPRODUCTINFO_M(String MST_CMPRODUCTINFO_M) {
        this.MST_CMPRODUCTINFO_M = MST_CMPRODUCTINFO_M;
    }

    public String getMST_CHECKSTATUS_INFO() {
        return MST_CHECKSTATUS_INFO;
    }

    public void setMST_CHECKSTATUS_INFO(String MST_CHECKSTATUS_INFO) {
        this.MST_CHECKSTATUS_INFO = MST_CHECKSTATUS_INFO;
    }

    public String getMST_COLLECTIONTEMPLATE_M() {
        return MST_COLLECTIONTEMPLATE_M;
    }

    public void setMST_COLLECTIONTEMPLATE_M(String MST_COLLECTIONTEMPLATE_M) {
        this.MST_COLLECTIONTEMPLATE_M = MST_COLLECTIONTEMPLATE_M;
    }

    public String getMST_TERMINALINFO_M() {
        return MST_TERMINALINFO_M;
    }

    public void setMST_TERMINALINFO_M(String MST_TERMINALINFO_M) {
        this.MST_TERMINALINFO_M = MST_TERMINALINFO_M;
    }


    public String getMST_AGENCYGRID_INFO() {
        return MST_AGENCYGRID_INFO;
    }

    public void setMST_AGENCYGRID_INFO(String MST_AGENCYGRID_INFO) {
        this.MST_AGENCYGRID_INFO = MST_AGENCYGRID_INFO;
    }

    public String getMST_AGENCYINFO_M() {
        return MST_AGENCYINFO_M;
    }

    public void setMST_AGENCYINFO_M(String MST_AGENCYINFO_M) {
        this.MST_AGENCYINFO_M = MST_AGENCYINFO_M;
    }

    public String getMST_AGENCYSUPPLY_INFO() {
        return MST_AGENCYSUPPLY_INFO;
    }

    public void setMST_AGENCYSUPPLY_INFO(String MST_AGENCYSUPPLY_INFO) {
        this.MST_AGENCYSUPPLY_INFO = MST_AGENCYSUPPLY_INFO;
    }

    public String getMST_CHECKEXERECORD_INFO() {
        return MST_CHECKEXERECORD_INFO;
    }

    public void setMST_CHECKEXERECORD_INFO(String MST_CHECKEXERECORD_INFO) {
        this.MST_CHECKEXERECORD_INFO = MST_CHECKEXERECORD_INFO;
    }

    public String getMST_CMPSUPPLY_INFO() {
        return MST_CMPSUPPLY_INFO;
    }

    public void setMST_CMPSUPPLY_INFO(String MST_CMPSUPPLY_INFO) {
        this.MST_CMPSUPPLY_INFO = MST_CMPSUPPLY_INFO;
    }

    public String getMST_COLLECTIONEXERECORD_INFO() {
        return MST_COLLECTIONEXERECORD_INFO;
    }

    public void setMST_COLLECTIONEXERECORD_INFO(String MST_COLLECTIONEXERECORD_INFO) {
        this.MST_COLLECTIONEXERECORD_INFO = MST_COLLECTIONEXERECORD_INFO;
    }

    public String getMST_GROUPPRODUCT_M() {
        return MST_GROUPPRODUCT_M;
    }

    public void setMST_GROUPPRODUCT_M(String MST_GROUPPRODUCT_M) {
        this.MST_GROUPPRODUCT_M = MST_GROUPPRODUCT_M;
    }

    public String getMST_PROMOTERM_INFO() {
        return MST_PROMOTERM_INFO;
    }

    public void setMST_PROMOTERM_INFO(String MST_PROMOTERM_INFO) {
        this.MST_PROMOTERM_INFO = MST_PROMOTERM_INFO;
    }

    public String getMST_VISIT_M() {
        return MST_VISIT_M;
    }

    public void setMST_VISIT_M(String MST_VISIT_M) {
        this.MST_VISIT_M = MST_VISIT_M;
    }

    public String getMST_VISTPRODUCT_INFO() {
        return MST_VISTPRODUCT_INFO;
    }

    public void setMST_VISTPRODUCT_INFO(String MST_VISTPRODUCT_INFO) {
        this.MST_VISTPRODUCT_INFO = MST_VISTPRODUCT_INFO;
    }
}
