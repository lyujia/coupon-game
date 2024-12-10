# coupon-game
# 쿠폰 서비스 구현 개요

이 쿠폰 서비스는 **Rest API** 형태로 구현되었으며, 주요 기능은 **쿠폰 발행**, **쿠폰 사용 (redeem)**, 그리고 **일괄 쿠폰 정지 (주제별)** 입니다. 또한, **낙관적 락**을 사용하여 동시에 쿠폰을 사용할 때 발생할 수 있는 충돌을 방지하도록 설계되었습니다. 

## 전체 시스템 아키텍처

시스템은 헥사고날 아키텍처(Hexagonal Architecture)를 기반으로 구현되었습니다. 주요 구조는 다음과 같습니다:

- **Domain**: 
  - **Aggregate**: 쿠폰 도메인의 핵심 객체들 (예: 쿠폰, 쿠폰 로그)
  - **Service**: 도메인 로직을 처리하는 서비스
  - **Port**: 외부와의 인터페이스 (Repository 등)
- **Adapter**:
  - **APIController**: REST API의 엔드포인트를 처리
  - **DTO**: 데이터 전송 객체 (API 요청 및 응답 처리)

이 구조는 **인터페이스 분리**를 통해 각 기능을 독립적으로 테스트 가능하게 하며, 코드의 유지보수를 용이하게 합니다.

---

## 주요 구현 사항

### 1. 쿠폰 코드 발행 (Coupon Issue)

쿠폰 코드 발행은 **ULID (Universally Unique Lexicographically Sortable Identifier)**를 사용하여 구현되었습니다. ULID는 **timestamp** 기반으로 정렬되며, 동시에 생성되는 쿠폰 코드들 간에 충돌을 방지하기 위해 **랜덤 40비트**를 추가하여 고유성을 보장합니다. (앞은 timestamp 뒤는 랜덤)

- **발행 시 입력값**:
  - **Topic**: 쿠폰의 주제
  - **Expired Date**: 쿠폰의 만료일
  - **Num**: 쿠폰 코드가 사용 가능한 횟수
- **동시성 처리**: ULID 생성 시 timestamp를 기반으로 하여, 중복 확률을 줄이고 뒤에 랜덤코드를 붙임으로써 **동시 요청 시에도 중복 없이 고유한 쿠폰 코드**를 생성합니다.
- **중복 쿠폰 발행 방지**: 같은 topic에 대해 이미 쿠폰 코드가 발행되었으면 새로 발행하지 않고 실패 응답을 반환합니다. 

---

### 2. 쿠폰 사용 (Redeem Coupon)

쿠폰 코드는 사용자별 1회 사용이 가능합니다. 
쿠폰 사용은 다음과 같은 절차로 이루어집니다:

- **사용자 유효성 검사**: 쿠폰 코드가 사용된 적이 없고, 활성 상태여야만 사용이 가능합니다.
- **쿠폰 로그 기록 서치해서 검사**: 사용된 쿠폰 코드와 해당 사용자 ID를 **쿠폰 로그**에서 추적합니다. -> 한 쿠폰코드당 한사람만 사용 가능하므로 쿠폰로그에 해당 쿠폰코드를 사용한 사용자의 이력이 있으면 코드를 더 사용하지 못하게 합니다.
- **상태 업데이트**: 사용된 쿠폰의 **Num** 값을 1 감소시키고, 사용 횟수가 0이 되면 **쿠폰 상태를 'exhausted'**로 변경합니다.
- **낙관적 락**: 쿠폰 코드에는 **버전 정보**가 존재하여, 쿠폰 코드의 상태가 변경될 때 **낙관적 락**을 사용해 충돌을 방지합니다. 충돌이 발생하면 1초 대기 후 재요청을 시도합니다. 최대 5번의 시도 후에도 충돌이 해결되지 않으면 예외가 발생합니다.
- 성공하면 코드 로그에 기록을 남깁니다.
---

### 3. 일괄 쿠폰 정지 (주제별)

쿠폰을 정지하는 기능은 **쿠폰 상태를 'inactive'**로 변경하여 처리됩니다. 이는 특정 **topic**에 해당하는 모든 쿠폰을 비활성화하는 기능을 구현한 것입니다.

---

## 추가 구현 사항

### 4. 테스트

- **Unit Test**: `Service`와 `ApiController`에 대한 단위 테스트가 작성되었습니다.
- **API 문서 작성**: `Spring Rest Docs`를 사용하여 **API 문서**(snippets)를 자동 생성하였습니다. 부분 문서화가 완료하였으나 버전 문제로 html 문서 X, **Swagger**를 추가하여 API 문서를 개선할 예정입니다.

---

### 성능 최적화 및 설계

1. **낙관적 락(Optimistic Locking) 사용**
   - **낙관적 락**을 적용하여 **동시성** 문제를 해결하고, 데이터 충돌을 방지했습니다.
   - **버전 관리**를 통해 데이터 충돌 여부를 체크하며, 락을 최소화하여 시스템 성능을 최적화했습니다.
   - 많은 사용자가 동시에 쿠폰을 발행하거나 수정할 때, 락을 줄여 성능 저하를 방지할 수 있습니다.

2. **ULID를 기본키로 사용하여 성능 향상**
   - **ULID**(Universally Unique Lexicographically Sortable Identifier)를 기본키로 사용하여 데이터 조회 성능을 최적화했습니다.
   - **ULID**는 시간 순으로 정렬되는 특성이 있어, 쿠폰 코드 기준으로 빠른 조회가 가능합니다.
   - 인덱스 검색 시 **기본키**를 기준으로 하여 검색 성능이 뛰어나고, **빠른 데이터 조회**가 가능합니다.

---

## 코드 구조

```plaintext
src
 ├── domain
 │    ├── errors   
 │    ├── aggregate
 │    ├── service
 │    └── port -> repositoyr,usecase
 ├── adapter
 │    ├── apicontroller
 │    └── dto
 └── util
