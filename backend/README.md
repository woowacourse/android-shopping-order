# Shopping Backend

안드로이드 앱의 HTTP 계약에 맞춘 간단한 메모리 기반 backend 모듈이다.

## 실행

```bash
./gradlew :backend:run
```

기본 포트는 `8080`이고, 환경 변수 `PORT`로 변경할 수 있다.

## 제공 API

- `GET /products?page={page}&size={size}&category={category?}`
- `GET /products/{id}`
- `GET /coupons`
- `GET /cart-items?page={page}&size={size}`
- `POST /cart-items`
- `PATCH /cart-items/{id}`
- `DELETE /cart-items/{id}`
- `GET /cart-items/counts`
- `POST /orders`

## 비고

- 데이터는 서버 메모리에만 저장된다.
- 상품 데이터는 Android 테스트 fixture와 같은 형태의 24개 샘플 상품으로 초기화된다.
- 쿠폰 데이터는 README 4단계 요구사항을 기준으로 한 정적 샘플 정책 정보(`FIXED5000`, `BOGO`, `FREESHIPPING`, `MIRACLESALE`)를 반환한다.
- `POST /orders`는 전달된 `cartItemIds`를 장바구니에서 제거한다.
