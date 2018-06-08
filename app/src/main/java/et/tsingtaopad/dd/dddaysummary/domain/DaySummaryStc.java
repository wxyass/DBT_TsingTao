package et.tsingtaopad.dd.dddaysummary.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class DaySummaryStc implements Serializable{
    
    private static final long serialVersionUID = 354588046223065993L;

    // 工作计划
    private String workplan;
    private String themainproductshopgoods;
    private String themainproductprice;
    private String promotionactivity;
    private String agencyrepertory;

    public String getWorkplan() {
        return workplan;
    }

    public void setWorkplan(String workplan) {
        this.workplan = workplan;
    }

    public String getThemainproductshopgoods() {
        return themainproductshopgoods;
    }

    public void setThemainproductshopgoods(String themainproductshopgoods) {
        this.themainproductshopgoods = themainproductshopgoods;
    }

    public String getThemainproductprice() {
        return themainproductprice;
    }

    public void setThemainproductprice(String themainproductprice) {
        this.themainproductprice = themainproductprice;
    }

    public String getPromotionactivity() {
        return promotionactivity;
    }

    public void setPromotionactivity(String promotionactivity) {
        this.promotionactivity = promotionactivity;
    }

    public String getAgencyrepertory() {
        return agencyrepertory;
    }

    public void setAgencyrepertory(String agencyrepertory) {
        this.agencyrepertory = agencyrepertory;
    }
}
