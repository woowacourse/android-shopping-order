# android-shopping-order

# 🚀 3단계 - Navigation & Flow & 4단계 - 주문

## 3단계 구현 기능 목록

### Navigation 구조 전환

- [x] `navigation-compose` 의존성 추가
- [x] 기존 Activity 기반 화면 전환 로직 제거
- [x] `startActivity()`, `finish()`, `onRestart()` 기반 흐름 제거
- [x] 단일 Activity에서 `NavHost`를 사용해 화면 전환 처리
- [x] 상품 목록 화면을 Navigation 목적지로 등록
- [x] 상품 상세 화면을 Navigation 목적지로 등록
- [x] 장바구니 화면을 Navigation 목적지로 등록
- [x] 상품 추천 화면을 Navigation 목적지로 등록

### Route 구성

- [x] 모든 화면 Route를 `@Serializable` 타입으로 선언
- [x] 상품 상세 화면 이동 시 상품 ID를 Route로 전달
- [x] 장바구니 화면 Route 정의
- [x] 상품 추천 화면 Route 정의
- [x] 문자열 기반 Route 사용을 최소화

### 화면 이동 처리 개선

- [x] 각 화면 이동을 `NavController`를 통해 처리
- [x] `NavController`를 화면 Composable에 직접 전달하지 않기
- [x] 화면 이동 로직을 콜백 람다로 분리
- [x] 상품 목록에서 상품 상세로 이동하는 콜백 연결
- [x] 상품 상세에서 장바구니로 이동하는 콜백 연결
- [x] 장바구니에서 주문 완료 후 상품 목록으로 이동하는 콜백 연결
- [x] 상품 추천은 CartScreen 내부 CartFlow 상태 분기로 처리

### 주문 완료 후 Back Stack 정리

- [x] 주문 완료 이벤트 발생 시 상품 목록 화면으로 이동
- [x] 주문 완료 후 장바구니/추천 화면이 Back Stack에 남지 않도록 처리
- [x] `popUpTo()`를 사용해 주문 흐름 제거
- [x] 뒤로가기 시 주문 완료 이전 화면으로 돌아가지 않도록 검증

### SharedFlow 기반 단발성 이벤트 처리

- [x] 장바구니 추가 완료 이벤트를 `SharedFlow`로 처리
- [x] 장바구니 삭제 완료 이벤트를 `SharedFlow`로 처리
- [x] 주문 완료 이벤트를 `SharedFlow`로 처리
- [x] 스낵바 표시 이벤트를 `SharedFlow`로 처리
- [x] ViewModel의 `MutableSharedFlow`를 외부에 `SharedFlow`로 노출

### 상품 추천 화면 처리

- [x] 주문하기 버튼에서 CartFlow 전환 로직 구현
- [x] 추천 상품 추가/수정/삭제 시 CartViewModel의 메서드 호출
- [x] 주문 완료 시 CartFlow.CART에서 CartFlow.RECOMMEND로 진행
- [x] 주문 최종 완료 후 ProductListRoute로 탈출 

### Compose 상태 관리 정리

- [x] 화면 전환과 관련된 로컬 Compose State 제거
- [x] Navigation 흐름은 ViewModel 이벤트 또는 상위 콜백으로 처리

## 4단계 구현 기능 목록

### 주문 / 결제 기능

- [x] 장바구니에 담긴 선택된 상품을 최종 주문할 수 있다.
- [x] 결제 완료 시 상품 목록으로 이동한다.

### 요금 / 배송

- [x] 배송비는 기본 3,000원이다.
- [x] 무료 배송 쿠폰 적용 시 배송비 0원 처리

### 쿠폰 (조회·검증·적용)

- [x] 쿠폰 도메인 모델, DTO 매핑, 데이터소스/레포지토리 구현
- [x] 쿠폰 할인 계산기(CouponCalculator) 구현 
- [x] 결제 화면에서 적용 가능한 쿠폰을 조회하고 1개만 적용할 수 있다.
- [x] 쿠폰 적용은 만료일, 최소주문금액, 시간조건 등으로 검증한다.
- [x] 쿠폰 적용 결과는 결제 완료 시에도 쿠폰 데이터 자체는 사라지지 않는다.

### 알림 및 설정

- [x] 설정에서 미결제 알림 기능을 On/Off 할 수 있다.
- [x] 결제 화면에 진입하면 5분 후 알림을 예약한다. (빠른 테스트를 위해 10초로 구현)
- [x] 결제를 완료하거나 결제 화면으로 다시 진입하면 예약된 알림을 취소한다.
- [x] 5분 안에 결제하지 않으면 알림을 노출한다.(빠른 테스트를 위해 10초로 구현)
- [ ] 알림을 클릭하면 결제 화면으로 이동한다.

### API / 데이터 동작 상세

- [x] 주문 완료 시 장바구니에서 주문된 상품이 초기화된다.
- [x] 쿠폰은 주문 완료 후에도 사라지지 않는다.
