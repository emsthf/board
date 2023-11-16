package com.example.board.dto;

import com.example.board.domain.Hashtag;

import java.time.LocalDateTime;
import java.util.Set;

public record HashtagWithArticlesDto(
        Long id,
        Set<ArticleDto> articles,
        String hashtagName,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static HashtagWithArticlesDto of(Set<ArticleDto> articles, String hashtagName) {
        return new HashtagWithArticlesDto(
                null,
                articles,
                hashtagName,
                null,
                null,
                null,
                null
        );
    }

    public static HashtagWithArticlesDto of(
            Long id,
            Set<ArticleDto> artcles,
            String hashtagName,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new HashtagWithArticlesDto(
                id,
                artcles,
                hashtagName,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

    public static HashtagWithArticlesDto from(Hashtag entity) {
        return new HashtagWithArticlesDto(
                entity.getId(),
                entity.getArticles().stream()
                        .map(ArticleDto::from)
                        .collect(java.util.stream.Collectors.toUnmodifiableSet()),
                entity.getHashtagName(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Hashtag toEntity() {
        return Hashtag.of(hashtagName);
    }
}
