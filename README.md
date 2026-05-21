# Android Shopping Order

## 3단계 - Navigation & Flow

## 기능 구현 목록 

### 1. Compose Navigation 화면 전환
- [ ] Compose Navigation을 사용하여 화면 전환을 구현한다.
- [ ] 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- [ ] 각 화면 이동은 `NavController`를 통해 처리하며, Composable에 직접 전달하지 않고 콜백 람다로 분리한다.
- [ ] 화면 이동 시 전달하는 데이터는 타입 안전한(Type-safe) Route를 사용한다.
- [ ] 모든 Route는 `@Serializable` 타입으로 선언한다.
- [ ] 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 처리한다.
- [ ] 기존 Activity 전환 방식을 제거하고 Compose Navigation(`navigation-compose`)으로 교체한다.

### 2. Flow를 이용한 상태 관리
- [ ] ViewModel의 UI 상태를 `StateFlow`로 노출한다.
- [ ] 기존 `remember`/`mutableStateOf` 기반 Compose State를 `StateFlow`/`SharedFlow`로 교체한다.
- [ ] ViewModel의 상태는 `MutableStateFlow`로 선언하고 `StateFlow`로 노출한다.
- [ ] 장바구니 담기/삭제, 스낵바 표시, 화면 이동 트리거 등 일회성 이벤트는 `SharedFlow`를 사용한다.
- [ ] Composable에서 상태를 구독할 때 `collectAsStateWithLifecycle()`을 사용한다.

---

## 4단계 - 주문 & 알림

## 기능 구현 목록

### 1. 주문 기능
- [ ] 장바구니에 담긴 상품을 최종 주문할 수 있다.
- [ ] 배송비는 기본 3,000원을 적용한다.
- [ ] 결제 화면에서 적용 가능한 쿠폰을 조회하고 1개를 선택하여 적용할 수 있다.
- [ ] 결제하기 버튼을 누르면 최종 주문이 완료되며, 장바구니에서 주문된 상품을 초기화한다.
- [ ] 최종 주문이 완료되면 상품 목록 화면으로 이동한다.

### 2. 쿠폰 할인 계산
- [ ] **5,000원 할인 쿠폰 (FIXED5000)**: 최소 주문 금액 100,000원 이상 시 5,000원을 할인한다.
- [ ] **2개 구매 시 1개 무료 쿠폰 (BOGO)**: 동일 제품 3개 구매 시 1개 금액을 할인하며, 대상 제품이 여러 개인 경우 가장 비싼 제품에 적용한다.
- [ ] **무료 배송 쿠폰 (FREESHIPPING)**: 최소 주문 금액 50,000원 이상 시 배송비를 무료로 처리한다.
- [ ] **미라클모닝 30% 할인 쿠폰 (MIRACLESALE)**: 오전 4시~7시 사이에만 30% 할인을 적용한다.
- [ ] 모든 쿠폰은 명시된 만료일 이후에는 사용할 수 없도록 처리한다.

### 3. 미결제 알림 기능
- [ ] 설정에서 미결제 알림 기능을 On/Off 할 수 있으며, 설정 값은 `SharedPreferences`를 통해 유지한다.
- [ ] 결제 화면 진입 시 5분 후 알림을 예약한다.
- [ ] 결제를 완료하거나 결제 화면으로 돌아오면 예약된 알림을 취소한다.
- [ ] 5분 안에 결제하지 않으면 "아직 결제가 완료되지 않았어요" 알림을 노출한다.
- [ ] 알림을 클릭하면 결제 화면으로 이동한다. 
