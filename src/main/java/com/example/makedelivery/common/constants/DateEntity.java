package com.example.makedelivery.common.constants;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 공통적으로 CreateDateTime 과 UpdateDateTime 을 관리해주며,
 * 해당 컬럼을 사용하는 모든 Entity 에게 공통적으로 적용됩니다.
 * SpringBoot 가 실행되는 Application 클래스에 @EnableJpaAuditing 을 적용하였고
 * 이로인해 @EntityListeners(AuditingEntityListener.class) 가 동작합니다.
 * EntityListeners 는 변화를 감지해주는 역할을 합니다.
 * <br><br>
 * 또한 @Temporal 이라는 어노테이션을 사용하여 TIMESTAMP 등을 사용하면 쉽게 관리할 수 있지만,
 * 이 어노테이션은 Date 나 Calendar 데이터 타입만 적용할 수 있습니다.
 * 해당 클래스는 가변타입에 Thread-Safe 하지도 않으므로
 * 현재는 LocalDateTime 을 사용합니다.
 * 그렇기 때문에 @Temporal 은 사용하지 않습니다.
 */
@Getter
@EntityListeners(AuditingEntityListener.class)
@MappedSuperclass
public class DateEntity {

    @CreatedDate // 처음 만들어질 떄 날짜가 자동 주입됩니다.
    @Column(name = "CRTE_DTTM", updatable = false) // Create 이기 때문에 수정 할 수 없습니다.
    private LocalDateTime createDateTime;

    @LastModifiedDate // 마지막으로 수정 된 날짜를 자동 주입해줍니다.
    @Column(name = "UPDT_DTTM", updatable = true) // Update 이기 때문에 수정 할 수 있습니다.
    private LocalDateTime updateDateTime;

}
