package com.greenbox.shop.domain.board;

import com.greenbox.shop.domain.member.Member;
import com.greenbox.shop.domain.product.Product;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Qna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qna_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 1)
    private String secretYn = "N";

    @Column(columnDefinition = "TEXT")
    private String answer;

    private LocalDateTime answeredAt;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static Qna create(Product product, Member member, String title, String content, boolean secret) {
        Qna q = new Qna();
        q.product = product;
        q.member = member;
        q.title = title;
        q.content = content;
        q.secretYn = secret ? "Y" : "N";
        return q;
    }

    public void answer(String answer) {
        this.answer = answer;
        this.answeredAt = LocalDateTime.now();
    }

    public boolean isSecret() {
        return "Y".equals(secretYn);
    }

    public boolean isAnswered() {
        return answer != null && !answer.isBlank();
    }

    /** 비밀글은 작성자와 관리자만 열람 */
    public boolean canRead(Long memberId, boolean admin) {
        return !isSecret() || admin || (memberId != null && memberId.equals(member.getId()));
    }
}
