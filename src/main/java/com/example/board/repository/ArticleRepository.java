package com.example.board.repository;

import com.example.board.domain.Article;
import com.example.board.domain.QArticle;
import com.example.board.repository.querydsl.ArticleRepositoryCustom;
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
        ArticleRepositoryCustom, // QuerydslRepositorySupport를 상속받은 클래스를 사용하기 위해 추가
        QuerydslPredicateExecutor<Article>,  // 엔티티 안에 있는 모든 필드에 대한 검색기능을 추가해준다.
        QuerydslBinderCustomizer<QArticle> {  // QuerydslBinderCustomizer에는 QClass를 넣어줘야 한다.

    // JPA의 네임드 쿼리에선 원래는 Containing에 IgnoreCase를 붙여줘야 대소문자를 구분하지 않고 조회해오는데 현재 사용중인 MySQL 특성 상 DB에서 대소문자를 구분하지 않고 조회하기 때문에 IgnoreCase를 붙여주지 않아도 대소문자를 구분하지 않고 검색 작동한다.
    // (대부분의 RDBMS는 기본적으로 대소문자를 구분하지 않는 검색을 수행한다. 다만 애플리케이션에서 해당 검색이 어떻게 작동하는지 의도를 알리기 위해 명시해주는 것이 좋다.)
    Page<Article> findByTitleContaining(String title, Pageable pageable);  // containing은 like '%${}%'와 같다.
    Page<Article> findByContentContaining(String content, Pageable pageable);
    Page<Article> findByUserAccount_UserIdContaining(String userId, Pageable pageable);
    Page<Article> findByUserAccount_NicknameContaining(String nickname, Pageable pageable);

    void deleteByIdAndUserAccount_UserId(Long articleId, String userId);

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);  // 기본적으로 제공하는 검색기능을 제외하고 싶을 때 사용
        bindings.including(root.title, root.content, root.hashtags, root.createdAt, root.createdBy);  // 검색으로 필터링 하고 싶은 컬럼
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // 부분 검색을 위한 like '%${}%' 처리
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);  // 동일 검사(시분초 모두 일치해야 함)
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
