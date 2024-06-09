# android-shopping-order

# 1 단계

## 기능 요구 사항

데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.

- 상품 목록
- 최근 본 상품
- 쇼핑 장바구니
- 상품 상세

- issue:
    - item_product_skeleton 에서 이미지 부분 사이에 마진을 안 줄 수 없을까?

### 프로그래밍 요구 사항

서버를 연동한다.
기존에 작성한 테스트가 깨지면 안 된다.
사용자 인증 정보를 저장한다. (적절한 저장 방법을 선택한다)
서버 API 스펙
API 문서: http://54.180.95.212:8080/swagger-ui/index.html
관리자 페이지
상품 관리: http://54.180.95.212:8080/admin
상품 추가, 삭제
설정: http://54.180.95.212:8080/settings
계정 정보 확인(사용자 아이디, 비밀번호)

# 2 단계

## 기능 요구사항

장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.  
별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다. (쿠팡 UX 참고)  
상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.  
예를 들어 가장 최근에 본 상품이 fashion 카테고리라면, fashion 상품 10개 노출  
해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출  
장바구니에 이미 추가된 상품이라면 미노출  
추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.

## 프로그래밍 요구 사항

서버 통신을 Retrofit으로 리팩터링한다.
서버 통신을 위한 JSON 직렬화 라이브러리를 선택하고 PR에 선택 이유를 남긴다.
기능 요구 사항에 대한 테스트를 작성해야 한다.

# 리팩토링

- [x] 사용하지 않는 클래스, 메서드 제거
- [x] shoppingcartRepository 를 분리하기
- [x] 쇼핑 장바구니 remove 동작 버그 픽스
- [x] cart 에서 개수는 삼품 종류 개수가 아니라, 실제 상품 개수이다. 버그 픽스
- [x] 주문 페이지 OrderFragment
    - [x] 추천 상품 불러오기
    - [x] 추천 상품을 장바구니에 담을 수 있다.
        - [x] 뷰모델에서 productsInCart 만들기 라이브 데이터
        - [x] 리사이클러뷰의 + 를 누르면 productsInCart 에 담긴다.
        - [x] onIncrease, onDecrease 구현
    - [x] 주문하기

- [x] 장바구니에서 상품을 담을 때 직접 넘기지 말고, 레포지토리에 저장해두고, 가져오는 거로 하자.
    - [x] 레포지토리가 저장하는 게 아니라 레포지토리가 가진 데이터 소스에서 저장하도록 변경하기

- [x] 리스트 어댑터로 마이그레이션
    - [x] shopping cart
    - [x] product list
    - [x] prouct history

- [x] 주문에서의 버그를 픽스해야 함.
    - [x] 주문 데이터 소스에서 클리어해야 하는 시점!
    - [x] 예를 들어 주문 화면에서 주문 아이템에 데이터를 넣고 나서 뒤로 가면, 주문 아이템에는 데이터를 여전히 가지고 있다.

- [ ] navigation 을 더 MVVM 답게

- [x] local.properties 에 url, id, password 저장.
- [x] gson 선택 이유, adapter 의 생성 시점과 방법.
- [x] Retrofit 의 execute, enqueue 차이, 장/단점 고찰.
- [x] 여러 url 을 사용한다면?

# 3 단계

Coroutines을 적용하여 비동기 요청을 리팩터링한다.
단, Flow를 사용하진 않는다.

- [x] 모든 데이터 소스에 suspend, Result 적용
- [x] 모든 Repository 에 suspend, Result 적용
- [x] Repository 테스트 추가
- [x] 뷰모델에 코루틴 적용하기



- [x] 상품 추천 관련
  - [x] productsRecommendationRepository 의 함수에 파라미터를 제거.
  - [x] CategoryBasedProductRecommendationRepository 의 생성자에 historySource 추가.



