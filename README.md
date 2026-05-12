# 🚀 1단계 - 서버 연동

## 🎯 기능 목록

### 환경 및 인프라 설정
- [ ] 네트워크 통신 환경 구성
    - [ ] Retrofit 의존성 추가
    - [ ] BaseUrl 상수 분리
    - [ ] HTTP 네트워크 보안 설정 허용
- [ ] JSON 직렬화 라이브러리 선정 및 설정
    - [ ] org.json / Kotlinx Serialization / Gson / Moshi 중 선택
    - [ ] 선택 사유를 PR에 명시 (성능, Kotlin 친화성, 학습 비용 등)
    - [ ] Retrofit Converter 등록
- [ ] 사용자 인증 정보 저장소 구현
    - [ ] EncryptedSharedPreferences 또는 DataStore(Encrypted) 선정
    - [ ] 아이디/비밀번호 Base64 인코딩 후 저장
    - [ ] 인증 정보 저장/조회/삭제 인터페이스 정의

### 도메인 로직 / 데이터 계층
- [ ] 인증 헤더 자동 주입 Interceptor 구현
    - [ ] 저장된 인증 정보를 읽어 `Authorization: Basic ...` 헤더 추가
    - [ ] 인증 정보가 없는 경우 처리 정책 수립
- [ ] 상품 목록 API 연동
    - [ ] DTO ↔ Domain Model 매퍼 작성
    - [ ] Repository 추상화 및 Remote DataSource 구현
- [ ] 장바구니 API 연동
    - [ ] 장바구니 조회 / 담기 / 수량 변경 / 삭제 구현
    - [ ] 서버 응답을 도메인 모델로 변환
- [ ] 주문 API 연동
    - [ ] 주문 생성 요청 모델 정의
    - [ ] 주문 결과 응답 처리

### UI / 프레젠테이션 계층
- [ ] 로딩 상태 표현을 위한 UiState 모델 정의
    - [ ] `Loading` / `Success` / `Error` 상태 분기
- [ ] 스켈레톤 UI 구현
    - [ ] 상품 목록 화면 스켈레톤 레이아웃 작성
    - [ ] 데이터 로딩 전까지 스켈레톤 노출, 완료 시 실제 UI로 교체
    - [ ] Shimmer 효과 적용 여부 결정
- [ ] 네트워크 에러 핸들링 UI
    - [ ] 일반 에러 / 인증 실패 / 네트워크 단절 케이스 분기
    - [ ] 재시도 버튼 제공
