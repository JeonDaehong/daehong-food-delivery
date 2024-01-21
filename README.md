# Daehong Food Delivery Service

![logo](https://github.com/JeonDaehong/daehong-food-delivery/assets/90895144/dfcdfbd0-52c6-48f5-aa21-5a491284ec4f)

## 1. 개발 주요 로직 & 주요 이슈 해결 목록

**`클릭 시, 개발 내용을 정리한 개인 블로그 내용으로 이동합니다.`**

#### 1. [JPA 생성 및 수정 날짜 자동처리를 위한 공통 엔티티 개발](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-JPA%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%83%9D%EC%84%B1-%EB%B0%8F-%EC%88%98%EC%A0%95-%EB%82%A0%EC%A7%9C-%EC%9E%90%EB%8F%99-%EC%B2%98%EB%A6%AC%EB%A5%BC-%EC%9C%84%ED%95%9C-%EA%B3%B5%ED%86%B5-Entity-%EA%B0%9C%EB%B0%9C)

#### 2. [여러 대의 서버에서 이미지를 관리할 수 있는 AWS S3 활용법](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-%EC%97%AC%EB%9F%AC-%EB%8C%80%EC%9D%98-%EC%84%9C%EB%B2%84%EB%A5%BC-%EC%9C%84%ED%95%9C-AWS-S3-%ED%99%9C%EC%9A%A9-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B4%80%EB%A6%AC)

#### 3. [동시성 이슈로 인한 문제를 고려하여 Redisson Lock 을 활용한, 적립 포인트 → 사용 가능 포인트 전환 기능 구현](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-Redisson-Lock-%EC%9D%84-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%A0%81%EB%A6%BD-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%E2%86%92-%EC%82%AC%EC%9A%A9-%EA%B0%80%EB%8A%A5-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%EC%A0%84%ED%99%98-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84)

#### 4. [테스트 코드 작성 시 @Transactional 사용에 대하여 고민하고 테스트 코드 작성](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-%ED%85%8C%EC%8A%A4%ED%8A%B8-%EC%BD%94%EB%93%9C-%EC%9E%91%EC%84%B1-%EC%8B%9C-Transactional-%EC%96%B4%EB%85%B8%ED%85%8C%EC%9D%B4%EC%85%98%EC%9D%84-%EC%82%AC%EC%9A%A9%ED%95%98%EB%8A%94-%EA%B2%83%EC%97%90-%EB%8C%80%ED%95%9C-%EC%A7%A7%EC%9D%80-%EC%83%9D%EA%B0%81)

#### 5. 낙관적 Lock 을 사용할 때 테스트 코드나, Facade 에 @Transactional 을 사용하면 안되는 이유
