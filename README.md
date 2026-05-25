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

## 🚀 4단계 - 주문

### 주문

- [x] 배송비 3,000원을 적용한다.
- [x] 적용 가능한 쿠폰을 조회하고 적용한다.
    - [x] 각 쿠폰에 대한 할인이 정확히 적용되어야 한다.
    - 쿠폰은 1개만 적용 가능
- [x] 결제하기 버튼 클릭 시 최종 주문 완료

### 알림

- [x] 알림 권한(POST_NOTIFICATIONS) 요청
- [x] 설정에서 알림 기능을 On/Off
    - [x] SharedPreferences로 설정 상태 유지
- [x] 알림 예약/취소(AlarmManager)
    - [x] 결제 화면 진입 시 5분 후 알림 예약
    - [x] 결제 완료 또는 결제 화면 재진입 시 기존 알림 취소
- [x] 알림 수신/노출
    - [x] BroadcastReceiver를 통해 알림 수신
    - [x] NotificationManager로 알림 노출
        - [x] 5분 안에 결제하지 않을 시, `"아직 결제가 완료되지 않았어요"` 메시지 노출
        - [x] 알림 클릭 시, 결제 화면으로 이동한다.
