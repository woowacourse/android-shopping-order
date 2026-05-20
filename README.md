# Android Shopping Cart

## 단계 현황

- `1단계 - 서버 연동`: 완료
- `2단계 - 상품 추천`: 완료
- `3단계 - Navigation & Flow`: 진행 예정

## 3단계 - Navigation & Flow

### 기능 요구 사항

- [ ] Compose Navigation으로 화면 전환을 구현한다.
- [ ] 상품 목록, 상품 상세, 장바구니, 상품 추천 화면을 Navigation Component로 구성한다.
- [ ] 각 화면 이동은 NavController를 통해 처리한다.
- [ ] 화면 이동 시 전달하는 데이터는 타입 안전한 Route를 사용한다.
- [ ] 주문 완료 후 상품 목록으로 이동할 때 주문 흐름이 Back Stack에 남지 않도록 한다.
- [ ] ViewModel의 UI 상태를 StateFlow로 노출한다.
- [ ] 장바구니 담기/삭제 등 단발성 이벤트는 SharedFlow로 처리한다.
- [ ] Composable에서 상태를 구독할 때 `collectAsStateWithLifecycle()`을 사용한다.

### 프로그래밍 요구 사항
    
- [ ] 기존 Activity 전환 방식을 제거하고 Compose Navigation(`navigation-compose`)으로 교체한다.
- [ ] 모든 Route를 `@Serializable` 타입으로 선언한다.
- [ ] NavController는 화면 Composable에 직접 전달하지 않고, 이동 로직은 콜백 람다로 분리한다.
- [ ] 기존 `remember`/`mutableStateOf` 기반 Compose State를 `StateFlow`/`SharedFlow`로 교체한다.
- [ ] ViewModel의 상태는 `MutableStateFlow`로 선언하고 `StateFlow`로 노출한다.
- [ ] 일회성 이벤트(스낵바 표시, 화면 이동 트리거 등)는 `MutableSharedFlow`를 사용한다.
- [ ] Composable에서 `collectAsState()` 대신 `collectAsStateWithLifecycle()`을 사용한다.

---

## 이전 단계 기록

### 1단계 - 서버 연동

#### 기능 요구 사항

- 데이터가 로딩되기 전 상태에서는 스켈레톤 UI를 노출한다.

#### 프로그래밍 요구 사항

- 서버를 연동한다.
- 기존에 작성한 테스트가 깨지면 안 된다.
- 사용자 인증 정보를 저장한다.
- 서버 통신을 위한 JSON 직렬화 라이브러리를 선택하고, 선택 이유를 PR에 남긴다.

#### 서버 API 참고

- API 문서: `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/swagger-ui/index.html`
- 관리자 페이지(상품 관리): `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/admin`
- 설정 페이지(계정 정보 확인): `http://techcourse-lv2-alb-974870821.ap-northeast-2.elb.amazonaws.com/settings`

#### 기능 목록

- [x] 상품 목록을 서버에서 조회한다.
- [x] 서버 응답 DTO를 도메인 모델로 변환한다.
- [x] 데이터 로딩 전 스켈레톤 UI를 노출한다.
- [x] 사용자 인증 정보를 저장한다.
- [x] 저장된 인증 정보를 서버 요청에 사용한다.
- [x] Kotlinx Serialization을 사용해 JSON을 직렬화/역직렬화한다.
- [x] 기존 테스트가 깨지지 않도록 유지한다.

### 2단계 - 상품 추천

#### 기능 요구 사항

- 장바구니 화면에서 특정 상품만 골라 주문하기 버튼을 누를 수 있다.
- 별도의 화면에서 상품 추천 알고리즘으로 사용자에게 적절한 상품을 추천한다.
- 추천 상품은 최근 본 상품의 카테고리를 기준으로 최대 10개 노출한다.
- 해당 카테고리 상품이 10개 미만이면 가능한 개수만 노출한다.
- 장바구니에 이미 추가된 상품은 추천 목록에서 제외한다.
- 추천 상품을 해당 화면에서 바로 장바구니에 추가하고 함께 주문할 수 있다.

#### 프로그래밍 요구 사항

- 기능 요구 사항에 대한 테스트를 작성해야 한다.

#### 기능 목록

##### 장바구니 선택 주문

- [x] 장바구니 상품을 선택할 수 있다.
- [x] 선택한 상품만 주문하기 버튼을 누를 수 있다.
- [x] 선택한 상품이 없으면 주문할 수 없다.

##### 최근 본 상품 기반 추천

- [x] 사용자가 최근 본 상품의 카테고리를 저장한다.
- [x] 가장 최근에 본 상품의 카테고리를 기준으로 추천 상품을 조회한다.
- [x] 같은 카테고리 상품을 최대 10개까지 노출한다.
- [x] 이미 장바구니에 담긴 상품은 추천 목록에서 제외한다.

##### 추천 상품 장바구니 추가

- [x] 추천 상품 화면에서 상품을 바로 장바구니에 추가할 수 있다.
- [x] 추천 상품을 추가하면 함께 주문할 수 있다.
