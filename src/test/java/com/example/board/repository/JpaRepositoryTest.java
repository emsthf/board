package com.example.board.repository;

import com.example.board.domain.Article;
import com.example.board.domain.Hashtag;
import com.example.board.domain.UserAccount;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JPA 연결 테스트")
@Import(JpaRepositoryTest.TestJpaConfig.class)  // JpaConfig(Auditing)를 테스트 환경에 적용 -> 테스트 환경에 맞는 테스트 전용 JpaConfig를 만들어서 사용
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserAccountRepository userAccountRepository;
    private final HashtagRepository hashtagRepository;

    public JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                             @Autowired ArticleCommentRepository articleCommentRepository,
                             @Autowired UserAccountRepository userAccountRepository,
                             @Autowired HashtagRepository hashtagRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userAccountRepository = userAccountRepository;
        this.hashtagRepository = hashtagRepository;
    }

    @DisplayName("select 테스트")
    @Test
    void givenTestData_whenSelecting_thenWorksFine() {
        // given

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles)
                .isNotNull()
                .hasSize(123);  // classpath:resources/data.sql 참조
    }

    @DisplayName("insert 테스트")
    @Test
    void givenTestData_whenInserting_thenWorksFine() {
        // given
        long previousCount = articleRepository.count();
        UserAccount userAccount = userAccountRepository.save(UserAccount.of("timcook", "pw", null, null, null));
        Article article = Article.of(userAccount, "new Article", "new Content");
        article.addHashtags(Set.of(Hashtag.of("spring")));

        // when
        articleRepository.save(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @DisplayName("update 테스트")
    @Test
    void givenTestData_whenUpdating_thenWorksFine() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        Hashtag updatedHashtag = Hashtag.of("#springboot");
        article.clearHashtags();
        article.addHashtags(Set.of(updatedHashtag));

        // when
        Article savedArticle = articleRepository.saveAndFlush(article);  // saveAndFlush()는 save()와 달리 즉시 DB에 반영한다.

        // then
        assertThat(savedArticle.getHashtags())
                .hasSize(1)
                .extracting("hashtagName", String.class)
                .containsExactly(updatedHashtag.getHashtagName());
    }

    @DisplayName("delete 테스트")
    @Test
    void givenTestData_whenDeleting_thenWorksFine() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentSize = article.getArticleComments().size();

        // when
        articleRepository.delete(article);

        // then - 글과 연관된 댓글도 삭제되는지 확인
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentSize);
    }

    @DisplayName("[QueryDSL] 전체 hashtag 리스트에서 이름만 조회하기")
    @Test
    void givenNothing_whenQueryingHashtags_thenReturnsHashtagNames() {
        // given
        
        // when
        List<String> hashtagNames = hashtagRepository.findAllHashtagNames();
        
        // then
        assertThat(hashtagNames).hasSize(19);
    }

    @DisplayName("[QueryDSL] hashtag로 페이징 된 게시글 검색하기")
    @Test
    void givenHashtagNamesAndPageable_whenQueryingArticles_thenReturnsArticlePage() throws Exception {
        // given
        List<String> hashtagNames = List.of("blue", "crimson", "fuscia");
        Pageable pageable = PageRequest.of(0, 5, Sort.by(
                Sort.Order.desc("hashtags.hashtagName"),
                Sort.Order.asc("title")
        ));

        // when
        Page<Article> articlePage = articleRepository.findByHashtagNames(hashtagNames, pageable);

        // then
        assertThat(articlePage.getContent()).hasSize(pageable.getPageSize());
        assertThat(articlePage.getContent().get(0).getTitle()).isEqualTo("Fusce posuere felis sed lacus.");
        assertThat(articlePage.getContent().get(0).getHashtags())
                .extracting("hashtagName", String.class)
                .containsExactly("fuscia");
        assertThat(articlePage.getTotalElements()).isEqualTo(17);
        assertThat(articlePage.getTotalPages()).isEqualTo(4);
    }


    // JpaAuditConfig에 시큐리티 인증 유저 로직을 붙이니 JpaRepositoryTest에서 insert 테스트 코드 실행 시 createdBy가 null로 들어가는 문제가 발생
    // = JpaAuditConfig는 UserAccountRepository를 사용해서 유저를 가져오게 되어 있는데, 이 repository가 제대로 빈으로 등록되어 있지 않거나 회원 데이터가 들어가 있지 않으면 테스트를 실패하게 되는 것이다.
    // 이 문제를 해결하기 위한 테스트 전용 설정
    @EnableJpaAuditing
    @TestConfiguration  // 테스트 환경에서만 사용하도록 configuration 설정. 실제 애플리케이션 실행 시에는 빈 스캔 대상에 포함되지 않음
    public static class TestJpaConfig {  // 시큐리티와는 완전히 분리되어 동작

        @Bean
        public AuditorAware<String> auditorAware() {
            return () -> Optional.of("sol");  // 시큐리티 인증 유저와 상관 없이 강제로 특정 유저를 auditing에 넣어줌
        }
    }
}
