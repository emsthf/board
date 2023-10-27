package com.example.board.controller;

import com.example.board.config.SecurityConfig;
import com.example.board.dto.ArticleWithCommentsDto;
import com.example.board.dto.UserAccountDto;
import com.example.board.service.ArticleService;
import com.example.board.service.PaginationService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @WebMvcTest는 @Controller, @ControllerAdvice, @JsonComponent, Converter, GenericConverter, Filter, WebMvcConfigurer, HandlerMethodArgumentResolver를 스캔한다.
 * 하지만 해당 테스트에서는 모든 컨트롤러를 읽어들일 필요가 없으므로 ArticleController만 읽어들이도록 설정한다.
 */
@DisplayName("View 컨트롤러 - 게시글")
@Import(SecurityConfig.class)  // 기본 웹 시큐리티가 아닌 직접 구현한 SecurityConfig를 사용하도록 설정
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean  // @WebMvcTest의 컨트롤러 단에 의존하는 의존성 테스트를 위해 사용. 컨트롤러에 있는 의존성을 끊고 mocking 해준다.
    private ArticleService articleService;  // 왜 필드 주입을 했는가? JUnit5의 @WebMvcTest 내에는 @Autowired를 인지해서 생성자 주입을 할 수 있게 되었는데 이게 @MockBean에 대해선 구현이 안되어 있기 때문에 통일성을 위해 테스트 코드에선 모두 필드 주입을 사용하기로 결정함.

    @MockBean
    private PaginationService paginationService;

    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticlesView_thenReturnsArticlesView() throws Exception {
        // given
        given(articleService.searchArticles(eq(null), eq(null), any(Pageable.class))).willReturn(Page.empty());  // 필드의 일부분만 matcher를 사용할 수 없기 때문에 null이 들어가는 부분도 matcher를 사용해줘야 한다.
        given(paginationService.getPaginationBarNumbers(anyInt(), anyInt())).willReturn(List.of(0, 1, 2, 3, 4));  // getPaginationBarNumbers()의 인자는 primitive 타입인데 any()는 null도 허용한다는 뜻이기 때문에 사용하면 안된다. 따라서 anyInt()를 사용해야 한다.

        // when & then
        mvc.perform(get("/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))  // contentType()은 정확히 일치해야 하지만 contentTypeCompatibleWith()은 'text/html;charset=UTF-8'같이 옵션이 포함된 것도 인정된다.
                .andExpect(view().name("articles/index"))  // 뷰 이름 검증
                .andExpect(model().attributeExists("articles"))  // 내용 검증이 아닌 Model에 'articles'이라는 키가 있는지 확인
                .andExpect(model().attributeExists("paginationBarNumbers"));
        then(articleService).should().searchArticles(eq(null), eq(null), any(Pageable.class));  // 이러한 조건에서 동작을 하는지 검증. should()에는 '1회 호출'이라는 의미가 포함되어 있음
        then(paginationService).should().getPaginationBarNumbers(anyInt(), anyInt());
    }
    
    @DisplayName("[view][GET] 게시글 리스트 (게시판) 페이지 - 페이징, 정렬 기능")
    @Test
    void givenPagingAndSortingParams_whenSearchingArticlesPage_thenReturnsArticlesPage() throws Exception {
        // given
        String sortName = "title";
        String direction = "desc";
        int pageNumber = 0;
        int pageSize = 5;
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Order.desc(sortName)));
        List<Integer> barNumbers = List.of(1, 2, 3, 4, 5);
        given(articleService.searchArticles(null, null, pageable)).willReturn(Page.empty());
        given(paginationService.getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages())).willReturn(barNumbers);
        
        // when & then
        mvc.perform(
                        get("/articles")
                                .queryParam("page", String.valueOf(pageNumber))
                                .queryParam("size", String.valueOf(pageSize))
                                .queryParam("sort", sortName + "," + direction)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/index"))
                .andExpect(model().attributeExists("articles"))
                .andExpect(model().attribute("paginationBarNumbers", barNumbers));
        then(paginationService).should().getPaginationBarNumbers(pageable.getPageNumber(), Page.empty().getTotalPages());
    }

    @DisplayName("[view][GET] 게시글 상세 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleView_thenReturnsArticleView() throws Exception {
        // given
        long articleId = 1L;
        long totalCount = 1L;

        given(articleService.getArticle(articleId)).willReturn(createArticleWithCommentsDto());
        given(articleService.getArticleCount()).willReturn(totalCount);

        // when & then
        mvc.perform(get("/articles/" + articleId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(view().name("articles/detail"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attributeExists("articleComments"))
                .andExpect(model().attribute("totalCount", totalCount));
        then(articleService).should().getArticle(articleId);
        then(articleService).should().getArticleCount();
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 검색 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleSearchView_thenReturnsArticleSearchView() throws Exception {
        // given

        // when & then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search"));
    }

    @Disabled("구현 중")
    @DisplayName("[view][GET] 게시글 해시태그 검색 페이지 - 정상 호출")
    @Test
    void givenNothing_whenRequestingArticleHashtagSearchView_thenReturnsArticleHashtagSearchView() throws Exception {
        // given

        // when & then
        mvc.perform(get("/articles/search"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
                .andExpect(model().attributeExists("articles/search-hashtag"));
    }


    private ArticleWithCommentsDto createArticleWithCommentsDto() {
        return ArticleWithCommentsDto.of(
                1L,
                createUserAccountDto(),
                Set.of(),
                "title",
                "content",
                "#java",
                LocalDateTime.now(),
                "sol",
                LocalDateTime.now(),
                "sol"
        );
    }

    private UserAccountDto createUserAccountDto() {
        return UserAccountDto.of(
                1L,
                "sol",
                "pw",
                "sol@gmail.com",
                "Ssol",
                "iphone 14 pro owner",
                LocalDateTime.now(),
                "sol",
                LocalDateTime.now(),
                "sol"
        );
    }
}
