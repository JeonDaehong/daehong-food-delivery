# 토이 프로젝트

## 1. 개발 주요 로직

### 1. [JPA 생성 및 수정 날짜 자동처리를 위한 공통 엔티티 개발](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-JPA%EB%A5%BC-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%83%9D%EC%84%B1-%EB%B0%8F-%EC%88%98%EC%A0%95-%EB%82%A0%EC%A7%9C-%EC%9E%90%EB%8F%99-%EC%B2%98%EB%A6%AC%EB%A5%BC-%EC%9C%84%ED%95%9C-%EA%B3%B5%ED%86%B5-Entity-%EA%B0%9C%EB%B0%9C)
프로젝트에서는 JPA(Java Persistence API)를 사용하여 데이터베이스와의 상호작용을 관리합니다.
생성 및 수정 날짜 자동처리를 위해 공통 엔티티를 개발하였습니다.
이를 통해 각 엔티티에서 코드 중복을 최소화하며, 자동으로 생성 및 수정 날짜를 관리할 수 있습니다.

### 2. [여러 대의 서버에서 이미지를 관리할 수 있는 AWS S3 활용법](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-%EC%97%AC%EB%9F%AC-%EB%8C%80%EC%9D%98-%EC%84%9C%EB%B2%84%EB%A5%BC-%EC%9C%84%ED%95%9C-AWS-S3-%ED%99%9C%EC%9A%A9-%EC%9D%B4%EB%AF%B8%EC%A7%80-%EA%B4%80%EB%A6%AC)
프로젝트에서는 여러 대의 서버에서 이미지를 효율적으로 관리하기 위해 AWS S3를 활용합니다.
S3는 객체 스토리지 서비스로, 안전하게 파일을 저장하고 검색할 수 있도록 지원합니다.

### 3.[Redisson Lock 을 활용한, 적립 포인트 → 사용 가능 포인트 전환 기능 구현](https://development-my-link.tistory.com/entry/%EA%B0%9C%EB%B0%9C-%ED%9A%8C%EA%B3%A0%EB%A1%9D-Redisson-Lock-%EC%9D%84-%ED%99%9C%EC%9A%A9%ED%95%9C-%EC%A0%81%EB%A6%BD-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%E2%86%92-%EC%82%AC%EC%9A%A9-%EA%B0%80%EB%8A%A5-%ED%8F%AC%EC%9D%B8%ED%8A%B8-%EC%A0%84%ED%99%98-%EA%B8%B0%EB%8A%A5-%EA%B5%AC%ED%98%84)
적립 포인트를 사용 가능한 포인트로 전환하는 기능에서 동시성 이슈를 방지하기 위해 Redisson Lock을 활용합니다. 이를 통해 여러 서버 간에 안전하게 포인트 전환을 처리할 수 있습니다.

