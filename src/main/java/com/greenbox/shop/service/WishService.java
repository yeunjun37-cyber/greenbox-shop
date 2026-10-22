package com.greenbox.shop.service;

import com.greenbox.shop.domain.product.Wishlist;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.ProductRepository;
import com.greenbox.shop.repository.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WishService {

    private final WishlistRepository wishlistRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /** F-11 찜 토글 @return 찜 상태 */
    @Transactional
    public boolean toggle(Long memberId, Long productId) {
        return wishlistRepository.findByMemberIdAndProductId(memberId, productId)
                .map(w -> {
                    wishlistRepository.delete(w);
                    return false;
                })
                .orElseGet(() -> {
                    wishlistRepository.save(Wishlist.create(memberRepository.getReferenceById(memberId),
                            productRepository.getReferenceById(productId)));
                    return true;
                });
    }

    public boolean isWished(Long memberId, Long productId) {
        return memberId != null && wishlistRepository.existsByMemberIdAndProductId(memberId, productId);
    }

    public List<Wishlist> list(Long memberId) {
        return wishlistRepository.findByMemberIdOrderByIdDesc(memberId);
    }
}
