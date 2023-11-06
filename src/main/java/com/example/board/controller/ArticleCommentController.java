package com.example.board.controller;

import com.example.board.dto.UserAccountDto;
import com.example.board.dto.request.ArticleCommentRequest;
import com.example.board.service.ArticleCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/comments")
@Controller
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    @PostMapping("/new")
    public String postNewArticleComment(ArticleCommentRequest articleCommentRequest) {
        // TODO: 11/6/23 인증 정보를 넣어줘야 한다
        articleCommentService.saveArticleComment(articleCommentRequest.toDto(UserAccountDto.of(
                "sol", "qwer1234", "sol@gmail.com", "Ssol", "user memo"
        )));

        return "redirect:/articles/" + articleCommentRequest.articleId();
    }

    // 삭제 요청인데도 @PostMapping을 사용하는 이유는 form-data는 기본적으로 get과 post만 허용하기 때문이다.
    // 물론 타임리프를 사용하면 put과 delete 맵핑을 하고 타임리프 코드에 메서드로 put, delete로 넣어주면 알아서 편법으로 변환해주기 때문에 사용이 가능하지만
    // http 표준 스펙을 지기키 위해서 form-data가 허용하는 메서드만 사용하였다.
    @PostMapping("/{commentId}/delete")
    public String deleteArticleComment(@PathVariable Long commentId, Long articleId) {
        articleCommentService.deleteArticleComment(commentId);

        return "redirect:/articles/" + articleId;
    }
}
