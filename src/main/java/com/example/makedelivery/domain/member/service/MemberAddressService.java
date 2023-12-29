package com.example.makedelivery.domain.member.service;

import com.example.makedelivery.common.exception.ApiException;
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

    @Transactional
    public void addAddress(Member member, MemberAddressRequest request) {
        MemberAddress address = MemberAddressRequest.toEntity(request, member.getId());
        Long addressId = memberAddressRepository.save(address).getId();
        // 아직 메인 주소가 등록되어있지 않다면, 자동으로 메인 주소를 등록해줍니다.
        if (member.getMainAddressId() == null) member.updateMainAddress(addressId, LocalDateTime.now());
    }

    @Transactional
    public void updateMainAddress(Member member, Long addressId) {
        member.updateMainAddress(addressId, LocalDateTime.now());
    }

    @Transactional
    public void deleteAddress(Member member, Long addressId) {

        if (Objects.equals(member.getMainAddressId(), addressId)) throw new ApiException(MAIN_ADDR_DELETE); // 메인 주소는 삭제할 수 없습니다.

        MemberAddress memberAddress = memberAddressRepository
                .findMemberAddressesByIdAndMemberId(addressId, member.getId())
                .orElseThrow(() -> new ApiException(ADDR_NOT_FOUND));

        memberAddress.deleteAddress(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<MemberAddressResponse> getMyAddressList(Member member) {
        // Status 가 DEFAULT 인 주소만 불러옵니다.
        return memberAddressRepository
                .findAllByMemberIdAndStatus(member.getId(), MemberAddress.Status.DEFAULT)
                .orElse(List.of())
                .stream()
                .map(MemberAddressResponse::toMemberAddressResponse)
                .toList();
    }

    /**
     * 처음 주소 삭제시에는 Status 가 Deleted 상태가 됩니다. ( 현재 배달 중 및 서비스 로직의 문제가 생기지 않게 하기 위해서 )
     * 이 후 스케쥴러로 Status 가 Deleted 상태인 주소들은 DB 에서 삭제합니다.
     * 해당 서비스 로직은 스케쥴러에서 호출합니다.
     */
    @Transactional
    public void deleteAllAddressDeleteStatus() {
        memberAddressRepository.deleteAllByStatus(MemberAddress.Status.DELETED);
    }

}
