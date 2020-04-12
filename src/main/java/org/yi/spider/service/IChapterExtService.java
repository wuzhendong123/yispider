package org.yi.spider.service;

import org.yi.spider.entity.ChapterExtEntity;
import org.yi.spider.enums.ChapterExtEnum;

import java.sql.SQLException;

public interface IChapterExtService {
     void saveChapter(ChapterExtEntity chapterExtEntity) throws SQLException;

    void updateChapter(ChapterExtEntity chapterExtEntity) throws SQLException;

    ChapterExtEntity findByChapterNo(Integer chapterNo,ChapterExtEnum type) throws SQLException;


}
