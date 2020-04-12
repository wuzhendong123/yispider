package org.yi.spider.entity;


import lombok.Data;
import org.yi.spider.enums.SpiderLogEnum;

import java.util.Date;

@Data
public class SpiderLogEntity  implements Cloneable{

	/**章节位置**/
	private String cno;
	/**爬出规则文件**/
    private String spiderRulXml;

    private String url;
    private Date createTime;
    private Date updateTime;

    private String status;
    private String articleNo;

    private String cpm;
    private String novelNo;

    public static SpiderLogEntity build(String ruleName, Number articleNo,String url,String novelNo) {
        SpiderLogEntity spiderLogEntity=new SpiderLogEntity();
        spiderLogEntity.setArticleNo(articleNo.toString());
        spiderLogEntity.setSpiderRulXml(ruleName);
        spiderLogEntity.setStatus(SpiderLogEnum.INIT.name());
        spiderLogEntity.setUrl(url);
        spiderLogEntity.setNovelNo(novelNo);
        return spiderLogEntity;
    }
}
