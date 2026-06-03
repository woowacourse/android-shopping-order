## 기능 구현 목록

### Navigation

- [x] Compose Navigation 의존성 추가
- [x] Routes 정의
  - Shopping
  - ProductDetail
  - Cart
  - Recommendation
- [x] Single Activity 전환
  - [x] AndroidManifest 수정
  - [x] MainActivity 이외의 Activity 제거
  - [x] 각 Route 객체 연결

### Flow

- [x] 일회성 이벤트 SharedFlow 적용
  - [x] 장바구니 담기
  - [x] 장바구니 삭제
  - [x] 상품 더보기
  - [x] 페이지 이동
  - [x] 장바구니 페이지 이동
  - [x] 상품 수량 변경
  - [x] 결제 예정 상품 ID 저장

### 주문

- [x] 쿠폰
  - [x] 쿠폰 도메인 객체 정의
  - [x] 쿠폰 Repository 정의
  - [x] 쿠폰 api 호출 함수 정의
    - [x] ApiResult를 통한 예외처리 적용
  - [x] 쿠폰 DTO 정의

- [x] 주문
  - [x] 주문 Api 호출 정의
    - [x] ApiResult를 통한 예외처리 적용
  - [x] 주문한 상품은 장바구니에서 제거

### 알림

- [x] SharedPreference로 알림 허용 여부 저장
- [x] AlarmManager로 결제 화면 진입 시 알림 예약 생성
- [x] 결제 완료시 알림 예약 취소
- [x] BroadcastReceiver 클래스 정의
  - [x] AndroidManifest <receiver> 등록
- [x] 예약된 시간이 되면 BroadcastReceiver를 호출
- [x] onReceive 메서드 내에서 알림 빌드
- [x] 알림 클릭시 결제 화면으로 이동

## 1차 피드백 수정사항

1. [x] build.gradle.kts 에서 BASE_URL에 대한 경고 메시지 추가
2. [x] Order 객체 calculateFinalPrice() 메서드에 0월 이하의 반환값 제한
3. [x] viewModel isLoading 상태 변경 점검
4. [x] 알림 클릭시 LaunchedEffect가 아닌 DeepLink를 통해 화면을 이동
5. [x] 주문 중복 호출 방지를 위한 isOrdering 상태 추가
6. [x] 스낵바 이벤트 emit 처리 점검
7. [x] replaceAll() 트랙젝션 추가
8. [ ] 사용자 인증 정보 및 설정값 DataSource 객체 추가
9. [ ] DiscountType Enum 클래스 추가
10. [ ] 정확한 알림 사용 제거
11. [ ] 알림 클릭 시 생성되는 intent의 flag 수정
12. [ ] navController 의존 관계 제거 및 콜백 함수 추가
13. [ ] 스낵바 이벤트 처리를 컴포저블에서 수행
    - [ ] strings.xml을 통해 스낵바에 표시할 문자열 관리
14. [ ] viewModel에서 ApiResult 처리에 대한 중복되는 코드 함수화
