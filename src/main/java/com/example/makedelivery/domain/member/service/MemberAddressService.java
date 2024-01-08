package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.domain.member.model.MemberAddressPriorityRequest;
import com.example.makedelivery.domain.member.model.MemberAddressRequest;
import com.example.makedelivery.domain.member.model.MemberAddressResponse;
import com.example.makedelivery.domain.member.model.entity.Member;
import com.example.makedelivery.domain.member.model.entity.MemberAddress;
import com.example.makedelivery.domain.member.repository.MemberAddressRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.example.makedelivery.common.exception.ExceptionEnum.ADDR_NOT_FOUND;
import static com.example.makedelivery.common.exception.ExceptionEnum.MAIN_ADDR_DELETE;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberAddressService {

    private final MemberAddressRepository memberAddressRepository;

    private MemberAddress findMemberAddressByIdAndMemberIdAndStatus(Long addressId, Long memberId, MemberAddress.Status status) {
        return memberAddressRepository
                .findMemberAddressesByIdAndMemberIdAndStatus(addressId, memberId, status)
                .orElseThrow(() -> new ApiException(ADDR_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public MemberAddress findMemberAddressByIdAndMemberId(Long addressId, Long memberId) {
        return memberAddressRepository
                .findMemberAddressesByIdAndMemberId(addressId, memberId)
                .orElseThrow(() -> new ApiException(ADDR_NOT_FOUND));
    }

    @Transactional
    public void addAddress(Member member, MemberAddressRequest request) {

        // 해당 Member 가 가지고 있는 Address 의 순서중 가장 큰 값을 가져옵니다.
        // 주소가 하나도 없을 경우 0을 return 합니다.
        int maxPriority = memberAddressRepository.findMaxPriorityByStatusAndMemberId(MemberAddress.Status.DEFAULT, member.getId());

        MemberAddress address = MemberAddressRequest.toEntity(request, member.getId(), (maxPriority + 1));
        memberAddressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Member member, Long addressId) {
        MemberAddress memberAddress = findMemberAddressByIdAndMemberIdAndStatus(addressId, member.getId(), MemberAddress.Status.DEFAULT);
        memberAddress.deleteAddress();
    }

    @Transactional(readOnly = true)
    public List<MemberAddressResponse> getMyAddressList(Member member) {
        // Status 가 DEFAULT 인 주소를 Priority 순서로 불러옵니다.
        return memberAddressRepository
                .findAllByMemberIdAndStatusOrderByPriority(member.getId(), MemberAddress.Status.DEFAULT)
                .orElse(List.of())
                .stream()
                .map(MemberAddressResponse::toMemberAddressResponse)
                .toList();
    }

    @Transactional
    public void changeAddressPriority(Member member, List<MemberAddressPriorityRequest> requestList) {
        for ( MemberAddressPriorityRequest request : requestList ) {
            MemberAddress memberAddress = findMemberAddressByIdAndMemberIdAndStatus(request.getAddressId(), member.getId(), MemberAddress.Status.DEFAULT);
            memberAddress.changeAddressPriority(request.getPriority());

        }
    }

    /**
     * 처음 주소 삭제시에는 Status 가 Deleted 상태가 됩니다.
     * ( 현재 배달 중 및 서비스 로직의 문제가 생기지 않게 하기 위해서 )
     * 이 후 스케쥴러로 Status 가 Deleted 상태면서 24시간 이상이 지난 주소들은 DB 에서 삭제합니다.
     * ( 혹시나 타이밍 좋게 스케쥴러에 걸릴 수 있기 때문에 )
     * 해당 서비스 로직은 스케쥴러에서 호출합니다.
     */
    @Transactional
    public void deleteAllAddressDeleteStatus() {
        memberAddressRepository.deleteAllByStatusAndUpdateDateTime24Hour(MemberAddress.Status.DELETED,
                                                                         LocalDateTime.now().minusHours(24));
    }

}
