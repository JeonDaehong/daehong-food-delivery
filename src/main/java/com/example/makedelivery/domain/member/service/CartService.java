package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.exception.ExceptionEnum;
import com.example.makedelivery.domain.member.model.CartOptionRequest;
import com.example.makedelivery.domain.member.model.CartRequest;
import com.example.makedelivery.domain.member.model.CartResponse;
import com.example.makedelivery.domain.member.model.entity.Cart;
import com.example.makedelivery.domain.member.model.entity.CartOption;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.repository.CartOptionRepository;
import com.example.makedelivery.domain.member.repository.CartRepository;
import com.example.makedelivery.domain.menu.domain.entity.Menu;
import com.example.makedelivery.domain.menu.domain.entity.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.makedelivery.common.constants.CacheValueConstants.CART_LIST;
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

    /**
     * 요청한 메뉴, 옵션이 모두 일치한 메뉴가 장바구니에 담겨있는지
     * 확인해주는 메서드입니다.
     */
    private boolean isMenuAlreadyInCart(Member member, CartRequest request) {
        Optional<Cart> myCartOptional = cartRepository.findCartByMenuIdAndMemberId(request.getMenuId(), member.getId());

        return myCartOptional.filter(myCart -> {
                    long optionCount = request.getOptionList().stream()
                            .filter(optionRequest -> cartOptionRepository.findCartOptionByMenuOptionIdAndCartId(optionRequest.getMenuOptionId(), myCart.getId()).isPresent())
                            .count();
                    return optionCount == request.getOptionList().size();
                })
                .isPresent();
    }

    /**
     * 같은 메뉴+옵션이 모두 일치하는 장바구니가 없을 경우, 새롭게 추가해줍니다.
     */
    private void addNewCart(Member member, CartRequest request) {
        Cart cart = CartRequest.toEntity(request, member.getId());
        Long newCartId = cartRepository.save(cart).getId();
        request.getOptionList().forEach(optionRequest -> {
            CartOption cartOption = CartOptionRequest.toEntity(optionRequest, newCartId, member.getId());
            cartOptionRepository.save(cartOption);
        });
    }

    /**
     * 같은 메뉴+옵션이 모두 일치하는 메뉴가 담겨있을 경우 Count 를 1 상승 해줍니다.
     */
    private void increaseCartItemCount(Member member, CartRequest request) {
        Optional<Cart> cartOptional = cartRepository.findCartByMenuIdAndMemberId(request.getMenuId(), member.getId());
        cartOptional.ifPresent(Cart::countUp);
    }


    /**
     * 장바구니에 A 매장 메뉴가 들어가있으면,
     * 다른 매장 메뉴는 담을 수 없게 설계하고, 개발하였습니다.
     * 그 이유는, 한 번에 여러 매장에서 일괄 주문을 진행 할 경우
     * 장바구니에 있는 매장의 메뉴 상황이나, 매장의 현재 운영 상태가 하나라도 바뀌게 될 경우
     * 문제가 생길 수 있기 때문입니다.
     * 그러한 부분을 고려하였을 때, 매장별로 주문을 하는 것이
     * 고객 입장에서도 좋고, 프로그램 설계면에서도 좋다고 판단하였습니다.
     */
    @Transactional
    @CacheEvict(value = CART_LIST, key = "'member:' + #member.id", cacheManager = "redisCacheManager")
    public void addCart(Member member, CartRequest request) {
        validateCartStoreMatching(member.getId(), request.getStoreId()); // 장바구니에는 같은 매장의 메뉴만 담을 수 있습니다.
        if ( ! isMenuAlreadyInCart(member, request) ) {
            addNewCart(member, request);
        } else {
            increaseCartItemCount(member, request);
        }
    }

    @Cacheable(key = "'member:' + #member.id", value = CART_LIST, cacheManager = "redisCacheManager")
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
     * <br><br>
     * 또한 이렇게 장바구니에 변화가 생기면, 캐시에서 삭제 처리를 해줍니다.
     */
    @CacheEvict(value = CART_LIST, key = "'member:' + #member.id", cacheManager = "redisCacheManager")
    @Transactional
    public void deleteCart(Member member, Long cartId) {
        cartOptionRepository.deleteAllByCartIdAndMemberId(cartId, member.getId());
        cartRepository.deleteByIdAndMemberId(cartId, member.getId());
    }

    @CacheEvict(value = CART_LIST, key = "'member:' + #member.id", cacheManager = "redisCacheManager")
    @Transactional
    public void deleteCartAll(Member member) {
        cartOptionRepository.deleteAllByMemberId(member.getId());
        cartRepository.deleteAllByMemberId(member.getId());
    }

}
