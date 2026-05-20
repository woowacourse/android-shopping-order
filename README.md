# 쇼핑 주문 (3 ~ 4단계)

## 단계별 진행

## 🚀 3단계 - Navigation & Flow

### 기능 요구사항

#### Compose Navigation으로 화면 전환을 구현한다

- [x] 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- [x] 각 화면 이동은 NavController를 통해 처리한다.
- [x] 화면 이동 시 전달하는 데이터는 타입 안전한 Route를 사용한다.
- [x] 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 한다.

#### UI 상태를 Flow로 관리한다

- [x] 장바구니 담기/삭제 등 단발성 이벤트는 SharedFlow로 처리한다.
  - [x] ProductDetail에서 장바구니에 담는 이벤트 처리
  - [x] Cart에서 장바구니 상품 삭제하는 이벤트 처리

### 프로그래밍 요구사항

#### Navigation

- [x] 기존 Activity 전환 방식을 제거하고 Compose Navigation(navigation-compose)으로 교체한다.
- [x] 모든 Route를 @Serializable 타입으로 선언한다.
- [x] NavController는 화면 Composable에 직접 전달하지 않는다. 이동 로직은 콜백 람다로 분리한다.

#### Flow

- [x] 일회성 이벤트(스낵바 표시, 화면 이동 트리거 등)는 MutableSharedFlow를 사용한다.
