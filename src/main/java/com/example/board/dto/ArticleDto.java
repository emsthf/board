package com.example.board.dto;

import com.example.board.domain.Article;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserAccountDto userAccountDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleDto of(Long id,
                                UserAccountDto userAccountDto,
                                String title,
                                String content,
                                String hashtag,
                                LocalDateTime createdAt,
                                String createdBy,
                                LocalDateTime modifiedAt,
                                String modifiedBy) {
        return new ArticleDto(id,
                userAccountDto,
                title,
                content,
                hashtag,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy);
    }

    /**
     * 엔티티를 입력하면 DTO로 변환하여 반환한다.
     * 이 메서드가 존재함으로써 Article 엔티티는 DTO의 존재를 몰라도 된다.
     * 엔티티에는 DTO와의 연관관계가 없이 도메인 로직만 존재하면 되기 때문이다.
     * 이렇게 함으로써 엔티티에 변화가 생기면 DTO에 영향을 주겠지만, DTO엔 변화가 생겨도 엔티티에 영향을 주지 않는다.
     */
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserAccountDto.from(entity.getUserAccount()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    /**
     * DTO로 부터 엔티티를 만들어낸다.
     * 이 메서드가 존재함으로써 Article 엔티티는 DTO의 존재를 몰라도 된다.
     * 엔티티에는 DTO와의 연관관계가 없이 도메인 로직만 존재하면 되기 때문이다.
     * 이렇게 함으로써 엔티티에 변화가 생기면 DTO에 영향을 주겠지만, DTO엔 변화가 생겨도 엔티티에 영향을 주지 않는다.
     */
    public Article toEntity() {
        return Article.of(
                userAccountDto.toEntity(),
                title,
                content,
                hashtag
        );
    }
}