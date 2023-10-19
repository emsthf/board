package com.example.board.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString
@Table(indexes = {  // 테이블 인덱스 설정
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@EntityListeners(AuditingEntityListener.class)  // JPA Auditing을 사용하기 위해 설정
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter  //@Setter 어노테이션을 엔티티 자체가 아니라 특정 필드에만 붙여 해당 필드만 setter 사용이 가능하게 할 수 있다.
    @Column(nullable = false)
    private String title;  // 제목

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;  // 본문

    @Setter
    private String hashtag;  // 해시태그

    @ToString.Exclude  // ArticleComment의 toString()에서 Article을 출력하지 않도록 설정(순환 참조 방지)
    @OrderBy("id")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)  // 양방향 바인딩 연관관계의 주인. 실무에서는 양방향 관계를 일부러 풀어서 단방향으로 사용한다(양방향 관계는 서로 강하게 결합이 되어 있어서 데이터 마이그레이션이나 편집을 할 때 불편함이 크다. 원치 않은 데이터 손실이 일어날 수도 있고.)
    private final Set<ArticleComment> articleComments = new HashSet<>();  // 게시글에 달린 댓글들. 연관관계 처리

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;  // 생성일시

    @CreatedBy
    @Column(nullable = false, length = 100)
    private String createdBy;  // 생성자

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;  // 수정일시

    @LastModifiedBy
    @Column(nullable = false, length = 100)
    private String modifiedBy;  // 수정자


    protected Article() {}  // JPA는 기본 생성자가 필요하다. 하지만 public으로 모두 열어줄 필요가 없음. 따라서 protected로 설정(private는 사용 불가)

    private Article(String title, String content, String hashtag) {  // 생성자를 private로 막고 아래 of 메서드로 생성하도록 함
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(String title, String content, String hashtag) {
        return new Article(title, content, hashtag);
    }

    // 동일한 것인지 비교를 위해서 Lombok의 @EqualsAndHashCode를 사용해도 되긴 하지만 좀 더 엔티티스럽게 구현하기 위해 equals와 hashCode를 오버라이드. 그리고 PK인 id만 비교하도록 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return id != null && Objects.equals(id, article.id);  // id가 아직 영속화되지 않았을 때에는 동등성 검사가 의미 없는 것으로 판단해서 id != null를 AND 조건으로 추가해 줌
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
