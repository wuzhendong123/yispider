package org.yi.spider.entity;


import lombok.Data;
import org.yi.spider.enums.ChapterExtEnum;

@Data
public class ChapterExtEntity extends BaseEntity implements Cloneable{

	/**章节序号**/
	private Integer chapterNo;
	/**章节名**/
    private String chapterName;

    private String type;
    private String content;


    public void padd(String content,ChapterExtEnum chapterExtEnum){
        this.setContent(content);
        this.setType(chapterExtEnum.name());
    }

   public  static ChapterExtEntity bulid(ChapterEntity chapterEntity ,String content,ChapterExtEnum chapterExtEnum){

        ChapterExtEntity chapterExtEntity=new ChapterExtEntity();
        chapterExtEntity.setNovelName(chapterEntity.getNovelName());
        chapterExtEntity.setNovelNo(chapterEntity.getNovelNo());
        chapterExtEntity.setChapterName(chapterEntity.getChapterName());
        chapterExtEntity.setChapterNo(chapterEntity.getChapterNo());
        chapterExtEntity.setContent(content);
        chapterExtEntity.setType(chapterExtEnum.name());
        return  chapterExtEntity;
    }
    public  static ChapterExtEntity bulid(NovelEntity novelEntity ,String content,ChapterExtEnum chapterExtEnum){

        ChapterExtEntity chapterExtEntity=new ChapterExtEntity();
        chapterExtEntity.setNovelName(novelEntity.getNovelName());
        chapterExtEntity.setNovelNo(novelEntity.getNovelNo());
        chapterExtEntity.setChapterNo(novelEntity.getNovelNoInteger());
        chapterExtEntity.setContent(content);
        chapterExtEntity.setType(chapterExtEnum.name());
        return  chapterExtEntity;
    }
}
