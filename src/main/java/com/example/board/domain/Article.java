package com.example.board.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)  // 상속받은 AuditingFields의 toString()까지 출력하도록 설정
@Table(indexes = {  // 테이블 인덱스 설정
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields {  // 공통 필드를 추출한 AuditingFields를 상속받음

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "userId")
    private UserAccount userAccount;

    @Setter  //@Setter 어노테이션을 엔티티 자체가 아니라 특정 필드에만 붙여 해당 필드만 setter 사용이 가능하게 할 수 있다.
    @Column(nullable = false)
    private String title;

    @Setter
    @Column(nullable = false, length = 10000)
    private String content;

    @Setter
    private String hashtag;

    @ToString.Exclude  // ArticleComment의 toString()에서 Article을 출력하지 않도록 설정(순환 참조 방지)
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)  // 양방향 바인딩 연관관계의 주인. 실무에서는 양방향 관계를 일부러 풀어서 단방향으로 사용한다(양방향 관계는 서로 강하게 결합이 되어 있어서 데이터 마이그레이션이나 편집을 할 때 불편함이 크다. 원치 않은 데이터 손실이 일어날 수도 있고.)
    private final Set<ArticleComment> articleComments = new HashSet<>();  // 게시글에 달린 댓글들. 연관관계 처리


    protected Article() {}  // JPA는 기본 생성자가 필요하다. 하지만 public으로 모두 열어줄 필요가 없음. 따라서 protected로 설정(private는 사용 불가)

    private Article(UserAccount userAccount, String title, String content, String hashtag) {  // 생성자를 private로 막고 아래 of 메서드로 생성하도록 함
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    public static Article of(UserAccount userAccount, String title, String content, String hashtag) {
        return new Article(userAccount, title, content, hashtag);
    }

    // 동일한 것인지 비교를 위해서 Lombok의 @EqualsAndHashCode를 사용해도 되긴 하지만 좀 더 엔티티스럽게 구현하기 위해 equals와 hashCode를 오버라이드. 그리고 PK인 id만 비교하도록 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article that)) return false;
        return this.getId() != null && Objects.equals(getId(), that.getId());  // id가 아직 영속화되지 않았을 때에는 동등성 검사가 의미 없는 것으로 판단해서 id != null를 AND 조건으로 추가해 줌.
        // JPA로 엔티티를 다룰 때, 엔티티 데이터는 하이버네이트 구현체가 만든 프록시 객체를 이용하여 지연 로딩될 수 있다.
        // 따라서 엔티티를 조회할 때 필드에 직접 접근하면 id == null인 상황이 있을 수 있고, 이러면 올바른 비교를 하지 못하게 된다. getter를 사용하면 이러한 문제를 예방할 수 있다.
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
