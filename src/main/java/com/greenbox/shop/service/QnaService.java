package com.greenbox.shop.service;

import com.greenbox.shop.domain.board.Qna;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.ProductRepository;
import com.greenbox.shop.repository.QnaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QnaService {

    private final QnaRepository qnaRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    /** F-19 상품 문의 등록 */
    @Transactional
    public Long ask(Long memberId, Long productId, String title, String content, boolean secret) {
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            throw new BusinessException("문의 제목과 내용을 입력하세요.");
        }
        Qna q = Qna.create(productRepository.getReferenceById(productId), memberRepository.getReferenceById(memberId),
                title, content, secret);
        return qnaRepository.save(q).getId();
    }

    public List<Qna> byProduct(Long productId) {
        return qnaRepository.findByProductIdOrderByIdDesc(productId);
    }

    // ===== 관리자 F-27 =====
    public Page<Qna> adminList(int page) {
        return qnaRepository.findAllByOrderByIdDesc(PageRequest.of(page, 10));
    }

    @Transactional
    public void answer(Long qnaId, String answer) {
        if (answer == null || answer.isBlank()) throw new BusinessException("답변 내용을 입력하세요.");
        qnaRepository.findById(qnaId).orElseThrow(() -> new NotFoundException("문의를 찾을 수 없습니다.")).answer(answer);
    }

    public long unansweredCount() {
        return qnaRepository.countByAnswerIsNull();
    }
}
