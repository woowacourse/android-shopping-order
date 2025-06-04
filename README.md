# android-shopping-order

### 1,2단계 기능 요구 사항
- 스켈레톤 UI 노출
  - [x] 상품 목록
  - [x] 장바구니
- 서버 연동
  - [x] 장바구니 아이템
  - [ ] 주문
  - [x] 상품
- [x] 사용자 인증 정보 저장
- [ ] 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- [ ] 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다. (쿠팡 UX 참고)
- [ ] 상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.
- [ ] 예를 들어 가장 최근에 본 상품이 fashion 카테고리라면, fashion 상품 10개 노출
- [ ] 해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출
- [ ] 장바구니에 이미 추가된 상품이라면 미노출
- [ ] 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.

### 코니 요구사항
- [ ] 리드미 작성, PR 작성 잘하기

상품 목록 화면 구현
- [ ] 최근 본 상품이 0개인 경우, 최근 본 상품 목록 삭제

Cart 화면 구현
- [ ] 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다. 구현
- [ ] 총 가격은 변하는데 왜 체크 박스는 변하지 않는 기능 수정

- [x] ShoppingApplication에 thread { DevMockServer.start() } 삭제
- [x] ShoppingApplication에서 정말 SharedPreferences에 값을 저장하고 있는 지 확인 + SharedPreference에서 어떻게 동작하는 지 확인
- [x] CatalogViewModel에서 catalogProduct, quantity 사용하지 않는 값 삭제
- [x] CatalogViewModel에서 interface에서 remoteCatalogProductRepositoryImpl 실제 구현체 주입 -> 수정
- [x] 최근 상품 목록이 0개인 경우, 최근 본 상품 목록 뷰가 보이지 않게 하기
- [x] 사용자 정보 local.properties에 저장 -> BuildConfig로 갖고 오게 끔 구현
  - [ ] 레벨업 부분 ) 인증에 필요한 key은 secerets에 저장
- CartActivity에서 생각해야 할 부분
  - [ ] CartActivity에서 hasHandledTotalCount가 정말 필요한 로직일지 생각
  - [ ] View에서 totalCountrk 몉인지에 따라서 어떤한 fragment를 commit할지 아닌 ViewModel에서 어떠한 상태를 두고 그 상태에 따라서 화면을 이동하는 것
  - [ ] View까지 와서 단순 ViewModel의 함수를 호출하는 observe를 해야하는 지 생각 -> ViewModel의 일은 스스로 하게 변
- CartRecommendationFragment
  - [ ] 로그 삭제 
  - [ ] 이런 식으로 Unit으로 처리를 하게 될 함수가 필수 overide 라면 ProductActionListener 에서 저 함수가 필수가 아니었던 게 아닌지 생각
  - [ ] collect는 viewLifecycleOwner 따라가게끔 해주셨는데, 데이터 바인딩에 사용될 lifecycleOwner 는 this 를 넘기고 있음
- CartSelectionFragment
  - [ ] 아직 View가 만들어지기 전에 view에 필요한 데이터를 미리 observe를 하고 adpater를 세팅할 필요한지
  - [ ] onCreateView에서는 어떠한 동작을 해야 하고 어떠한 단계인지 생각
  - [ ] ViewModelStoreOwner를 Activity로 잡은 점
  - [ ] Application이 왜 필요로 하는가?
  - [ ] Enabled 된 것이랑 updateCartItems 를 호출하는 상관관계 -> 전반적으로 view까지 observe가 되어야 했던 값들일까 하는 의문
  - [ ] 데이터 바인딩 적용
  - [ ] updateCartItems()가 view에서 처리해야하는 지 생각
- CartViewModel
  - [ ] totalCount가 -1 라는 것은 논리적으로 읽을 수 없음 -> 상수화
----- https://github.com/woowacourse/android-shopping-order/pull/111#discussion_r2113932605 여기 부터 적용 다시
