package com.example.board.dto;

import com.example.board.domain.Hashtag;

import java.time.LocalDateTime;

public record HashTagDto(
        Long id,
        String hashtagName,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static HashTagDto of(String hashtagName) {
        return new HashTagDto(null, hashtagName, null, null, null, null);
    }

    public static HashTagDto of(
            Long id,
            String hashtagName,
            LocalDateTime createdAt,
            String createdBy,
            LocalDateTime modifiedAt,
            String modifiedBy
    ) {
        return new HashTagDto(id, hashtagName, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static HashTagDto from(Hashtag entity) {
        return new HashTagDto(
                entity.getId(),
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
