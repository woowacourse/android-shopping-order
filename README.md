# Android Shopping Order

## 단계 현황

- `1단계 - 서버 연동`: 완료
- `2단계 - 상품 추천`: 완료
- `3단계 - Navigation & Flow`: 완료
- `4단계 - 주문 & 알림`: 진행 예정

## 4단계 - 주문 & 알림

### 목표

- 장바구니 상품의 최종 주문 흐름을 완성한다.
- 쿠폰과 배송비 정책을 정확히 반영한 결제 금액 계산을 구현한다.
- 미결제 알림 예약, 취소, 설정 저장 흐름을 구현한다.

### 기능 요구 사항

#### 주문

- [x] 장바구니에 담긴 상품을 최종 주문할 수 있다.
- [x] 배송비는 기본 3,000원이다.
- [x] 결제 수단은 구현하지 않는다.
- [x] 결제하기 버튼을 누르면 바로 최종 주문이 완료된다.
- [x] 최종 주문이 완료되면 상품 목록으로 이동한다.

#### 쿠폰

- [x] 결제 화면에서 적용 가능한 쿠폰을 조회하고 적용할 수 있다.
- [x] 쿠폰은 1개만 적용 가능하다.

#### 알림

- [x] 설정에서 미결제 알림 기능을 On/Off 할 수 있다.
- [x] 앱을 재실행해도 설정이 유지되어야 한다.
- [x] 결제 화면에 진입하면 5분 후 알림을 예약한다.
- [x] 결제를 완료하거나 결제 화면으로 돌아오면 예약된 알림을 취소한다.
- [x] 5분 안에 결제하지 않으면 `"아직 결제가 완료되지 않았어요"` 알림을 노출한다.
- [x] 알림을 클릭하면 결제 화면으로 이동한다.

### API 구현 상세

- [x] 최종 주문이 완료되면 장바구니에서 주문된 상품이 초기화된다.
- [x] 쿠폰은 사용해도 사라지지 않는다.

### 쿠폰 정책

#### `FIXED5000`

- 할인 금액: 5,000원
- 최소 주문 금액: 100,000원
- 만료일: 2024년 11월 30일

#### `BOGO`

- 구매 수량: 2
- 무료 제공 수량: 1
- 만료일: 2024년 5월 30일
- 동일한 제품을 3개 담은 상태에서 사용하면 1개 분량의 금액을 할인한다.
- 3개씩 담은 제품이 여러 개인 경우 1개당 금액이 가장 비싼 제품에 적용한다.

#### `FREESHIPPING`

- 최소 주문 금액: 50,000원
- 만료일: 2024년 8월 31일
- 도서 및 산간 지역인 경우에도 무료 배송이 가능하다.

#### `MIRACLESALE`

- 할인율: 30%
- 사용 가능 시간: 오전 4시부터 7시까지
- 만료일: 2024년 7월 31일

### 프로그래밍 요구 사항

- [x] `AlarmManager`를 사용해 결제 화면 진입 시 5분 후 알림을 예약한다.
- [x] 결제 완료 또는 결제 화면 재진입 시 `AlarmManager`로 예약된 알림을 취소한다.
- [x] `BroadcastReceiver`를 구현해 알람을 수신하고 `NotificationManager`로 알림을 노출한다.
- [x] 알림 권한(`POST_NOTIFICATIONS`)을 요청한다.
- [x] 알림 권한 Dialog 및 Notification은 기본 UI를 그대로 사용한다.
- [x] Notification의 아이콘은 커스텀한다.
- [x] 알림 On/Off 설정은 `SharedPreferences`로 저장한다.

#### 알림 권한

- `registerForActivityResult`는 권한 요청이나 외부 Activity 호출 결과를 비동기로 받기 위한 Activity Result API 진입점이다.
- 알림 권한 요청 결과에 따라 예약/노출 가능한지 분기한다.


#### SharedPreferences

- 알림 On/Off처럼 앱 재실행 후에도 유지되어야 하는 단순 설정 값 저장에 적합하다.

### 참고 자료

- AlarmManager: https://developer.android.com/training/scheduling/alarms?hl=ko
- BroadcastReceiver: https://developer.android.com/guide/components/broadcasts?hl=ko

---

## 이전 단계 기록

### 3단계 - Navigation & Flow

#### 기능 요구 사항

- [x] Compose Navigation으로 화면 전환을 구현한다.
- [x] 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- [x] 각 화면 이동은 `NavController`를 통해 처리한다.
- [x] 화면 이동 시 전달하는 데이터는 타입 안전한 Route를 사용한다.
- [x] 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 한다.
- [x] ViewModel의 UI 상태를 `StateFlow`로 노출한다.
- [x] 장바구니 담기/삭제 등 단발성 이벤트는 `SharedFlow`로 처리한다.
- [x] Composable에서 상태를 구독할 때 `collectAsStateWithLifecycle()`을 사용한다.

#### 프로그래밍 요구 사항

- [x] 기존 Activity 전환 방식을 제거하고 Compose Navigation(`navigation-compose`)으로 교체한다.
- [x] 모든 Route를 `@Serializable` 타입으로 선언한다.
- [x] `NavController`는 화면 Composable에 직접 전달하지 않고, 이동 로직은 콜백 람다로 분리한다.
- [x] 기존 `remember`/`mutableStateOf` 기반 Compose State를 `StateFlow`/`SharedFlow`로 교체한다.
- [x] ViewModel의 상태는 `MutableStateFlow`로 선언하고 `StateFlow`로 노출한다.
- [x] 일회성 이벤트는 `MutableSharedFlow`를 사용한다.
- [x] Composable에서 `collectAsState()` 대신 `collectAsStateWithLifecycle()`을 사용한다.

### 2단계 - 상품 추천

#### 기능 요구 사항

- [x] 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- [x] 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천한다.
- [x] 추천 상품은 최근 본 상품의 카테고리를 기준으로 최대 10개 노출한다.
- [x] 해당 카테고리 상품이 10개 미만이면 가능한 개수만 노출한다.
- [x] 장바구니에 이미 추가된 상품은 추천 목록에서 제외한다.
- [x] 추천 상품을 해당 화면에서 바로 장바구니에 추가하고 함께 주문할 수 있다.

#### 프로그래밍 요구 사항

- [x] 기능 요구 사항에 대한 테스트를 작성한다.

#### 기능 목록

- [x] 장바구니 상품을 선택할 수 있다.
- [x] 선택한 상품만 주문하기 버튼을 누를 수 있다.
- [x] 선택한 상품이 없으면 주문할 수 없다.
- [x] 사용자가 최근 본 상품의 카테고리를 저장한다.
- [x] 가장 최근에 본 상품의 카테고리를 기준으로 추천 상품을 조회한다.
- [x] 같은 카테고리 상품을 최대 10개까지 노출한다.
- [x] 이미 장바구니에 담긴 상품은 추천 목록에서 제외한다.
- [x] 추천 상품 화면에서 상품을 바로 장바구니에 추가할 수 있다.
- [x] 추천 상품을 추가하면 함께 주문할 수 있다.

### 1단계 - 서버 연동

#### 기능 요구 사항

- [x] 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.

#### 프로그래밍 요구 사항

- [x] 서버를 연동한다.
- [x] 기존에 작성한 테스트가 깨지면 안 된다.
- [x] 사용자 인증 정보를 저장한다.
- [x] 서버 통신을 위한 JSON 직렬화 라이브러리를 선택하고, 선택 이유를 PR에 남긴다.

#### 기능 목록

- [x] 상품 목록을 서버에서 조회한다.
- [x] 서버 응답 DTO를 도메인 모델로 변환한다.
- [x] 데이터 로딩 전 스켈레톤 UI를 노출한다.
- [x] 사용자 인증 정보를 저장한다.
- [x] 저장된 인증 정보를 서버 요청에 사용한다.
- [x] Kotlinx Serialization을 사용해 JSON을 직렬화/역직렬화한다.
- [x] 기존 테스트가 깨지지 않도록 유지한다.

#### 서버 API 참고

- API 문서: `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/swagger-ui/index.html`
- 관리자 페이지(상품 관리): `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/admin`
- 설정 페이지(계정 정보 확인): `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/settings`
