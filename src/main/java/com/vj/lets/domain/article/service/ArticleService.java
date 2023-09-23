package com.vj.lets.domain.article.service;


import com.vj.lets.domain.article.dto.Article;
import com.vj.lets.domain.article.dto.ArticleUpdateForm;
import com.vj.lets.domain.group.util.PageParams;
import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
* 클래스 설명 : Article 관련 로직 처리 및 관리
* 작성일 : 2023-09-08
* @author : 이한솔
*/
public interface ArticleService {

    /**
     * 게시글 등록
     * @param article : 게시글
     */
    public void create (Article article);

    /**
     * 게시글 검색
     * @param keyword : 검색어
     * @return : 검색된 게시글 목록
     */
    public Article search (@Param("keyword") String keyword);

    /**
     * 게시글 수정
     * @param article : 게시글
     */
    public void update (Article article);

    /**
     * 게시글 정보 수정 시 수정 정보
     *
     * @param articleId 게시글 id
     * @return 체크 용 폼 객체
     */
    public ArticleUpdateForm checkEdit(int articleId);

    /**
     * 게시글 삭제
     * @param articleId : 게시글 번호
     */
    @Transactional
    public void delete(int articleId);


    /**
     * 게시글 목록 (검색값 , 페이지처리 포함)
     * @param pageParams : 페이지
     * @return
     */
    public List<Map<String, Object>> findByPage (@Param("pageParams") PageParams pageParams, @Param("groupId")int groupId);

    /**
     * 페이지 처리(검색 값 포함)에 필요한 게시글의 갯수
     * @param keyword : 검색어
     * @return : 검색된 게시글 갯수
     */
    public int getCountAll(@Param("keyword") String keyword, @Param("groupId")int groupId);

    /**
     * 게시글 번호로 해당 게시글 찾기
     * @param articleId : 게시글 번호
     * @return : 해당 게시글
     */
    public Article findById(int articleId);

    /**
     * 게시글 수정을위한 게시글번호로 게시글 찾기
     * @param articleId : 게시글 ID
     * @return : 해당 게시글
     */
    public ArticleUpdateForm findArticle(int articleId);


    /**
     * 해당 게시글의 번호로 댓글들 검색
     * @param articleIds : 해당 페이지에 나오는 게시글 번호들
     * @return : 댓글 목록
     */
    public List<Map<String, Object>> findComment (List<Integer> articleIds);

    /**
     * 최근 게시글 목록 검색
     */
    public List<Article> getRecentArticles (@Param("groupId")int id);
}
