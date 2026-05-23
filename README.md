# Android Shopping Cart

# 🚀 3단계 - Navigation & Flow

### ✅ 체크리스트

#### Navigation

- [x] NavHost 기반 화면 전환으로 변경
- [x] Route를 @Serializable 타입으로 선언
- [x] 주문 완료 후 Back Stack 정리
- [x] NavController 직접 전달 없이 콜백으로 이동 처리

#### Flow

- [x] 화면 상태를 StateFlow로 통합
- [x] 일회성 이벤트를 SharedFlow로 처리
- [x] collectAsStateWithLifecycle() 적용

---

# 🚀 4단계 - 주문 & 알림

### 1. 쿠폰
```
1. 5,000원 할인 쿠폰
  - 쿠폰 코드: FIXED5000
  - 할인 금액: 5,000원
  - 최소 주문 금액: 100,000원
  - 만료일: 2024년 11월 30일

2. 2개 구매 시 1개 무료 쿠폰
  - 쿠폰 코드: BOGO
  - 구매 수량: 2
  - 무료 제공 수량: 1
  - 만료일: 2024년 5월 30일
  - BOGO 쿠폰은 장바구니에 동일한 제품을 3개를 담은 상태에서 사용하면, 1개 분량의 금액을 할인한다.
  - 3개씩 담은 제품이 여러개인 경우, 1개당 금액이 가장 비싼 제품에 적용한다.

3. 5만원 이상 구매 시 무료 배송 쿠폰
  - 쿠폰 코드: FREESHIPPING
  - 최소 주문 금액: 50,000원
  - 만료일: 2024년 8월 31일
  - 배송비 무료 쿠폰은 도서 및 산간 지역인 경우에도 무료 배송이 가능하다.

4. 미라클모닝 30% 할인 쿠폰
  - 쿠폰 코드: MIRACLESALE
  - 할인율: 30%
  - 사용 가능 시간: 오전 4시부터 7시까지
  - 만료일: 2024년 7월 31일
```

## 구현 목표 설계
```
쿠폰
├── title
├── lastDay
└── benefit: CouponBenefit
    ├── AmountDiscount
    │   ├── discountAmount
    │   └── minimumOrderAmount
    │
    ├── BuyTwoGetOne
    │   ├── requiredQuantity
    │   └── freeQuantity
    │
    ├── FreeShipping
    │   └── minimumOrderAmount
    │
    └── MorningCleaning
        ├── condition
        └── benefitDescription
```
```
CouponBenefit sealed interface
├── AmountDiscount
├── BuyTwoGetOne
├── FreeShipping
└── MorningCleaning
```
### ✅ 체크리스트

#### 주문

- [ ] 장바구니에 담긴 상품을 최종 주문할 수 있다.
  - 배송비는 기본 3,000원이다.
- [ ] 선택 상품 기준 주문 요청
- [ ] 주문 성공 시 장바구니 초기화 확인
- [ ] 주문 완료 후 목록 화면 이동

#### 쿠폰

- [ ] 결제 화면에서 적용 가능한 쿠폰을 조회하고 적용할 수 있다.
  - 쿠폰은 1개만 적용 가능하다.
- [ ] FIXED5000 적용
- [ ] BOGO 적용
- [ ] FREESHIPPING 적용
- [ ] MIRACLESALE 시간 조건 적용
- [ ] 쿠폰을 사용해도 사라지지 않는 것이 정상이다.

#### 알림

- [ ] BroadcastReceiver를 구현해 알람을 수신하고 NotificationManager로 알림을 노출한다.
- [ ] 알림 권한(POST_NOTIFICATIONS)을 요청한다.
- [ ] larmManager를 사용해 결제 진입 5분 후 알림 예약
- [ ] 결제 완료/재진입 시 알림 취소
- [ ] 알림 클릭 시 결제 화면 이동
- [ ] 알림 권한 Dialog 및 Notification은 기본 UI를 그대로 사용 단, Notification의 아이콘은 커스텀한다.
- [ ] 알림 설정 On/Off SharedPreferences로 영속화
- [ ] 5분 안에 결제하지 않으면 "아직 결제가 완료되지 않았어요" 알림을 노출한다.

#### 결제

- [ ] 결제 수단은 구현하지 않는다.
  - 결제하기 버튼을 누르면 바로 최종 주문이 완료된다.
  - 최종 주문이 완료되면 상품 목록으로 이동한다.