# 🚀 4단계 - 주문 & 알림

## 🎯 기능 목록

### 환경 및 인프라 설정
- [ ] 알림 권한(POST_NOTIFICATIONS) 처리
  - [ ] `ContextCompat.checkSelfPermission`으로 권한 보유 여부 확인
  - [ ] Android 13(TIRAMISU) 이상에서만 권한 요청 수행
  - [ ] `registerForActivityResult`를 활용한 권한 요청 런처 구현
  - [ ] `shouldShowRequestPermissionRationale` 분기로 거부 케이스 처리
  - [ ] Android 12 이하에서는 권한 요청 없이 정상 동작
- [ ] AlarmManager 기반 알림 스케줄링 인프라 구축
  - [ ] 결제 화면 진입 시 5분 후 알림 예약
  - [ ] 결제 완료 또는 결제 화면 재진입 시 예약된 알림 취소
- [ ] BroadcastReceiver 구현 및 등록
  - [ ] AlarmManager로부터 브로드캐스트 수신
  - [ ] NotificationManager를 통해 알림 노출
  - [ ] Notification 아이콘은 커스텀 리소스로 지정
  - [ ] 권한 요청 Dialog와 Notification 본체는 기본 UI 사용
- [ ] SharedPreferences를 통한 설정값 영속화
  - [ ] `settings` 이름의 SharedPreferences에 알림 On/Off 값 저장
  - [ ] 앱 재실행 시에도 저장된 설정값 유지

### 도메인 로직 / 쿠폰 정책
- [ ] 쿠폰 조회 및 적용 가능 여부 판별
  - [ ] API로부터 쿠폰 데이터 조회
  - [ ] 만료일이 지난 쿠폰은 적용 불가 처리
  - [ ] 쿠폰별 최소 주문 금액 / 사용 가능 시간 조건 검증
  - [ ] 단 1개의 쿠폰만 적용 가능하도록 제약
- [ ] `FIXED5000` 5,000원 할인 쿠폰 계산 로직
  - [ ] 주문 금액이 100,000원 이상일 때만 적용
  - [ ] 최종 결제 금액에서 5,000원 차감
- [ ] `BOGO` 2개 구매 시 1개 무료 쿠폰 계산 로직
  - [ ] 동일 상품이 3개 이상 담긴 경우에만 적용
  - [ ] 조건을 만족하는 상품이 여러 개일 경우, 단가가 가장 비싼 상품에 적용
  - [ ] 1개 분량의 금액을 할인 금액으로 계산
- [ ] `FREESHIPPING` 무료 배송 쿠폰 계산 로직
  - [ ] 주문 금액이 50,000원 이상일 때만 적용
  - [ ] 기본 배송비 3,000원을 0원으로 처리
  - [ ] 도서 / 산간 지역에 관계없이 무료 배송 적용
- [ ] `MIRACLESALE` 30% 할인 쿠폰 계산 로직
  - [ ] 현재 시각이 오전 4시 ~ 7시 구간일 때만 적용
  - [ ] 상품 총액의 30% 할인 금액 계산
- [ ] 최종 결제 금액 산출
  - [ ] (상품 총액 - 쿠폰 할인) + 배송비(기본 3,000원) 공식으로 계산

### UI / API 계층
- [ ] 결제 화면 진입
  - [ ] 적용 가능한 쿠폰 목록 조회 및 노출
  - [ ] 쿠폰 적용 시 할인 금액 / 최종 결제 금액 실시간 반영
  - [ ] 화면 진입 시점에 5분 알림 예약
- [ ] 결제하기 동작
  - [ ] 결제하기 버튼 클릭 시 즉시 최종 주문 완료 (결제 수단 화면 없음)
  - [ ] 주문 완료 후 장바구니에서 주문된 상품만 초기화
  - [ ] 사용한 쿠폰은 차감 없이 그대로 유지
  - [ ] 주문 완료 후 상품 목록 화면으로 이동
  - [ ] 결제 완료 시 예약된 알림 취소
- [ ] 설정 화면
  - [ ] 미결제 알림 On/Off 토글 제공
  - [ ] 토글 변경 시 SharedPreferences에 즉시 저장
  - [ ] 앱 재실행 후에도 마지막 설정 상태 복원
- [ ] 미결제 알림 동작
  - [ ] 결제 화면 진입 후 5분 내 결제하지 않으면 "아직 결제가 완료되지 않았어요" 알림 노출
  - [ ] 알림 설정이 Off인 경우 알림이 노출되지 않음
  - [ ] 알림 클릭 시 결제 화면으로 이동(Deep Link / PendingIntent)

---

# 🚀 3단계 - Navigation & Flow

## 🎯 기능 목록

### 환경 및 인프라 설정
- [x] Compose Navigation 의존성 추가 및 프로젝트 구성
  - [x] `androidx.navigation:navigation-compose` 의존성 추가
- [x] 기존 Activity/Intent 기반 화면 전환 코드 제거
  - [x] 각 화면별 Activity 클래스 및 `startActivity` 호출부 제거
  - [x] `AndroidManifest.xml`에서 불필요한 Activity 선언 정리
  - [x] 진입점을 단일 Activity + NavHost 구조로 전환

### Navigation 구조 설계 (Route)
- [x] 화면별 Route를 `@Serializable` 타입으로 선언
  - [x] 상품 목록 Route (`Shopping`) 정의
  - [x] 상품 상세 Route (`ProductDetail`) 정의 — productId 파라미터 포함
  - [x] 장바구니 Route (`Cart`) 정의
- [x] NavHost 및 NavController 구성
  - [x] 앱 루트에 단일 `NavHost` 배치 및 시작 목적지(상품 목록) 지정
  - [x] 각 Route에 대응하는 `composable<Route>` 블록 작성
  - [x] NavController는 화면 Composable에 직접 전달하지 않고 이동 콜백 람다로 분리

### 화면 이동 및 Back Stack 제어
- [x] 화면 간 이동을 NavController 기반으로 처리
  - [x] 상품 목록 → 상품 상세 이동 시 productId 전달
  - [x] 상품 상세 → 장바구니 이동
  - [x] 장바구니 → 상품 추천 이동
- [x] 주문 완료 흐름의 Back Stack 정리
  - [x] 주문 완료 후 상품 목록으로 이동 시 `popUpTo`로 주문 관련 화면 제거
  - [x] `inclusive` 옵션을 적절히 설정하여 뒤로가기 시 주문 흐름이 남지 않도록 처리

### 도메인 로직 / UI 상태 관리 (Flow)
- [x] ViewModel의 UI 상태를 `StateFlow`로 노출
  - [x] 내부 상태는 `MutableStateFlow`로 선언
  - [x] 외부 노출은 `asStateFlow()`를 통한 읽기 전용 `StateFlow`
  - [x] 기존 `remember` / `mutableStateOf` 기반 상태를 모두 교체
- [x] 일회성 이벤트를 `SharedFlow`로 처리
  - [x] 장바구니 담기 성공/실패 이벤트를 `MutableSharedFlow`로 발행
  - [x] 스낵바 표시, 화면 이동 트리거 등 단발성 이벤트 처리
  - [x] 이벤트 수신 후 재발행되지 않도록 replay/buffer 정책 검토
- [x] Composable에서 Lifecycle 인식 상태 구독
  - [x] `collectAsState()` 호출부를 `collectAsStateWithLifecycle()`로 교체
  - [x] 백그라운드 상태에서 불필요한 수집이 발생하지 않도록 보장

### 테스트
- [x] 기존 테스트 호환성 유지

---

# 1 & 2단계 리팩토링 목록 - 2번째

### 아키텍처 및 ViewModel 계층 개선
- [x] **도메인 로직 분리:** `CartViewModel`에서 수행 중인 총액 계산(`calculatePrice`) 등의 로직을 도메인 객체나 별도 UseCase로 위임하기.
- [x] **데이터 매핑 로직 위임:** `ShoppingViewModel`에서 `Product`와 `Cart`를 조합해 `ProductUiModel`로 변환하는 작업을 Mapper 클래스로 분리하여 단위 테스트 환경 구축하기.
- [x] **중복 함수 호출 제거:** `calculatePrice`와 `calculateTotalSelectedCount` 함수에서 각각 호출되는 `getAllCartItems()`를 한 번만 호출하여 재사용하도록 수정하기.
- [x] **공통 로직 모듈화:** 모든 ViewModel에서 중복으로 사용되는 `handleError` 함수를 재사용 가능하도록 공통화하기.

### 예외 처리 및 앱 안정성 강화
- [x] **예외 문맥 정보 강화:** 장바구니 상품 삭제 예외(ID null) 발생 시 앱 강제 종료를 방지하도록 예외 처리 범위 수정
- 
### 데이터 및 네트워크 계층 최적화
- [x] **무제한 API 호출 제한:** `CartService`의 조회 `size` 기본값(`Int.MAX_VALUE`)을 서버 정책에 맞는 적절한 상한선으로 변경하여 메모리 부담 줄이기.

---

# 1 & 2단계 리팩토링 목록

## 정리
- [x] 미션에서 사용하지 않는 파일 제거

## Network 레이어
- [x] RetrofitClient 단일화 - Retrofit 인스턴스 하나만 만들고 .create()로 여러 서비스 생성으로 변경
- [x] CartService의 @Header 파라미터 제거 후 Interceptor로 자동 주입하도록 변경

## UiState 설계
- [x] CartUiState 상태 표현 개선

## ViewModel 구조
- [x] ViewModelProvider.Factory를 각 ViewModel의 companion object로 이동
- [x] CartViewModel 멤버 가시성 점검
- [x] loadData() 제거 - refreshData()를 불필요하게 한 번 더 감싸기만 함
- [x] 모든 상호작용마다 전체 새로고침 호출하는 패턴 점검 - 로컬 상태 업데이트로 충분한 케이스 분리

## 페이지네이션
- [x] ProductRepository를 fromIndex/count -> page/size 기반 인터페이스로 변경
- [x] getProducts 내부 drop().take() 제거하고 서버 페이지네이션 그대로 활용
- [x] hasNext 책임을 ViewModel로 이동 - 서버 응답 메타데이터(last, totalPages)로 판단

## 에러/예외 처리
- [x] 요청 실패 시 피드백 추가 (ex. 토스트)
- [x] 추천 화면 재진입 시 stale한 recommendItems 초기화

# 🚀 2단계 - 상품 추천

## 🎯 기능 목록

### 장바구니 화면 (UI)

- [x] 각 상품별로 선택/해제할 수 있는 기능(체크박스 등)을 구현한다.
- [x] 사용자가 선택한 특정 상품들만 포함해 '주문하기' 버튼을 눌러 주문을 완료한다
- [x] 전체 상품을 선택할 수 있다.
- [x] 주문하기 옆에 상품 종류 개수와 전체 계산된 가격을 보여준다.

### 상품 추천 화면 (UI)

- [x] 장바구니에서 주문하기 버튼을 누르면 화면에 추천 상품 목록을 노출한다.
- [x] 추천된 상품 아이템마다 '장바구니 추가' 버튼을 구현하여 즉시 함께 주문할 수 있도록한다.

### 상품 추천 알고리즘 (Domain)

- [x] 사용자가 가장 최근에 본 상품의 카테고리를 식별한다.
- [x] 해당 카테고리와 일치하는 상품을 목록에서 필터링한다.
- [x] 이미 장바구니에 추가된 상품은 추천 목록에서 완전히 제외한다.
- [x] 필터링된 추천 상품이 10개 이상일 경우 최대 10개만 반환한다.
  - [x] 10개 미만일 경우 존재하는 개수만큼만 반환한다.
- [x] 추천 화면에서 상품을 추가하면 장바구니가 업데이트 된다.

### 주문 데이터 (Domain)
- [x] 주문 API 연동
  - [x] 주문 생성 요청 모델 정의
  - [x] 주문 결과 응답 처리

### 테스트 요구 사항 (Test)

#### 상품 추천 알고리즘 테스트
- [x] 가장 최근에 본 상품의 카테고리를 기준으로 추천 목록이 생성되는지 검증한다.
- [x] 추천 목록이 최대 10개까지만 노출되는지 검증한다.
- [x] 추천 대상 상품이 10개 미만일 때, 해당 개수만큼만 정확히 노출되는지 검증한다.
- [x] 장바구니에 이미 담겨있는 상품은 추천 목록에서 제외되는지 검증한다.


# 🚀 1단계 - 서버 연동

##  기능 목록

### 환경 및 인프라 설정
- [x] 네트워크 통신 환경 구성
    - [x] Retrofit 의존성 추가
    - [x] BaseUrl 상수 분리
    - [x] HTTP 네트워크 보안 설정 허용
- [x] JSON 직렬화 라이브러리 선정 및 설정
    - [x] Kotlinx Serialization 선택 
      - 코틀린에 친화적이어서 default value 를 인식 가능하고, Null-Safety 같은 특징을 준수한다.  
      - Moshi 와 비교했을 때 일반적인 모델, 크기가 큰 모델, Sealed class로 나눠서 분석했을 때 Serializer 생성, 직렬화, 역직렬화 시간이 덜 걸렸다.  
    - [x] Retrofit Converter 등록
- [x] 사용자 인증 정보 저장소 구현

### 도메인 로직 / 데이터 계층
- [x] 저장된 인증 정보를 읽어 `Authorization: Basic ...` 헤더 추가
- [x] 상품 목록 API 연동
    - [x] DTO ↔ Domain Model 매퍼 작성
    - [x] Repository 추상화 및 Remote DataSource 구현
- [x] 장바구니 API 연동
    - [x] 장바구니 조회 / 담기 / 수량 변경 / 삭제 구현
    - [x] 서버 응답을 도메인 모델로 변환

### UI / 프레젠테이션 계층
- [x] 스켈레톤 UI 구현
    - [x] 상품 목록 화면 스켈레톤 레이아웃 작성
    - [x] 데이터 로딩 전까지 스켈레톤 노출, 완료 시 실제 UI로 교체
    - [x] Shimmer 효과 적용 여부 결정
- [x] 네트워크 에러 핸들링 UI
    - [x] 일반 에러 / 인증 실패 / 네트워크 단절 케이스 분기
