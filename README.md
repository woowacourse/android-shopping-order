# 기능 구현 사항

## 3단계 구현 기능 사항
- [x] Compose Navigation으로 화면 전환
- [x] 화면 전환 시 필요한 데이터는 Route로 처리
- [x] RecommendScreen, RecommendViewModel 분리
- [x] ViewModel의 UI 상태를 StateFlow로 노출
- [x] 일회성 이벤트는 MutableSharedFlow를 사용

## 4단계 기능 구현 사항
- [x] 결제 화면 구현
- [x] 쿠폰 Api 로직 구현
- [x] 쿠폰 적용 로직 구현
- [x] 결제 화면 진입 시 5분 후 알림 예약
  - [x] 결제 완료 시 알림 취소
  - [x] 결제 화면으로 돌아올 시 타이머 재세팅
  - [x] 알림 클릭 시 결제 화면으로 이동 
- [x] 최종 주문 완료
  - [x] 상품 목록 페이지로 이동
  - [x] BackStack Entry 제거
  - [x] 장바구니 초기화
- [ ] 설정창 미결제 알림 on/off 토글 제공
