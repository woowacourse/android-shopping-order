# 🚀 1단계 - 서버 연동

## 🎯 기능 목록

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
- [ ] 장바구니 API 연동
    - [ ] 장바구니 조회 / 담기 / 수량 변경 / 삭제 구현
    - [ ] 서버 응답을 도메인 모델로 변환
- [ ] 주문 API 연동
    - [ ] 주문 생성 요청 모델 정의
    - [ ] 주문 결과 응답 처리

### UI / 프레젠테이션 계층
- [x] 스켈레톤 UI 구현
    - [x] 상품 목록 화면 스켈레톤 레이아웃 작성
    - [x] 데이터 로딩 전까지 스켈레톤 노출, 완료 시 실제 UI로 교체
    - [x] Shimmer 효과 적용 여부 결정
- [ ] 네트워크 에러 핸들링 UI
    - [ ] 일반 에러 / 인증 실패 / 네트워크 단절 케이스 분기
    - [ ] 재시도 버튼 제공
