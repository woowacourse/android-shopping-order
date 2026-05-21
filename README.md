# 쇼핑 주문 (3 ~ 4단계)

## 단계별 진행

### 3단계 — Navigation & Flow

## 기능 목록

- [ ] 기존 Activity 전환 제거 및 단일 Activity + Compose Navigation 도입
- [ ] @Serializable Route 타입 정의 (ProductList/ProductDetail/Cart/Recommend)
- [ ] 상품 목록 -> 상품 상세 이동 및 값 전달
- [ ] 상품 목록 -> 장바구니 이동
- [ ] 장바구니 -> 상품 추천 이동 및 값 전달
- [ ] 상품 추천에서 주문 완료 후 뒤로가기를 누르면 상품 목록으로 복귀한다.
- [ ] 장바구니 담기 완료 이벤트 SharedFlow (스낵바)
- [ ] 장바구니 삭제 완료 이벤트 SharedFlow (스낵바)

#### 기능 요구사항

### Compose Navigation으로 화면 전환을 구현한다

- 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- 각 화면 이동은 NavController를 통해 처리한다.
- 화면 이동 시 전달하는 데이터는 타입 안전한 Route를 사용한다.
- 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 한다.

### UI 상태를 Flow로 관리한다

- ViewModel의 UI 상태를`StateFlow`로 노출한다.
- 장바구니 담기/삭제 등 단발성 이벤트는`SharedFlow`로 처리한다.
- Composable에서 상태를 구독할 때`collectAsStateWithLifecycle()`을 사용한다.

#### 프로그래밍 요구사항

### Navigation

- 기존 Activity 전환 방식을 제거하고Compose Navigation(`navigation-compose`)으로 교체한다.
- 모든 Route를`@Serializable`타입으로 선언한다.
- NavController는 화면 Composable에 직접 전달하지 않는다. 이동 로직은 콜백 람다로 분리한다.

### Flow

- 기존`remember`/`mutableStateOf`기반 Compose State를`StateFlow`/`SharedFlow`로 교체한다.
- ViewModel의 상태는`MutableStateFlow`로 선언하고`StateFlow`로 노출한다.
- 일회성 이벤트(스낵바 표시, 화면 이동 트리거 등)는`MutableSharedFlow`를 사용한다.
- Composable에서`collectAsState()`대신`collectAsStateWithLifecycle()`을 사용한다.
