# android-shopping-order

## 기능 요구 사항

- [x] 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.

## 프로그래밍 요구 사항

- [x] 서버를 연동한다.
    - [x] 사용자의 장바구니 목록 조회 API를 연동한다.
    - [x] 장바구니 아이템 추가 API를 연동한다.
    - [x] 장바구니 아이템 삭제 API를 연동한다.
    - [x] 장바구니 아이템 수량 변경 API를 연동한다.
    - [x] 장바구니 아이템 수량 조회 API를 연동한다.
    - [x] 상품 목록 조회 API를 연동한다.
    - [x] 상품 상세 조회 API를 연동한다.
- [x] 기존에 작성한 테스트가 깨지면 안 된다.
- [x] 사용자 인증 정보를 저장한다. (적절한 저장 방법을 선택한다)
- [x] 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- [x] 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다. (쿠팡 UX 참고)
    - [x] 상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.
    - [x] 해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출
    - [x] 장바구니에 이미 추가된 상품이라면 미노출
- [x] 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.

## 피드백 반영

- [ ] CouponService | 빈 리스트를 반환하도록 변경
- [ ] ReceiptViewModel | error("") 대신 더 안전한 방식으로 변경
- [ ] 아직 수정하지 않은 execute, body 삭제
- [ ] RecommendViewModel | error("") 대신 더 안전한 방식으로 변경
- [ ] DefaultProductsRepository | maxBy { it.viewedAt }가 빈리스트일 경우 핸들링
- [x] CouponResponseItem | 다형성을 사용하여 파싱하도록 수정
- [ ] ReceiptActivity | 네트워크가 끊어지게 되면 생기는 오류 핸들링
- [ ] 전체적으로 mapper함수 생성
- [ ] 각 viewmodel에 비즈니스 로직을 Repository로 이동
- [ ] ReceiptViewModel | calculator 메서드 네이밍 수정
- [ ] ReceiptViewModel | BindingAdapter를 사용
- [ ] RecommendViewModel | 추천 목록에 있는 상품들은 장바구니에서 제외
- [ ] ReceiptViewModel | map사용