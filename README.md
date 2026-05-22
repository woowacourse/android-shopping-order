# android-shopping-order

# 🚀 3단계 - Navigation & Flow & 4단계 - 주문

## 3단계 구현 기능 목록

### Navigation 구조 전환

- [x] `navigation-compose` 의존성 추가
- [ ] 기존 Activity 기반 화면 전환 로직 제거
- [ ] `startActivity()`, `finish()`, `onRestart()` 기반 흐름 제거
- [ ] 단일 Activity에서 `NavHost`를 사용해 화면 전환 처리
- [ ] 상품 목록 화면을 Navigation 목적지로 등록
- [ ] 상품 상세 화면을 Navigation 목적지로 등록
- [ ] 장바구니 화면을 Navigation 목적지로 등록
- [ ] 상품 추천 화면을 Navigation 목적지로 등록

### Route 구성

- [ ] 모든 화면 Route를 `@Serializable` 타입으로 선언
- [ ] 상품 상세 화면 이동 시 상품 ID를 Route로 전달
- [ ] 장바구니 화면 Route 정의
- [ ] 상품 추천 화면 Route 정의
- [ ] 문자열 기반 Route 사용을 최소화

### 화면 이동 처리 개선

- [ ] 각 화면 이동을 `NavController`를 통해 처리
- [ ] `NavController`를 화면 Composable에 직접 전달하지 않기
- [ ] 화면 이동 로직을 콜백 람다로 분리
- [ ] 상품 목록에서 상품 상세로 이동하는 콜백 연결
- [ ] 상품 상세에서 장바구니로 이동하는 콜백 연결
- [ ] 장바구니에서 주문 완료 후 상품 목록으로 이동하는 콜백 연결
- [ ] 상품 추천은 CartScreen 내부 CartFlow 상태 분기로 처리 

### 주문 완료 후 Back Stack 정리

- [ ] 주문 완료 이벤트 발생 시 상품 목록 화면으로 이동
- [ ] 주문 완료 후 장바구니/추천 화면이 Back Stack에 남지 않도록 처리
- [ ] `popUpTo()`를 사용해 주문 흐름 제거
- [ ] 뒤로가기 시 주문 완료 이전 화면으로 돌아가지 않도록 검증

### SharedFlow 기반 단발성 이벤트 처리

- [ ] 장바구니 추가 완료 이벤트를 `SharedFlow`로 처리
- [ ] 장바구니 삭제 완료 이벤트를 `SharedFlow`로 처리
- [ ] 주문 완료 이벤트를 `SharedFlow`로 처리
- [ ] 스낵바 표시 이벤트를 `SharedFlow`로 처리
- [ ] 화면 이동 트리거 이벤트를 `SharedFlow`로 처리
- [ ] ViewModel의 `MutableSharedFlow`를 외부에 `SharedFlow`로 노출

### 상품 추천 화면 처리

- [ ] 주문하기 버튼에서 CartFlow 전환 로직 구현
- [ ] 추천 상품 추가/수정/삭제 시 CartViewModel의 메서드 호출
- [ ] 주문 완료 시 CartFlow.CART에서 CartFlow.RECOMMEND로 진행
- [ ] 주문 최종 완료 후 ProductListRoute로 탈출 (Back Stack 정리 포함)

### Compose 상태 관리 정리

- [ ] 화면 전환과 관련된 로컬 Compose State 제거
- [ ] Navigation 흐름은 ViewModel 이벤트 또는 상위 콜백으로 처리
- [ ] 남아 있는 `remember/mutableStateOf` 사용 위치 점검
- [ ] `ShimmerEffect`의 로컬 상태는 UI 측정용 예외로 유지할지 검토
