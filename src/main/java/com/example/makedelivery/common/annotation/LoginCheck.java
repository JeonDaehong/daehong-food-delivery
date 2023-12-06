package com.example.makedelivery.common.annotation;

import java.lang.annotation.*;

/**
 * Method위에 적용할 것이기 때문에 Target에는 Method를 적용하고
 * SPRING AOP 는 Run Time에 Weaving을 해주기 때문에 Retention은 Runtime으로 지정했습니다.
 * <br><br>
 * 사실 프록시 객체는 런타임에 생성하지만 런타임에 그 프록시를 생성할 타겟 빈을 찾을때 클래스정보(바이트 코드)를 참고하기 때문에
 * 런타임까지 해당 어노테이션을 유지할 필요는 없습니다.
 * Retention을 클래스까지로해도 똑같이 AOP적용이 가능합니다
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoginCheck {
    MemberLevel memberLevel();

    enum MemberLevel {

        MEMBER, OWNER, RIDER;

        public static MemberLevel memberLevel(String level) {
            return Enum.valueOf(MemberLevel.class, level);
        }
    }
}
