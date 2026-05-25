# 기능 구현 사항

## 4단계 구현 기능 사항

## Navigation
- [x] Payment Screen에서 결제하기 버튼을 선택하면 최종 주문을 완료하고 Shopping Screen으로 이동한다
- [x] 결제 미완료 알림을 선택하면 Payment Screen으로 이동한다

## Payment
- [x] 장바구니에 담긴 상품을 최종 주문할 수 있다
- [x] 배송비는 기본 3,000원으로 적용한다
- [x] 결제 화면에서 적용 가능한 쿠폰 목록을 조회한다
- [x] 쿠폰은 1개만 선택하여 적용할 수 있다
- [ ] 최종 주문이 완료되면 주문된 상품이 장바구니에서 초기화된다

## Coupon
- [x] 쿠폰 API를 통해 적용 가능한 쿠폰 목록을 조회한다
- [x] `fixed` 쿠폰은 `discount` 금액만큼 할인한다
- [x] `fixed` 쿠폰은 `minimumAmount` 조건을 만족할 때만 적용한다
- [x] `buyXgetY` 쿠폰은 `buyQuantity`, `getQuantity` 값을 사용해 할인 금액을 계산한다
- [x] `buyXgetY` 쿠폰은 동일 상품 수량이 `buyQuantity + getQuantity` 이상일 때만 적용한다
- [x] `buyXgetY` 쿠폰 조건을 만족하는 상품이 여러 개인 경우 단가가 가장 비싼 상품에 적용한다
- [x] `freeShipping` 쿠폰은 `minimumAmount` 조건을 만족할 때 배송비를 할인한다
- [x] `percentage` 쿠폰은 `discount` 비율만큼 주문 금액을 할인한다

## Notification
- [x] 설정에서 미결제 알림 기능을 On/Off 할 수 있다
- [x] 앱을 재실행해도 미결제 알림 설정값을 유지한다
- [x] 결제 화면에 진입하면 5분 후 미결제 알림을 예약한다
- [x] 결제를 완료하면 예약된 미결제 알림을 취소한다
- [x] 5분 안에 결제하지 않으면 아직 결제가 완료되지 않았어요 알림을 노출한다
- [x] BroadcastReceiver로 알람을 수신하고 NotificationManager로 알림을 노출한다
- [x] POST_NOTIFICATIONS 권한을 요청한다
- [x] 알림 On/Off 설정은 SharedPreferences에 저장한다

## 3단계 구현 기능 사항

## Navigation
- [x] Shopping Screen에서 카트 아이콘을 선택하면 Cart Screen으로 이동한다
- [x] Shopping Screen에서 상품을 선택하면 Detail Screen으로 이동한다
- [x] Shopping Screen에서 최근 본 상품을 선택하면 Detail Screen으로 이동한다
- [x] Detail Screen에서 마지막으로 본 상품을 선택하면 해당 Detail Screen으로 이동한다
- [x] Detail Screen에서 수량 설정 후 장바구니 담기 버튼을 선택하면 Shopping Screen으로 이동한다
- [x] Cart Screen에서 주문하기를 선택하면 Recommend Screen으로 이동한다
- [x] Recommend Screen에서 주문하기 버튼을 선택하면 Pay Screen으로 이동한다
- [x] Pay Screen에서 결제하기 버튼을 선택하면 Shopping Screen으로 이동한다

## Flow
- [x] 장바구니 담기/삭제 등 단발성 이벤트는 Channel로 처리한다
- [x] Shopping list의 Cart quantity를 위해 Flow<Map<Long, Int>>로 Flow로 처리한다
- [x] 최근 본 상품을 Flow로 처리한다
