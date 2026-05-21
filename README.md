## 📋 기능 구현 목록 (Roadmap)

작업은 아래 순서대로 진행하며, 완료된 기능은 `[x]`로 표시합니다.

### 🚀 1단계 - 서버 연동

- **네트워크 통신 환경 설정**

    - [x] Retrofit2 및 OkHttp3 의존성 추가
    - [x] 서버 API 통신 인터페이스 정의 (`ProductService`, `CartService`)

- **데이터 모델링**
    - [x] 서버 응답 DTO 설계 및 도메인 모델 변환 로직(`toDomain`) 구현
    - [x] `kotlinx-serialization` 설정
- **인증 정보 관리**
    - [x] DataStore를 활용한 사용자 인증 정보 저장 및 관리
    - [x] 모든 네트워크 요청에 Basic Auth 헤더 자동 포함 구현
- **상품 목록 기능**
    - [x] 상품 목록 조회 및 페이징(무한 스크롤) 구현
    - [x] 데이터 로딩 상태 스켈레톤 UI 적용
- **장바구니 기능**
    - [x] 장바구니 항목 추가, 수량 변경, 삭제 구현

### 🚀 2단계 - 상품 추천 및 주문 고도화

- **장바구니 선택 주문 UI**
    - [x] 장바구니 각 항목 체크박스 추가 (선택/해제)
    - [x] 전체 선택 체크박스 구현
    - [x] 실시간 총 주문 금액 및 개수 표시 UI 구현
- **상품 추천 알고리즘**
    - [x] 최근 본 상품의 카테고리 분석 로직 구현
    - [x] 최대 10개 노출 제한 및 카테고리 상품 매칭 로직
- **추천 상품 인터랙션**
    - [x] 장바구니 내 추천 상품 섹션 UI (LazyRow)
    - [x] 추천 상품 즉시 장바구니 추가 기능 구현
- **단위 테스트 작성**
    - [ ] 선택 주문 로직 테스트 작성
    - [ ] 추천 알고리즘 단위 테스트 작성

### 🧹 1차 피드백 반영 내용

- **프로젝트 빌드 실패**
  - [x] `CLEARTEXT` 허용 문구 추가

- **RecentlyViewedProductRepositoryImpl**
  - [x] getAll() 함수의 반환타입을 List<Long> 타입으로 변경해 viewModel과 data 계층의 의존성 제거

- **UserAuthDataStore**
  - [x] StateFlow를 사용하도록 변경
  - [x] applicationContext 제한 추가

- **WebServerResponse**
  - [x] MockProductResponse로 네이밍 변경

- **data/mock**
  - [x] 테스트 디렉터리로 이동

- **MockProductRepositoryImpl**
  - [x] getCategoryProduct에 Dispatcher.IO 명시

- **domain**
  - [x] @Percelizable 어노테이션 제거 및 상속관계 제거

- **CartViewModel**
  - [x] `전체 체크` 체크박스와 개별 체크박스 상태 동기화

- **RecommendationViewModel**
  - [x] 체크한 주문의 id를 주입받는다
  - [x] 주문할 상품의 총 가격을 주입받는다
  - [x] 가장 마지막으로 조회한 상품의 id를 조회한다
  - [x] 조회한 아이디로 해당 상품의 카테고리를 조회한다
  - [x] 서버에서 카테고리 별 상품 리스트를 조회한다
  - [x] 추천 상품을 바로 장바구니에 추가할 수 있다
  - [x] 추천 상품을 장바구니에 추가하면 총 가격에 반영된다

### 🧹 2차 피드백 반영 내용

- [x] ROOM에서 들고 있는 비즈니스 로직 Repository로 이동
- [x] UserAuthDataStore 코루틴 스코프 맴버 변수 추가
- [x] encodedUserAuthInfo의 쓰레드 풀을 Default로 변경
- [x] BASE_URL 네이밍 컨벤션 적용
- [ ] 네트워크 api 호출에 대한 예외처리 추가
  - [ ] api 호출의 결과를 Result 객체로 감싸기
  - [ ] success와 failure에 대한 처리 추가
- [ ] CatalogScreen 콜백 함수 람다 제거
- [ ] ProductImage 이미지 로드 실패 시 표시할 에러 이미지 추가
- [ ] RecentlyViewedProducts LazyRow의 key값의 index 결합 제거
- [x] 각 viewModel에 UiModel 도입
- [ ] 액티비티 별 startActivity companion object 추가
- [] screenComposable의 파라미터로 viewModel 주입