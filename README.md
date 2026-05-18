# Android Shopping Cart

현재 HTTP 400 에러를 반환 및 빌드가 제대로 되지 않고 있습니다.

다음 제출부터는 이러한 부분도 잘 확인해주세요.

다음 시나리오들을 각각 실행해보시고 어떤 문제가 발생하고 있는지 확인해보시겠어요?

1. 상품 리스트 화면에서 아이템 추가를 빠르게 반복하여 클릭한다.
2. 장바구니 화면에서 상품을 모두 선택한 뒤 상품을 삭제한다.
3. 마지막으로 본 상품이 보이는가?

# 🚀 3단계 - 상태 관리 | 4단계 - HTTP Client
## 핵심 기능

- 상품 목록 조회 및 페이지 로드
- 상품 수량 `+/-` 조작
- 장바구니 담기/삭제
- 상품 목록, 상세, 장바구니 간 수량 동기화(SSOT)
- 최근 본 상품(최신순, 최대 10개) 관리
- 앱 재시작 이후 장바구니/최근 방문 데이터 유지

## 미션 요구사항 반영

- 로컬 상태 관리를 위해 InMemory Repository 사용
- HTTP Client(OkHttp) 구현
- MockWebServer 기반 테스트 서버 구성

## 패키지 구조

```text
app/src/main/java/woowacourse/shopping
├─ ShoppingApplication.kt
├─ activity
│  ├─ ProductListActivity.kt
│  ├─ DetailProductActivity.kt
│  └─ ShoppingCartActivity.kt
├─ ui
│  ├─ ProductListScreen.kt
│  ├─ DetailProductScreen.kt
│  ├─ ShoppingCartScreen.kt
│  ├─ component
│  └─ pagination
├─ viewmodel
│  ├─ ProductListViewModel.kt
│  ├─ DetailProductViewModel.kt
│  └─ ShoppingCartItemViewModel.kt
├─ repository
│  ├─ ShoppingItemRepository.kt
│  ├─ ShoppingCartRepository.kt
│  ├─ InMemoryShoppingItemRepository.kt
│  └─ InMemoryShoppingCartRepository.kt
├─ backend
│  ├─ MockProductSeedData.kt
│  ├─ MockShoppingBackendServer.kt
│  ├─ ProductBackendDataSource.kt
│  ├─ OkHttpProductBackendDataSource.kt
│  └─ ShoppingItemsRemoteSyncer.kt
├─ storage
│  └─ datastore
│     ├─ VisitStore.kt
│     └─ DataStoreVisitStore.kt
└─ model
   ├─ Product.kt
   ├─ ProductTitle.kt
   ├─ Price.kt
   ├─ ShoppingItem.kt
   └─ ShoppingCartItem.kt
```

## 화면별 동작 규칙

### 상품 목록

- 상품은 20개 단위 페이지로 표시
- 수량이 0이면 장바구니 추가 버튼 노출
- 수량이 1 이상이면 `+/-` 컨트롤 노출
- 상단 장바구니 아이콘 배지는 전체 수량 합 표시

### 상품 상세

- 기본 선택 수량: 1
- `+/-`는 상세의 선택 수량만 변경
- `장바구니에 담기` 클릭 시에만 실제 수량 반영
- 최근 본 상품 영역은 "현재 상품과 다른 마지막 방문 상품 1개"만 노출

### 장바구니

- 페이지당 5개 노출
- `+/-` 조작 시 상품 목록/상세와 동일 수량으로 즉시 동기화
- 수량이 0이 되면 장바구니에서 제거
- 항목 삭제 시 해당 상품 수량도 0으로 초기화

## 체크리스트

### 3단계

- [x] 상품 목록에서 `+` 버튼 클릭 시 장바구니 수량 증가
- [x] 목록/상세/장바구니 간 수량 동기화
- [x] 수량 0이면 장바구니 항목 제거
- [x] 수량 음수 방지
- [x] 재시작 이후 수량 유지
- [x] 공통 수량 컴포넌트 동작
- [x] 장바구니 `x` 삭제 동작

### 4단계

- [x] 최근 본 상품 최신순 정렬, 최대 10개 유지
- [x] 상세 화면에서 마지막 방문 상품 1개 표시
- [x] 마지막 방문 상품 선택 시 해당 영역에서 제외
- [x] 마지막 방문 상품 상세에서 뒤로 가기 시 상품 목록 이동
- [x] 재시작 후 최근 방문/장바구니 데이터 유지

---

## 🚀 1단계 - 서버 연동

### 진행 방식

- 기능 구현 전 README에 기능 목록을 정리한다.
- 기능 목록 단위로 커밋을 나눈다.
- AngularJS Git Commit Message Conventions을 참고해 커밋 메시지를 작성한다.
- 요구 사항에 없는 세부 구현은 팀에서 판단해 결정한다.

### 기능 요구 사항

- 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.

### 프로그래밍 요구 사항

- 서버를 연동한다.
- 기존에 작성한 테스트가 깨지면 안 된다.
- 사용자 인증 정보를 저장한다.
- 서버 통신을 위한 JSON 직렬화 라이브러리를 선택하고, 선택 이유를 PR에 남긴다.

### 서버 API 참고

- API 문서: `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/swagger-ui/index.html`
- 관리자 페이지(상품 관리): `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/admin`
- 설정 페이지(계정 정보 확인): `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/settings`

### 1단계 기능 목록

- [x] 상품 목록을 서버에서 조회한다.
- [x] 서버 응답 DTO를 도메인 모델로 변환한다.
- [x] 데이터 로딩 전 스켈레톤 UI를 노출한다.
- [x] 사용자 인증 정보를 저장한다.
- [x] 저장된 인증 정보를 서버 요청에 사용한다.
- [X] Kotlinx Serialization을 사용해 JSON을 직렬화/역직렬화한다.

## 🚀 2단계 - 상품 추천

### 진행 방식

- 기능 구현 전 README에 기능 목록을 정리한다.
- 기능 목록 단위로 커밋을 나눈다.
- AngularJS Git Commit Message Conventions을 참고해 커밋 메시지를 작성한다.
- 요구 사항에 없는 세부 구현은 팀에서 판단해 결정한다.

### 기능 요구 사항

- 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천한다.
- 추천 상품은 최근 본 상품의 카테고리를 기준으로 최대 10개 노출한다.
- 해당 카테고리 상품이 10개 미만이면 가능한 개수만 노출한다.
- 장바구니에 이미 추가된 상품은 추천 목록에서 제외한다.
- 추천 상품을 해당 화면에서 바로 장바구니에 추가하고 함께 주문할 수 있다.

### 프로그래밍 요구 사항

- 기능 요구 사항에 대한 테스트를 작성해야 한다.

### 2단계 기능 목록

#### 장바구니 선택 주문

- [x] 장바구니 상품을 선택할 수 있다.
- [x] 선택한 상품만 주문하기 버튼을 누를 수 있다.
- [x] 선택한 상품이 없으면 주문할 수 없다.

#### 최근 본 상품 기반 추천

- [x] 사용자가 최근 본 상품의 카테고리를 저장한다.
- [x] 가장 최근에 본 상품의 카테고리를 기준으로 추천 상품을 조회한다.
- [x] 같은 카테고리 상품을 최대 10개까지 노출한다.
- [x] 이미 장바구니에 담긴 상품은 추천 목록에서 제외한다.

#### 추천 상품 장바구니 추가

- [X] 추천 상품 화면에서 상품을 바로 장바구니에 추가할 수 있다.
- [x] 추천 상품을 추가하면 함께 주문할 수 있다.
