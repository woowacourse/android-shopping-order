# android-shopping-order

1. ProductListPresenter

- [x] 상품 목록 불러오기

```Gherkin
GIVEN 상품을 불러올 수 있는 상태다. 
WHEN 상품을 목록 요청을 보낸다.
THEN 상품을 노출시킨다.
```

- [x] 최근 본 상품 목록 불러오기

```Gherkin
GIVEN 최근 본 상품을 불러올 수 있는 상태다.
WHEN 최근 본 상품 요청을 보낸다.
THEN 최근 본 상품을 노출시킨다.
```

- [x] 상품 상세 보여주기

```Gherkin
GIVEN 상품 상세를 보여줄 수 있는 상태다.
WHEN 상품 상세를 보여달라는 요청을 보낸다.
THEN 상품 상세 화면이 노출된다.
AND 최근 본 상품에 저장된다.
```

- [ ] 상품 개수 증가

```Gherkin
GIVEN 상품 개수를 증가시킬 수 있는 상태다. 
WHEN 상품 개수 증가 요청을 보낸다.
THEN 증가된 상품 개수가 노출된다.
```

- [ ] 상품 개수 감소

```Gherkin
GIVEN 상품 개수를 감소시킬 수 있는 상태다. 
WHEN 상품 개수 감소 요청을 보낸다.
THEN 감소된 상품 개수가 노출된다.
```