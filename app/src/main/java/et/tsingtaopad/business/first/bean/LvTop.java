package et.tsingtaopad.business.first.bean;

/**
 * Created by yangwenmin on 2018/4/8.
 */

public class LvTop {

    private String ranking;// 排名
    private String rankingname;// 排行name
    private String rankingnum;// 当前个数
    private String rankingall;// 总数
    private String unit;// 单位

    public LvTop() {
    }

    public LvTop(String ranking, String rankingname, String rankingnum, String rankingall, String unit) {
        this.ranking = ranking;
        this.rankingname = rankingname;
        this.rankingnum = rankingnum;
        this.rankingall = rankingall;
        this.unit = unit;
    }

    public String getRanking() {
        return ranking;
    }

    public void setRanking(String ranking) {
        this.ranking = ranking;
    }

    public String getRankingname() {
        return rankingname;
    }

    public void setRankingname(String rankingname) {
        this.rankingname = rankingname;
    }

    public String getRankingnum() {
        return rankingnum;
    }

    public void setRankingnum(String rankingnum) {
        this.rankingnum = rankingnum;
    }

    public String getRankingall() {
        return rankingall;
    }

    public void setRankingall(String rankingall) {
        this.rankingall = rankingall;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
