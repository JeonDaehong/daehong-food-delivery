package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.CartOptionRequest;
import com.example.makedelivery.domain.member.model.CartRequest;
import com.example.makedelivery.domain.member.model.CartResponse;
import com.example.makedelivery.domain.member.model.entity.Cart;
import com.example.makedelivery.domain.member.model.entity.CartOption;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.CartOptionRepository;
import com.example.makedelivery.domain.member.repository.CartRepository;
import com.example.makedelivery.domain.menu.domain.entity.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.makedelivery.common.exception.ExceptionEnum.CART_STORE_NOT_MATCHED;

@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartOptionRepository cartOptionRepository;

    private void validateCartStoreMatching(Long memberId, Long storeId) {
        if (cartRepository.countByMemberIdAndStoreIdIsNot(memberId, storeId) > 0) {
            throw new ApiException(CART_STORE_NOT_MATCHED);
        }
    }

    @Transactional
    public void addCart(Member member, CartRequest request) {
        // 장바구니에는 같은 매장의 메뉴만 담을 수 있습니다.
        validateCartStoreMatching(member.getId(), request.getStoreId());
        Long cartId = cartRepository.save(CartRequest.toEntity(request, member.getId())).getId();
        // 옵션이 있을 경우, 옵션도 장바구니에 담아줍니다.
        for ( CartOptionRequest cartOptionRequest : request.getOptionList() ) {
            CartOption cartOption = CartOptionRequest.toEntity(cartOptionRequest, cartId, member.getId());
            cartOptionRepository.save(cartOption);
        }
    }

    @Transactional(readOnly = true)
    public List<CartResponse> loadCart(Member member) {
        return cartRepository.findAllByMemberId(member.getId())
                .map(myCartList -> myCartList.stream()
                        .map(myCart -> {
                            CartResponse cartResponse = CartResponse.toCartResponse(myCart);
                            cartOptionRepository.findAllByMemberIdAndCartId(member.getId(), myCart.getId())
                                    .ifPresent(cartResponse::addOptionList);
                            return cartResponse;
                        })
                        .toList()
                )
                .orElse(List.of());
    }

    /**
     * 장바구니에서의 삭제는 다른 로직에 영향을 줄 일이 없기 때문에,
     * Status 변환 후 스케쥴러나 운영진에 의한 삭제보다
     * 즉각적인 DB 에서의 삭제가 서비스 성능면에서 좋을 거라고 생각하고 설계하였습니다.
     * 다만, 주의할 점은 Cart Option 이 남아있으면 안되기 때문에,
     * 해당 Cart 의 Option 들 먼저 삭제해 준 후, Cart 를 삭제해줍니다. ( CasCade )
     */
    @Transactional
    public void deleteCart(Member member, Long cartId) {
        cartOptionRepository.deleteAllByCartIdAndMemberId(cartId, member.getId());
        cartRepository.deleteByIdAndMemberId(cartId, member.getId());
    }

    @Transactional
    public void deleteCartAll(Member member) {
        cartOptionRepository.deleteAllByMemberId(member.getId());
        cartRepository.deleteAllByMemberId(member.getId());
    }

}
