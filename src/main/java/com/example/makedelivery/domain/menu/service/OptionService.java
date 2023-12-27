package com.example.makedelivery.domain.menu.service;

import com.example.makedelivery.common.exception.ApiException;
import com.example.makedelivery.common.exception.ExceptionEnum;
import com.example.makedelivery.domain.menu.domain.OptionRequest;
import com.example.makedelivery.domain.menu.domain.OptionResponse;
import com.example.makedelivery.domain.menu.domain.entity.Option;
import com.example.makedelivery.domain.menu.domain.entity.Option.Status;
import com.example.makedelivery.domain.menu.repository.OptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;

    /**
     * 에러를 반환하는 공통 코드를 하나의 함수로 통일하였습니다. ( 중복되므로 )
     */
    private Option findOptionByIdAndMenuId(Long optionId, Long menuId) {
        return optionRepository.findOptionByIdAndMenuId(optionId, menuId)
                .orElseThrow(() -> new ApiException(ExceptionEnum.OPTN_NOT_FOUND));
    }

    @Transactional
    public void addOption(OptionRequest request, Long menuId) {
        Option option = OptionRequest.toEntity(request, menuId);
        optionRepository.save(option);
    }

    @Transactional
    public void updateOption(OptionRequest request, Long menuId, Long optionId) {
        Option option = findOptionByIdAndMenuId(optionId, menuId);
        option.updateOptionInfo(request.getName(),
                                request.getPrice(),
                                LocalDateTime.now());
    }

    @Transactional
    public void deleteOption(Long menuId, Long optionId) {
        Option option = findOptionByIdAndMenuId(optionId, menuId);
        option.deleteMenu(LocalDateTime.now());
    }

    @Transactional
    public void changeStatusHidden(Long menuId, Long optionId) {
        Option option = findOptionByIdAndMenuId(optionId, menuId);
        option.changeStatusHidden(LocalDateTime.now());
    }

    @Transactional
    public void changeStatusDefault(Long menuId, Long optionId) {
        Option option = findOptionByIdAndMenuId(optionId, menuId);
        option.changeStatusDefault(LocalDateTime.now());
    }

    @Transactional(readOnly = true)
    public List<OptionResponse> getMenuOptions(Long menuId) {
        return optionRepository.findAllByMenuIdAndStatusOrderByName(menuId, Status.DEFAULT)
                .orElse(List.of())
                .stream()
                .map(OptionResponse::toOptionResponse)
                .toList();
    }

    @Transactional
    public void deleteAllOptionDeleteStatus() {
        optionRepository.deleteAllByStatus(Status.DELETED);
    }

}
