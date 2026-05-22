## 기능 구현 목록

### Navigation

- [ ] Compose Navigation 의존성 추가
- [ ] Route 정의
  - Shopping
  - ProductDetail
  - Cart
  - Recommendation
- [ ] Single Activity 전환
  - [ ] AndroidManifest 수정
  - [ ] MainActivity 이외의 Activity 제거
  - [ ] 각 Route 객체 연결

### Flow

- [ ] 일회성 이벤트 SharedFlow 적용
  - [ ] 장바구니 담기
  - [ ] 장바구니 삭제
  - [ ] 상품 더보기
  - [ ] 페이지 이동
  - [ ] 장바구니 페이지 이동
  - [ ] 상품 수량 변경
  - [ ] 선택 장바구니 상품 ID 저장

### 주문

- [ ] 쿠폰
  - [ ] 쿠폰 도메인 객체 정의
  - [ ] 쿠폰 Repository 정의
  - [ ] 쿠폰 api 호출 함수 정의
    - [ ] ApiResult를 통한 예외처리 적용
  - [ ] 쿠폰 DTO 정의

- [ ] 주문
  - [ ] 주문 Api 호출 정의
    - [ ] ApiResult를 통한 예외처리 적용
  - [ ] 주문한 상품은 장바구니에서 제거

### 알람

- [ ] SharedPreference로 알람 허용 여부 저장
- [ ] AlarmManager로 결제 화면 진입 시 알람 예약 생성
- [ ] 결제 화면 재진입 또는 결제 완료시 알람 예약 취소
- [ ] BroadcastReceiver 클래스 정의
  - [ ] AndroidManifest <receiver> 등록
- [ ] 예약된 시간이 되면 BroadcastReceiver를 호출
- [ ] onReceive 메서드 내에서 알림 빌드
- [ ] 알람 클릭시 결제 화면으로 이동
