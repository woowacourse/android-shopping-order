# android-shopping-order

## 🛠️ 기능 구현 목록

- [x] 목록 데이터 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다
- [x] 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- [x] 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다. (쿠팡 UX 참고)
    - 상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.
    - 예를 들어 가장 최근에 본 상품이 fashion 카테고리라면, fashion 상품 10개 노출
    - 해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출
    - 장바구니에 이미 추가된 상품이라면 미노출
- [x] 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.

## ⌨️ 프로그래밍 요구 사항

- [x] 서버를 연동한다
    - 기존에 작성한 테스트가 깨지면 안 된다
- [x] 사용자 인증 정보를 저장한다

### 1차 피드백 반영

- [x] ShoppingApplication 인스턴스를 생성 -> dataBase의 인스턴스 생성
- [x] Fragment의 Tag를 통한 분기 처리 로직 수정
- [x] ViewModel에서 수행 작업 -> ViewModel에서 리스너를 구현
- [ ] exception이 안 터지는 곳에서 runCatching rapping 제거
- [ ] datasource execute 동기처리 enqueue 비동기처리로 수정
  - (remoteDataSource에서 enqueue를 통한 비동기 처리중에 Cached 데이터랑 순서를 못 맞춰서 반영 실패)
    - CartRepository에서 cachedCart가 캐싱되기전에 Catalog 화면이 실행되어서 Catalog에서 캐싱되기전 cachedCart의 데이터로 화면을 초기화하는 부분 때문에 실패
    - Catalog 화면에서 각 CartProductUiModel이 increase, decrease를 통한 변경에서 캐싱 데이터가 초기화 되기 이전에 값을 사용해서 Ui에 반영이 늦어져서 실패
- [ ] recyclerView -> listAdapter로 수정
- [ ] ConcatAdapter 적용
- [ ] domain layer 학습 후 중복 로직(상품 수량을 늘린다, 줄인다, 장바구니 담는다) 제거
