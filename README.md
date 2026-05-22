# 기능 구현 사항

## Compose Navigation 화면 전환
- [x] 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- [x] 각 화면 이동은 NavController를 통해 처리한다.
    - 기존 Activity 전환 방식을 제거하고 Compose Navigation(navigation-compose)으로 교체한다.
    - 모든 Route를 @Serializable 타입으로 선언한다.
    - 화면 이동 시 전달하는 데이터는 타입 안전한 Route를 사용한다.
    - NavController는 화면 Composable에 직접 전달하지 않는다. 이동 로직은 콜백 람다로 분리한다.

## Flow
- [x] ViewModel의 UI 상태를 StateFlow로 노출한다.
  - 기존 remember/mutableStateOf 기반 Compose State를 StateFlow/SharedFlow로 교체한다.
  - ViewModel의 상태는 MutableStateFlow로 선언하고 StateFlow로 노출한다.
- [x] 장바구니 담기/삭제 등 단발성 이벤트는 SharedFlow로 처리한다.
- [x] 일회성 이벤트(스낵바 표시, 화면 이동 트리거 등)는 MutableSharedFlow를 사용한다.
- [x] Composable에서 상태를 구독할 때 collectAsStateWithLifecycle()을 사용한다.

## 결제 
- [x] 결제 UI 구현
- [x] 결제 스크린 네비게이션 구현
- [ ] 사용 가능한 쿠폰을 조회한다
- [ ] 결제 UI 에서 쿠폰을 적용한다
  - 쿠폰은 1개만 적용 가능하다
  - 해당 쿠폰을 적용했을 때의 할인 효과를 정확히 계산할 수 있도록 구현한다
  - 쿠폰은 사용해도 사라지지 않는다
- [ ] 결제하기 버튼을 누르면 최종 주문이 완료된다
  - 결제 수단은 구현하지 않으며 최종 주문이 완료되면 상품 목록으로 이동한다
  - 최종 주문이 완료되면 장바구니에서 주문된 상품이 초기화된다.
  - 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 한다.

## 알림
- [ ] 알림 권한(POST_NOTIFICATIONS)을 요청한다
- [ ] 결제 화면에 진입 시 5분 후 알림이 예약된다
  - 결제를 완료하거나 결제 화면으로 돌아오면 예약된 알림을 취소한다
  - 5분 안에 결제하지 않으면 "아직 결제가 완료되지 않았어요" 알림을 노출한다
  - 알림을 클릭하면 결제 화면으로 이동한다

## 설정
- [ ] 설정에서 미결제 알림 기능을 On/Off 할 수 있다
  - 앱을 재실행해도 설정이 유지되어야 한다.
  - 알림 On/Off 설정은 SharedPreferences로 저장한다.