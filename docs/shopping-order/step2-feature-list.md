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

### UseCase
- [x]: DefaultLoadAvailableDiscountCouponsUseCaseTest
    - [x]: 주문할 상품들에 적용가능한 쿠폰들을 불러온다
    - [x]: 상품 id들이 유효하지 않을 경우 예외를 반환한다
    - [x]: 장바구니 상품들을 불러오기에 실패할 경우 예외를 반환한다
    - [x]: 쿠폰 불러오기에 실패할 경우 예외가 발생한다
- [x]: DefaultAddCartProductUseCase 테스트
    - [x]: Id 로 상품을 찾을 수 없고, 장바구니에도 해당 상품이 있으면 장바구니에서 해당 상품을 삭제한다
    - [x]: 장바구니에 상품이 없으면, 장바구니 상품을 생성한다
    - [x]: 장바구니에 상품이 존재하면, 장바구니 상품을 업데이트한다
- [x]: DefaultDecreaseCartProductUseCaseTest 테스트
    - [x]: 장바구니에 상품이 없으면 예외를 반환한다
    - [x]: 장바구니에 상품이 있고, 수량이 1개 이하면 상품을 삭제한다.
    - [x]: 장바구니에 상품이 있고, 수량이 1개 이상이면 수량을 감소시킨다
- [x]: DefaultIncreaseCartProductUseCaseTest 테스트
    - [x]: 장바구니에 상품이 없으면 예외를 반환한다
    - [x]: cart 에 상품이 없으면 새롭게 생성한다
    - [x]: 장바구니에 상품이 있으면 수량을 증가시킨다
    
## Backlog
- []: 코루틴 익셉션 핸들링
- []: 전역 에러 핸들링
- []: 공유 이벤트 처리 (ActivityViewModel => 상위 Layer 로 마이그레이션해보기)