## 🚀 3단계 - Navigation & Flow

## 3단계 기능 및 작업 명세

### Compose Navigation 전환

- `navigation-compose`를 사용해 단일 Activity 기반 화면 전환 구조를 구성한다.
- 기존 Activity 전환 방식을 제거하고 Compose Navigation으로 대체한다.
- 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component의 목적지로 등록한다.
- 각 화면 이동은 `NavController`를 통해 처리한다.

### 타입 안전 Route 구현

- 모든 Route를 `@Serializable` 타입으로 선언한다.
- 화면 이동 시 필요한 데이터는 문자열 경로 조합이 아닌 타입 안전한 Route로 전달한다.
- 상품 상세 화면 이동에 필요한 상품 식별자를 Route 데이터로 전달한다.
- 상품 추천 화면 이동에 필요한 진입 정보를 Route 데이터로 전달한다.

### 화면별 이동 콜백 분리

- 화면 Composable에 `NavController`를 직접 전달하지 않는다.
- 상품 목록 화면은 상품 상세, 장바구니, 상품 추천 화면 이동을 콜백 람다로 요청한다.
- 상품 상세 화면은 뒤로 가기와 장바구니 담기 이후 필요한 이동을 콜백 람다로 요청한다.
- 장바구니 화면은 주문 완료, 상품 목록 복귀, 추천 상품 상세 이동을 콜백 람다로 요청한다.
- 상품 추천 화면은 추천 상품 상세 이동과 이전 화면 복귀를 콜백 람다로 요청한다.

### 주문 완료 후 Back Stack 정리

- 주문 완료 후 상품 목록 화면으로 이동한다.
- 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 `popUpTo` 또는 동등한 Navigation 옵션을 적용한다.
- 주문 완료 이후 뒤로 가기 시 장바구니 또는 주문 흐름으로 되돌아가지 않도록 한다.

### UI 상태 StateFlow 전환

- ViewModel의 UI 상태를 `MutableStateFlow`로 관리한다.
- 외부에는 변경 불가능한 `StateFlow`로 UI 상태를 노출한다.
- 기존 `remember`/`mutableStateOf` 기반 화면 상태를 ViewModel의 `StateFlow` 상태로 이전한다.
- 상품 목록, 상품 상세, 장바구니, 상품 추천 화면의 로딩/성공/오류 상태를 Flow 기반으로 표현한다.

### 단발성 이벤트 SharedFlow 처리

- 장바구니 담기 성공, 삭제 성공, 실패 메시지 등 일회성 이벤트를 `MutableSharedFlow`로 발행한다.
- 외부에는 변경 불가능한 `SharedFlow`로 이벤트를 노출한다.
- 스낵바 표시, 화면 이동 트리거처럼 한 번만 소비되어야 하는 동작을 SharedFlow로 처리한다.
- 상태 값으로 이벤트를 보관해 재구성 시 중복 실행되는 문제를 방지한다.

### Lifecycle-aware 상태 구독

- Composable에서 `collectAsState()` 대신 `collectAsStateWithLifecycle()`을 사용한다.
- 화면별 UI 상태는 Lifecycle을 고려해 수집한다.
- SharedFlow 이벤트는 `LaunchedEffect`에서 수집하고 Composable 재구성 시 중복 수집되지 않도록 한다.

## 3단계 테스트 명세

### 기존 테스트 회귀 확인

- 기존에 작성된 단위 테스트가 모두 통과한다.
- 상품 목록, 상품 상세, 장바구니, 상품 추천 관련 기존 기능이 깨지지 않는다.

### Navigation 동작 검증

- 상품 목록에서 상품을 선택하면 상품 상세 화면으로 이동한다.
- 상품 목록에서 장바구니 화면으로 이동할 수 있다.
- 장바구니에서 상품 추천 화면으로 이동할 수 있다.
- 추천 상품을 선택하면 상품 상세 화면으로 이동한다.
- 주문 완료 후 상품 목록으로 이동하며 주문 흐름이 Back Stack에 남지 않는다.

### Flow 상태 관리 검증

- ViewModel의 UI 상태가 `StateFlow`로 노출된다.
- 장바구니 담기/삭제 등 일회성 이벤트가 `SharedFlow`로 발행된다.
- Composable이 `collectAsStateWithLifecycle()`로 상태를 구독한다.
- 화면 재구성 후에도 스낵바나 화면 이동 이벤트가 중복 실행되지 않는다.
