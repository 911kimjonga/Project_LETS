package com.vj.lets.domain.article.service;


import com.vj.lets.domain.article.dto.ArticleComment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* 클래스 설명 : ArticleComment - 답글 관련 로직 처리 및 관리
* 작성일 : 2023-09-14
* @author : 이한솔
*/
public interface ArticleCommentService {
    /** 댓글 생성*/
    public void create (ArticleComment articleComment, @Param("articleId")int id);

    /** 댓글 삭제*/
    public void delete (int id);

    /** 댓글 목록*/
    public List<ArticleComment> findCommentAll ();
}