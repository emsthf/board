package com.example.board.repository;

import com.example.board.domain.Article;
import com.example.board.domain.QArticle;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        QuerydslPredicateExecutor<Article>,  // 엔티티 안에 있는 모든 필드에 대한 검색기능을 추가해준다.
        QuerydslBinderCustomizer<QArticle> {  // QuerydslBinderCustomizer에는 QClass를 넣어줘야 한다.

    Page<Article> findByTitleContaining(String title, Pageable pageable);  // containing은 like '%${}%'와 같다.
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);
    Page<Article> findByHashtag(String hashtag, Pageable pageable);

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);  // 기본적으로 제공하는 검색기능을 제외하고 싶을 때 사용
        bindings.including(root.title, root.content, root.hashtag, root.createdAt, root.createdBy);  // 검색으로 필터링 하고 싶은 컬럼
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // 부분 검색을 위한 like '%${}%' 처리
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtag).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);  // 동일 검사(시분초 모두 일치해야 함)
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
