# android-shopping-order

- [x] 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.
- [x] 사용자 인증 정보를 저장한다. (적절한 저장 방법을 선택한다)
- [x] 서버 통신을 Retrofit으로 리팩터링한다.
    - [ ] 서버 통신을 위한 JSON 직렬화 라이브러리를 선택하고 PR에 선택 이유를 남긴다.
- [x] 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- [x] 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다. (쿠팡 UX 참고)
- [x] 상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.
    - [x] 예를 들어 가장 최근에 본 상품이 fashion 카테고리라면, fashion 상품 10개 노출
    - [x] 해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출
    - [x] 장바구니에 이미 추가된 상품이라면 미노출
    - [x] 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.
- [ ] 기능 요구 사항에 대한 테스트를 작성해야 한다.

---

### 알게된 점

- toList()
  quantity를 업데이트하고 diffUtil을 사용했는데, 아이템이 업데이트된게 반영되지 않아 보였다.
  DiffUtil을 하나씩 따라가보니 oldData도 이미 변경된 데이터임을 로그를 통해 알았다.
  그래서 더 들어가보니 toList를 사용하면 list는 복제되어도 안에 객체 참조는 그대로 됨..
  그래서 map과 copy로 해결했다

- BindingAdapter Nullable
  데이터가 바인딩 되지 않은 시점에 xml이 로드되면 null이 들어갈 수 있어서 nullable 처리해야 오류 발생을 피할 수 있다.

- 제네릭은 컴파일 타임에 결정되지만, reified는 런타임에 할 수 있도록 해준다

### STEP 4

- 장바구니에 담긴 상품을 최종 주문할 수 있다.
    - 배송비는 기본 3,000원이다.
- 결제 화면에서 적용 가능한 쿠폰을 조회하고 적용할 수 있다.
    - 쿠폰은 1개만 적용 가능하다.
- 최종 주문이 완료되면 상품 목록으로 이동과 함께 주문 완료 토스트 메시지를 노출한다.
