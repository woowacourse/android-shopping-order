# 쇼핑 주문 (3 ~ 4단계)

## 단계별 진행

### 4단계 - 주문 & 알림

## 단위 테스트 목록 (TDD)

1. 5,000원 할인 쿠폰
    - 쿠폰 코드:`FIXED5000`
    - 할인 금액: 5,000원
    - 최소 주문 금액: 100,000원
    - 만료일: 2024년 11월 30일
2. 2개 구매 시 1개 무료 쿠폰
    - 쿠폰 코드:`BOGO`
    - 구매 수량: 2
    - 무료 제공 수량: 1
    - 만료일: 2024년 5월 30일
    - BOGO 쿠폰은 장바구니에 동일한 제품을 3개를 담은 상태에서 사용하면, 1개 분량의 금액을 할인한다.
    - 3개씩 담은 제품이 여러개인 경우, 1개당 금액이 가장 비싼 제품에 적용한다.
3. 5만원 이상 구매 시 무료 배송 쿠폰
    - 쿠폰 코드:`FREESHIPPING`
    - 최소 주문 금액: 50,000원
    - 만료일: 2024년 8월 31일
    - 배송비 무료 쿠폰은 도서 및 산간 지역인 경우에도 무료 배송이 가능하다.
4. 미라클모닝 30% 할인 쿠폰
    - 쿠폰 코드:`MIRACLESALE`
    - 할인율: 30%
    - 사용 가능 시간: 오전 4시부터 7시까지
    - 만료일: 2024년 7월 31일

### Domain: Coupon / CouponPolicy

- [x] FixedDiscountCoupon은 최소 주문 금액 미만이면 적용 불가다
- [x] FixedDiscountCoupon은 최소 주문 금액 이상이면 5,000원을 할인한다
- [x] BogoCoupon은 장바구니에 동일 상품이 3개 이상 있어야 적용 가능하다
- [x] BogoCoupon은 3개 이상인 상품이 없으면 적용 불가다
- [x] BogoCoupon은 3개 이상 담긴 상품이 여러 개일 때 가장 비싼 상품 1개 가격을 할인한다
- [x] FreeShippingCoupon은 최소 주문 금액(50,000원) 미만이면 적용 불가다
- [x] FreeShippingCoupon은 적용 시 배송비를 0원으로 만든다
- [x] MiracleMorningCoupon은 04시 이전 그리고 07시 이후면 적용할 수 없다
- [x] MiracleMorningCoupon은 04시 이후 그리고 07시 이전이면 적용할 수 있다
- [x] MiracleMorningCoupon은 적용 시 총 상품 금액의 30%를 할인한다
- [x] 쿠폰은 만료일을 초과하면 사용할 수 없다

### ViewModel: PurchaseViewModel

- [x] 적용 가능한 쿠폰 목록을 조회한다
- [x] 쿠폰 선택 시 총 결제 금액이 재계산된다
- [x] 총 결제 금액 = 주문 금액 - 쿠폰 할인 + 배송비다

### ViewModel: SettingViewModel (미결제 알림 On/Off)

- [x] 초기 상태를 저장소에서 가져오거나 기본값으로 처리한다
- [x] 토글 시 알림 설정값이 저장소에 즉시 반영된다
- [x] 저장된 알림 설정값은 앱 재시작 후에도 동일하게 노출된다

## UI 테스트 목록 (Compose + Navigation)

### Navigation: 결제 완료 흐름

- [ ] 결제하기 버튼을 누르면 ProductList 라우트로 이동한다
- [ ] 결제 완료 후 Back Stack에 Purchase/Recommend/Cart 라우트가 남지 않는다
- [ ] 결제 완료 후 ProductList에서 뒤로가기를 누르면 앱이 종료된다

### Navigation: 일반 흐름

- [ ] 상품 목록에서 상품 카드를 누르면 ProductDetail 라우트로 이동한다
- [ ] 상품 상세에서 전달된 productId로 화면이 렌더링된다
- [ ] 장바구니에서 주문하기를 누르면 Recommend 라우트로 이동한다
- [ ] 상품 추천에서 결제하기를 누르면 Purchase 라우트로 이동한다

### PurchaseScreen 렌더링

- [ ] 주문 상품 금액, 배송비(3,000원), 총 결제 금액이 화면에 표시된다
- [ ] 적용 가능한 쿠폰 목록이 화면에 표시된다
- [ ] 쿠폰을 선택하면 단 하나만 체크 상태가 유지된다
- [ ] 쿠폰 선택 시 총 결제 금액 표시가 갱신된다

### 스낵바 / 이벤트

- [ ] 장바구니 담기 완료 시 스낵바가 노출된다
- [ ] 장바구니 삭제 완료 시 스낵바가 노출된다
- [ ] 결제 완료 시 스낵바(또는 토스트)가 노출된다

### 알림 / 권한

- [ ] 결제 화면 진입 시 POST_NOTIFICATIONS 권한 요청 Dialog가 노출된다 (Android 13+)
- [ ] 알림 클릭 시 Purchase 라우트로 진입한다

## 기능 목록

### 주문/결제

- [x] 결제 화면에서 주문 상품 금액, 배송비(기본 3,000원), 총 결제 금액을 표시한다
- [x] 결제 화면에서 적용 가능한 쿠폰 목록을 조회할 수 있다
- [x] 결제 화면에서 쿠폰을 하나만 적용할 수 있다
- [x] 결제하기 버튼을 누르면 최종 주문이 완료된다
- [x] 최종 주문이 완료되면 상품 목록으로 이동한다 (Back Stack 정리)

### 쿠폰

- [x] FIXED5000 (5,000원 할인, 최소 주문 100,000원, 만료일 2026-11-30) 적용
- [x] BOGO (2개 구매 시 1개 무료, 동일 상품 3개 이상, 만료일 2026-05-30) 적용
- [x] BOGO 적용 시 3개 이상 담긴 상품 중 1개당 가격이 가장 비싼 상품 기준 할인
- [x] FREESHIPPING (5만원 이상 무료 배송, 도서/산간 포함, 만료일 2026-08-31) 적용
- [x] MIRACLESALE (30% 할인, 04:00 ~ 07:00, 만료일 2026-07-31) 적용

### 설정 & 알림

- [x] 설정 화면에서 미결제 알림 기능을 On/Off 할 수 있다
- [x] 알림 설정값이 DataStore에 저장되어 앱 재실행에도 유지된다
- [ ] 알림 설정이 On일 때 결제 화면 진입 시 5분 후 알림을 예약한다
- [ ] 결제 완료 또는 결제 화면 재진입 시 예약된 알림을 취소한다
- [ ] 5분 안에 결제하지 않으면 "아직 결제가 완료되지 않았어요" 알림을 노출한다
- [ ] 알림을 클릭하면 결제 화면으로 이동한다
- [x] POST_NOTIFICATIONS 권한을 요청한다 (Android 13+)

#### 기능 요구사항

- 장바구니에 담긴 상품을 최종 주문할 수 있다.
    - 배송비는 기본 3,000원이다.
- 결제 화면에서 적용 가능한 쿠폰을 조회하고 적용할 수 있다.
    - 쿠폰은 1개만 적용 가능하다.
- 결제 수단은 구현하지 않는다.
    - 결제하기 버튼을 누르면 바로 최종 주문이 완료된다.
    - 최종 주문이 완료되면 상품 목록으로 이동한다.
- 설정에서 미결제 알림 기능을 On/Off 할 수 있다.
    - 앱을 재실행해도 설정이 유지되어야 한다.
- 결제 화면에 진입하면 5분 후 알림을 예약한다.
    - 결제를 완료하거나 결제 화면으로 돌아오면 예약된 알림을 취소한다.
    - 5분 안에 결제하지 않으면 "아직 결제가 완료되지 않았어요" 알림을 노출한다.
    - 알림을 클릭하면 결제 화면으로 이동한다.

#### API 구현 상세

- 최종 주문이 완료되면 장바구니에서 주문된 상품이 초기화되는 것이 정상이다.
- 쿠폰을 사용해도 사라지지 않는 것이 정상이다.

#### 프로그래밍 요구사항

### 알림

- AlarmManager를 사용해 결제 화면 진입 시 5분 후 알림을 예약한다.
- 결제 완료 또는 결제 화면 재진입 시 AlarmManager로 예약된 알림을 취소한다.
- BroadcastReceiver를 구현해 알람을 수신하고 NotificationManager로 알림을 노출한다.
- 알림 권한(POST_NOTIFICATIONS)을 요청한다.
- 알림 권한 Dialog 및 Notification은 기본 UI를 그대로 사용한다.
    - 단, Notification의 아이콘은 커스텀한다.
- 알림 On/Off 설정은 SharedPreferences로 저장한다.

### 3단계 — Navigation & Flow

## 기능 목록

- [x] 기존 Activity 전환 제거 및 단일 Activity + Compose Navigation 도입
- [x] @Serializable Route 타입 정의 (ProductList/ProductDetail/Cart/Recommend)
- [x] 상품 목록 -> 상품 상세 이동 및 값 전달
- [x] 상품 목록 -> 장바구니 이동
- [x] 장바구니 -> 상품 추천 이동 및 값 전달
- [x] 상품 추천에서 주문 완료 후 뒤로가기를 누르면 상품 목록으로 복귀한다.
- [x] 장바구니 담기 완료 이벤트 SharedFlow (스낵바)
- [x] 장바구니 삭제 완료 이벤트 SharedFlow (스낵바)

#### 기능 요구사항

### Compose Navigation으로 화면 전환을 구현한다

- 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- 각 화면 이동은 NavController를 통해 처리한다.
- 화면 이동 시 전달하는 데이터는 타입 안전한 Route를 사용한다.
- 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 한다.

### UI 상태를 Flow로 관리한다

- ViewModel의 UI 상태를StateFlow로 노출한다.
- 장바구니 담기/삭제 등 단발성 이벤트는SharedFlow로 처리한다.
- Composable에서 상태를 구독할 때collectAsStateWithLifecycle()을 사용한다.

#### 프로그래밍 요구사항

### Navigation

- 기존 Activity 전환 방식을 제거하고Compose Navigation(navigation-compose)으로 교체한다.
- 모든 Route를@Serializable타입으로 선언한다.
- NavController는 화면 Composable에 직접 전달하지 않는다. 이동 로직은 콜백 람다로 분리한다.

### Flow

- 기존remember/mutableStateOf기반 Compose State를StateFlow/SharedFlow로 교체한다.
- ViewModel의 상태는MutableStateFlow로 선언하고StateFlow로 노출한다.
- 일회성 이벤트(스낵바 표시, 화면 이동 트리거 등)는MutableSharedFlow를 사용한다.
- Composable에서collectAsState()대신collectAsStateWithLifecycle()을 사용한다.
