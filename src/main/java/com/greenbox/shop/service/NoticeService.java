package com.greenbox.shop.service;

import com.greenbox.shop.domain.board.Notice;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final MemberRepository memberRepository;

    /** F-20 공지사항 목록 */
    public Page<Notice> list(int page) {
        return noticeRepository.findAllByOrderByIdDesc(PageRequest.of(page, 10));
    }

    public Notice get(Long id) {
        return noticeRepository.findById(id).orElseThrow(() -> new NotFoundException("공지사항을 찾을 수 없습니다."));
    }

    @Transactional
    public Notice view(Long id) {
        Notice n = get(id);
        n.increaseViewCount();
        return n;
    }

    @Transactional
    public Long save(Long adminId, Long id, String title, String content) {
        if (title == null || title.isBlank() || content == null || content.isBlank()) {
            throw new BusinessException("제목과 내용을 입력하세요.");
        }
        if (id == null) {
            return noticeRepository.save(Notice.create(memberRepository.getReferenceById(adminId), title, content)).getId();
        }
        Notice n = get(id);
        n.update(title, content);
        return n.getId();
    }

    @Transactional
    public void delete(Long id) {
        noticeRepository.delete(get(id));
    }
}
