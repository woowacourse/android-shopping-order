## Step 3
- [x]: 코루틴으로 마이그레이션
- [x]: ProductList 동시성 문제 해결하기 (레이스 컨디션 ----> productLoadJob 을 사용하여 해결)
- [x]: 이미 장바구니에 담긴 상품이라면 Create 가 아닌 Update 로 처리하기 (DetailProductViewModel)
## Step 4
- 쿠폰
1) FixedCoupon (금액 할인)
- [x]: 최소 구매 금액 이상일 때 고정된 가격 할인 적용
2) BuyXGetYCoupon (x 개 사면 y 개 무료)
- [x]: 최소 구매 개수 이상일 때 할인 적용
- [x]: x개씩 담은 제품이 여러개인 경우, 1개당 금액이 가장 비싼 제품에 적용한다.
3) FreeShippingCoupon
- [x]: 무료 배송 가능 최소 주문 금액 이상일 때 배송비 무료 적용
4) PercentageCoupon
- [x]: 할인 가능한 시간대에 할인율 적용
## Backlog
- []: 코루틴 익셉션 핸들링
- []: 전역 에러 핸들링
- []: 공유 이벤트 처리 (ActivityViewModel => 상위 Layer 로 마이그레이션해보기)