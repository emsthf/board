package com.example.board.dto.response;

import com.example.board.dto.ArticleDto;
import com.example.board.dto.HashTagDto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        String email,
        String nickname,
        LocalDateTime createdAt
) {

    public static ArticleResponse of(Long id,
                                     String title,
                                     String content,
                                     Set<String> hashtags,
                                     String email,
                                     String nickname,
                                     LocalDateTime createdAt) {
        return new ArticleResponse(id, title, content, hashtags, email, nickname, createdAt);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userAccountDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userAccountDto().userId();
        }

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashTagDto::hashtagName)
                        .collect(Collectors.toUnmodifiableSet()),
                dto.userAccountDto().email(),
                nickname,
                dto.createdAt()
        );
    }
}
