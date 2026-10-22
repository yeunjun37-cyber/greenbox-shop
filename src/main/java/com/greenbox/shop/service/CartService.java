package com.greenbox.shop.service;

import com.greenbox.shop.domain.cart.CartItem;
import com.greenbox.shop.domain.product.Product;
import com.greenbox.shop.dto.CartSummary;
import com.greenbox.shop.exception.BusinessException;
import com.greenbox.shop.exception.NotFoundException;
import com.greenbox.shop.repository.CartItemRepository;
import com.greenbox.shop.repository.MemberRepository;
import com.greenbox.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    /** F-12 장바구니 담기 - 같은 상품은 수량 합산 */
    @Transactional
    public Long add(Long memberId, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다."));
        if (!product.isOnSale()) {
            throw new BusinessException("현재 구매할 수 없는 상품입니다.");
        }
        CartItem item = cartItemRepository.findByMemberIdAndProductId(memberId, productId)
                .orElse(null);
        int newQty = (item == null ? 0 : item.getQuantity()) + Math.max(1, quantity);
        if (newQty > product.getStock()) {
            throw new BusinessException("재고가 부족합니다. (남은 재고: " + product.getStock() + "개)");
        }
        if (item == null) {
            item = cartItemRepository.save(CartItem.create(memberRepository.getReferenceById(memberId), product, Math.max(1, quantity)));
        } else {
            item.addQuantity(Math.max(1, quantity));
        }
        return item.getId();
    }

    /** F-13 장바구니 조회 */
    public CartSummary summary(Long memberId) {
        return CartSummary.of(cartItemRepository.findWithProductByMemberId(memberId));
    }

    public CartSummary summary(Long memberId, List<Long> cartItemIds) {
        List<CartItem> items = cartItemRepository.findWithProductByMemberId(memberId).stream()
                .filter(c -> cartItemIds.contains(c.getId())).toList();
        return CartSummary.of(items);
    }

    @Transactional
    public void changeQuantity(Long memberId, Long cartItemId, int quantity) {
        CartItem item = getOwned(memberId, cartItemId);
        if (quantity > item.getProduct().getStock()) {
            throw new BusinessException("재고가 부족합니다. (남은 재고: " + item.getProduct().getStock() + "개)");
        }
        item.changeQuantity(quantity);
    }

    @Transactional
    public void remove(Long memberId, List<Long> cartItemIds) {
        cartItemIds.forEach(id -> cartItemRepository.delete(getOwned(memberId, id)));
    }

    private CartItem getOwned(Long memberId, Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new NotFoundException("장바구니 상품을 찾을 수 없습니다."));
        if (!item.getMember().getId().equals(memberId)) {
            throw new BusinessException("본인의 장바구니만 수정할 수 있습니다.");
        }
        return item;
    }
}
