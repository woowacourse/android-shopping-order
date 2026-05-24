# android-shopping-order

## 🚀 3단계 - Navigation & Flow

### Navigation

- [x] 기존 Activity 전환 방식을 Compose Navigation으로 교체
    - [x] 각 화면을 Navigation Component로 구성
    - [x] 각 화면 이동을 NavController를 통해 처리
    - [x] 모든 Route를 @Serializable 타입으로 선언한다.

- [x] 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 처리
- [x] NavController는 화면 Composable에 직접 전달하지 않는다. 이동 로직은 콜백 람다로 분리한다.

### Flow

- [x] UI에 노출할 상태를 StateFlow로 관리한다.
- [x] 단발성 이벤트는 MutableSharedFlow를 사용한다.
- [x] composable에서 상태 구독 시, collectAsStateWithLifecycle()을 사용한다.
