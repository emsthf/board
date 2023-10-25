package com.example.board.service;

import com.example.board.domain.Article;
import com.example.board.domain.type.SearchType;
import com.example.board.dto.ArticleDto;
import com.example.board.dto.ArticleUpdateDto;
import com.example.board.repository.ArticleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

/**
 * 서비스 테스트는 스프링부트의 슬라이스 테스트 기능을 사용하지 않고 작성하는 방법으로 접근한다.
 * 그렇게 함으로써 스프링부트 애플리케이션 컨텍스트가 뜨는데 걸리는 시간을 없애서 가벼워 지는 것.
 *
 * 디펜던시가 필요하거나 하면 Mockito 라이브러리로 Mocking을 하는 방식으로 접근
 */
@DisplayName("비즈니스 로직 - 게시글")
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    @InjectMocks  // @InjectMocks은 mock을 주입하는 대상이라는 뜻
    private ArticleService sut;  // System under test의 약자. 테스트의 대상을 뜻한다.
    @Mock  // 그외 나머지 모든 mock
    private ArticleRepository articleRepository;

    @DisplayName("게시글을 검색하면, 게시글 리스트를 반환한다")
    @Test
    void givenSearchParameters_whenSearchArticles_thenReturnArticles() {
        // given

        // when
        Page<ArticleDto> articles = sut.searchArticles(SearchType.TITLE, "search keyword");

        // then
        assertThat(articles).isNotNull();
    }

    @DisplayName("게시글을 조회하면, 게시글을 반환한다")
    @Test
    void givenArticleId_whenSearchArticle_thenReturnArticle() {
        // given

        // when
        ArticleDto article = sut.searchArticles(1L);

        // then
        assertThat(article).isNotNull();
    }

    @DisplayName("게시글 정보를 입력하면, 게시글을 생성한다")
    @Test
    void givenArticleInfo_whenSavingArticle_thenSavesArticle() {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null);  // 실제로 저장이 잘 됬는지 Mock을 이용해서 확인해야 함

        // when
        sut.saveArticle(ArticleDto.of(LocalDateTime.now(), "Sol", "title", "content", "#spring"));

        // then
        then(articleRepository).should().save(any(Article.class));  // articleRepository의 save() 메서드가 호출되었는지 확인
        /*
          이런 식으로 Mocking을 이용해 테스트를 할 수 있다. 그러나 Mocking을 이용하면 테스트가 통과하더라도 실제로는 제대로 동작하지 않을 수도 있다.
          DB에 직접 데이터를 넣으면서 테스트 하는 것이 아니기 때문이다. DB 레이어까지 내려가면서 테스트를 진행하게 되면 이것은 더 이상 온전한 유닛 테스트가 아니게 된다.
         */
    }

    @DisplayName("게시글 ID와 수정 정보를 입력하면, 게시글을 수정한다")
    @Test
    void givenArticleIdAndModifiedInfo_whenUpdatingArticle_thenUpdatesArticle() {
        // given
        given(articleRepository.save(any(Article.class))).willReturn(null);

        // when
        sut.updateArticle(1L, ArticleUpdateDto.of("title", "content", "#spring"));

        // then
        then(articleRepository).should().save(any(Article.class));
    }
    
    @DisplayName("게시글 ID를 입력하면, 게시글을 삭제한다")
    @Test
    void givenArticleId_whenDeletingArticle_thenDeletesArticle() {
        // given
        willDoNothing().given(articleRepository).delete(any(Article.class));

        // when
        sut.deleteArticle(1L);

        // then
        then(articleRepository).should().delete(any(Article.class));
    }
}
