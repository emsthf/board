package com.example.board.dto.response;

import com.example.board.dto.ArticleWithCommentsDto;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        String hashtag,
        String userId,
        String email,
        String nickname,
        Set<ArticleCommentResponse> articleCommentsResponses,
        LocalDateTime createdAt
) {

    public static ArticleWithCommentsResponse of(
            Long id,
            String title,
            String content,
            String hashtag,
            String userId,
            String email,
            String nickname,
            Set<ArticleCommentResponse> articleCommentsResponses,
            LocalDateTime createdAt
    ) {
        return new ArticleWithCommentsResponse(id,
                title,
                content,
                hashtag,
                userId,
                email,
                nickname,
                articleCommentsResponses,
                createdAt);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtag(),
                dto.userAccountDto().userId(),
                dto.userAccountDto().email(),
                nickname,
                dto.articleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(Collectors.toCollection(LinkedHashSet::new)),
                dto.createdAt()
        );
    }
}
