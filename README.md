# 기능 구현 사항

## 피드백 반영
### From 토끼
- [ ] 선택 삭제 중 일부 요청 실패 시 상태 동기화 미스 문제
- [ ] 비정상 쿠폰 방어 로직
- [ ] 초기 로딩 실패 이벤트 유실 이슈
- [ ] 알림 버튼 토글 시 기존 알림 이벤트 삭제
- [ ] 쿠폰 체크박스 선택 이슈
- [ ] 배송비 기본값 단일 소스
- [ ] 알림 권한 요청 관련 분기
- [ ] 오류 메시지와 실제 실패 이슈 간 동기화

### From 코니
- [x] 기본 배송비 책임 소재 정리를 위한 PaymentPrice 모델 작성
- [x] string.xml 내 인텐트 식별자 제거 및 object 파일 신설
- [x] NavHost의 책임
- [x] SettingViewModel 생성
- [x] Coupon 타입의 동작을 타입 내부에 내재화

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
- [x] 설정창 미결제 알림 on/off 토글 제공
