package com.vj.lets.domain.article.service;

import com.vj.lets.domain.article.dto.Article;
import com.vj.lets.domain.article.dto.ArticleUpdateForm;
import com.vj.lets.domain.article.mapper.ArticleHistoryMapper;
import com.vj.lets.domain.article.mapper.ArticleMapper;
import com.vj.lets.domain.article.util.ArticleHistoryComment;
import com.vj.lets.domain.group.util.PageParams;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Article 서비스 구현체
 *
 * @author VJ특공대 이한솔
 * @since 2023-09-08 (금)
 */
@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {
    private final ArticleMapper articleMapper;
    private final ArticleHistoryMapper articleHistoryMapper;

    /**
     * 게시글 등록
     *
     * @param article 게시글
     */
    @Transactional
    @Override
    public void create(Article article) {
        articleMapper.create(article);
        articleHistoryMapper.create();
    }

    /**
     * 게시글 검색
     *
     * @param keyword 검색어
     * @return 검색된 게시글 목록
     */
    @Override
    public Article search(String keyword) {
        return articleMapper.search(keyword);
    }

    /**
     * 게시글 수정
     *
     * @param article 게시글
     */
    @Transactional
    @Override
    public void update(Article article) {
        articleMapper.update(article);
        articleHistoryMapper.createByUpdate(article.getId(), ArticleHistoryComment.UPDATE.getComment());
    }

    /**
     * 게시글 정보 수정 시 수정 정보
     *
     * @param articleId 게시글 id
     * @return 체크 용 폼 객체
     */
    @Override
    public ArticleUpdateForm checkEdit(int articleId) {
        return articleMapper.readUpdateForm(articleId);
    }

    /**
     * 게시글 삭제
     *
     * @param articleId 게시글 번호
     */
    @Transactional
    @Override
    public void delete(int articleId) {
        articleMapper.delete(articleId);
        articleHistoryMapper.createByUpdate(articleId, ArticleHistoryComment.DELETE.getComment());
    }

    /**
     * 게시글 목록 (검색값 , 페이징 정보 처리 포함)
     *
     * @param pageParams 페이징 정보
     * @param groupId    스터디 그룹 ID
     * @return 게시글 목록
     */
    @Override
    public List<Map<String, Object>> findByPage(PageParams pageParams, int groupId) {
        return articleMapper.findByPage(pageParams, groupId);
    }

    /**
     * 페이징 정보 처리(검색 값 포함)에 필요한 게시글의 갯수
     *
     * @param keyword 검색어
     * @param groupId 스터디 그룹 ID
     * @return 검색된 게시글 갯수
     */
    @Override
    public int getCountAll(String keyword, int groupId) {
        return articleMapper.getCountAll(keyword, groupId);
    }

    /**
     * 게시글 번호로 해당 게시글 찾기
     *
     * @param id 게시글 번호
     * @return 해당 게시글
     */
    @Override
    public Article findById(int id) {
        return articleMapper.findById(id);
    }

    /**
     * 게시글 수정을위한 게시글번호로 게시글 찾기
     *
     * @param articleId 게시글 ID
     * @return 해당 게시글
     */
    @Override
    public ArticleUpdateForm findArticle(int articleId) {
        return articleMapper.findArticle(articleId);
    }

    /**
     * 해당 게시글의 번호로 댓글들 검색
     *
     * @param articleIds 해당 페이징 정보에 나오는 게시글 번호들
     * @return 댓글 목록
     */
    @Override
    public List<Map<String, Object>> findComment(List<Integer> articleIds) {
        return articleMapper.findComment(articleIds);
    }

    /**
     * 최근 게시글 목록
     *
     * @param id 스터디 그룹 ID
     * @return 최근 게시글 목록 (3개까지)
     */
    @Override
    public List<Article> getRecentArticles(@Param("groupId") int id) {
        return articleMapper.getRecentArticles(id);
    }
}
