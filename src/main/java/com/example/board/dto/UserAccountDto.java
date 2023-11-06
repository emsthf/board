package com.example.board.dto;

import com.example.board.domain.UserAccount;

import java.time.LocalDateTime;

public record UserAccountDto(
        String userId,
        String userPassword,
        String email,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    // 영속 상태가 아닐 때(save를 하려할 때)에는 Auditing 값은 없으니 null로 설정해주는 팩토리 메서드
    public static UserAccountDto of(String userId,
                                    String userPassword,
                                    String email,
                                    String nickname,
                                    String memo) {
        return new UserAccountDto(
                userId,
                userPassword,
                email,
                nickname,
                memo,
                null,
                null,
                null,
                null
        );
    }

    public static UserAccountDto of(String userId,
                                    String userPassword,
                                    String email,
                                    String nickname,
                                    String memo,
                                    LocalDateTime createdAt,
                                    String createdBy,
                                    LocalDateTime modifiedAt,
                                    String modifiedBy) {
        return new UserAccountDto(
                userId,
                userPassword,
                email,
                nickname,
                memo,
                createdAt,
                createdBy,
                modifiedAt,
                modifiedBy
        );
    }

    public static UserAccountDto from(UserAccount entity) {
        return new UserAccountDto(
                entity.getUserId(),
                entity.getUserPassword(),
                entity.getEmail(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public UserAccount toEntity() {
        return UserAccount.of(
                userId,
                userPassword,
                email,
                nickname,
                memo
        );
    }
}
