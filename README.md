# android-shopping-order

### JSON 직렬화 라이브러리 선택 이유
- selected: GSON
- why gson is good to use: 
- reason: One of generally used Library, simple, easy to use, good compatibility with Java
- I trust the server will not return null type in this project

## Step1  ☝️

- 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다

- 서버를 연동한다.
    - 기존 작성한 테스트가 깨지면 안 된다
- 사용자 인증 정보를 저장한다.
    - 적절한 저장 방법을 선택한다

API 문서: http://54.180.95.212:8080/swagger-ui/index.html   
관리자 페이지

상품 관리: http://54.180.95.212:8080/admin  
상품 추가, 삭제

설정: http://54.180.95.212:8080/settings  
계정 정보 확인(사용자 아이디, 비밀번호)


## Step2  2️⃣

- 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.

- 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천해준다. (쿠팡 UX 참고)
- 상품 추천 알고리즘은 최근 본 상품 카테고리를 기반으로 최대 10개 노출한다.
    - 예를 들어 가장 최근에 본 상품이 fashion 카테고리라면, fashion 상품 10개 노출
    - 해당 카테고리 상품이 10개 미만이라면 해당하는 개수만큼만 노출
    - 장바구니에 이미 추가된 상품이라면 미노출
- 추천된 상품을 해당 화면에서 바로 추가하여 같이 주문할 수 있다.

### 프로그래밍 요구 사항
- 서버 통신을 Retrofit으로 리팩터링한다.
- 서버 통신을 위한 JSON 직렬화 라이브러리를 선택하고 PR에 선택 이유를 남긴다.
- 기능 요구 사항에 대한 테스트를 작성해야 한다.
