# Daehong Food Delivery Service

![logo](https://github.com/JeonDaehong/daehong-food-delivery/assets/90895144/dfcdfbd0-52c6-48f5-aa21-5a491284ec4f)

## 🏍️ 프로젝트 목표

- 배달의 민족과 같은 배달 서비스를 PC 웹 버전으로 구현해 내는 것을 목표로 합니다.
  
- 단순 기능 구현이 아닌, 동시성 이슈, 분산 서버 환경, 그리고 대용량 트래픽 처리 등을 고려하여 개발합니다.
  
- 객체지향의 원칙을 준수하고, 이론적 근거와 본인만의 철학을 토대로 코드를 작성합니다.
  
- 어떤 기술을 사용하든, 단순히 구현만 할 줄 아는 것이 아니라, 원리를 이해하고 개발합니다.
  
- 새롭게 접한 기술이나, 중요한 기술은 반드시 문서화(블로그) 하여 정리합니다.
  
- 네이버 클라우드를 활용하여, 실제로 서버에 적용시켜 보는 것을 목표로 합니다.

<br>

## 🏍️ 개발 주요 로직 & 주요 이슈 해결 목록

**`클릭 시, 개발 내용을 정리한 개인 블로그 내용으로 이동합니다.`**

#### 1. [여러 대의 분산 서버에서 이미지를 관리할 수 있는 AWS S3 활용법](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-%EC%97%AC%EB%9F%AC-%EB%8C%80%EC%9D%98-%EC%84%9C%EB%B2%84%EB%A5%BC-%EC%9C%84%ED%95%9C-AWS-S3-%ED%99%9C%EC%9A%A9-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B4%80%EB%A6%AC)

#### 2. [동시성 이슈로 인한 문제를 고려하여 Redisson Lock 을 활용한, 적립 포인트 → 사용 가능 포인트 전환 기능 구현](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-Redisson-Lock-%EC%9D%84-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%A0%81%EB%A6%BD-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%E2%86%92-%EC%82%AC%EC%9A%A9-%EA%B0%80%EB%8A%A5-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%EC%A0%84%ED%99%98-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84)

#### 3. [테스트 코드 작성 시 @Transactional 사용에 대하여 고민하고 테스트 코드 작성](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-%EC%8B%9C-Transactional-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EA%B2%83%EC%97%90-%EB%8C%80%ED%95%9C-%EC%A7%A7%EC%9D%80-%EC%83%9D%EA%B0%81)

#### 4. [낙관적 Lock 을 사용할 때 테스트 코드나, Facade 에 @Transactional 을 사용하면 안되는 이유](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-%EB%82%99%EA%B4%80%EC%A0%81-Lock-%EC%97%90%EC%84%9C-Transacional-%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%A9%B4-%EC%95%88%EB%90%98%EB%8A%94-%EC%9D%B4%EC%9C%A0-Lock-%EC%9D%84-%ED%86%B5%ED%95%9C-%EB%9D%BC%EC%9D%B4%EB%8D%94-%EB%B0%B0%EC%B0%A8-%EC%84%9C%EB%B9%84%EC%8A%A4-%EA%B0%9C%EB%B0%9C)

#### 5. [Redis INCR & 백업 테이블을 활용하여 선착순 쿠폰 발급 기능 개발](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-Redis-INCR-%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%98%EC%97%AC-%EC%84%A0%EC%B0%A9%EC%88%9C-%EC%BF%A0%ED%8F%B0-%EB%B0%9C%EA%B8%89-%EA%B8%B0%EB%8A%A5-%EB%A7%8C%EB%93%A4%EA%B8%B0)

#### 6. [주문, 결제 로직 개발시 Self-Invocation 을 고려하여 기능 개발](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-Self-Invocation-%EC%97%90-%EB%8C%80%ED%95%9C-%EA%B0%84%EB%8B%A8%ED%95%9C-%EC%9D%B4%EC%95%BC%EA%B8%B0)

#### 7. [여러 대의 분산 서버에서 Session 로그인을 유지하는 방법](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-Redis-Session%EC%9C%BC%EB%A1%9C-%EB%B6%84%EC%82%B0-%ED%99%98%EA%B2%BD%EC%97%90%EC%84%9C-%EC%84%B8%EC%85%98-%EA%B4%80%EB%A6%AC%ED%95%98%EA%B8%B0)

#### 8. [JPA 생성 및 수정 날짜 자동처리를 위한 공통 엔티티 개발](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-JPA%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%83%9D%EC%84%B1-%EB%B0%8F-%EC%88%98%EC%A0%95-%EB%82%A0%EC%A7%9C-%EC%9E%90%EB%8F%99-%EC%B2%98%EB%A6%AC%EB%A5%BC-%EC%9C%84%ED%95%9C-%EA%B3%B5%ED%86%B5-Entity-%EA%B0%9C%EB%B0%9C)

#### 9. [@RestContollerAdvice 와 @ExceptionHandler 를 활용하여 예외처리 기능 개발](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-RestAdviceController-%EB%A1%9C-Exception-%EC%B2%98%EB%A6%AC%ED%95%98%EA%B8%B0)

<br>

## 🏍️ 프로젝트 중점사항

<br>

## 🏍️ 프로젝트 전체 구성도
![image](https://github.com/JeonDaehong/daehong-food-delivery/assets/90895144/9abb0766-c2db-48f4-bcc3-b1a9be4c8043)

<br>

## 🏍️ 사용 기술 스택
![image](https://github.com/JeonDaehong/daehong-food-delivery/assets/90895144/8f93b3f7-c059-40c8-81df-0a2757b45585)

<br>

## 🏍️ DB ERD

<br>

## 🏍️ Use Case

<br>
<br>
<br>
