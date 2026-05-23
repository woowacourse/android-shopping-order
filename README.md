# 기능 구현 사항

## 3단계 구현 기능 사항

## Navigation
- [ ] Shopping Screen에서 카트 아이콘을 선택하면 Cart Screen으로 이동한다
- [ ] Shopping Screen에서 상품을 선택하면 Detail Screen으로 이동한다
- [ ] Shopping Screen에서 최근 본 상품을 선택하면 Detail Screen으로 이동한다
- [ ] Detail Screen에서 마지막으로 본 상품을 선택하면 해당 Detail Screen으로 이동한다
- [ ] Detail Screen에서 수량 설정 후 장바구니 담기 버튼을 선택하면 Shopping Screen으로 이동한다
- [ ] Cart Screen에서 주문하기를 선택하면 Recommend Screen으로 이동한다
- [ ] Recommend Screen에서 주문하기 버튼을 선택하면 Pay Screen으로 이동한다
- [ ] Pay Screen에서 결제하기 버튼을 선택하면 Shopping Screen으로 이동한다

## Flow
- [ ] 장바구니 담기/삭제 등 단발성 이벤트는 Channel로 처리한다
- [ ] Shopping list의 Cart quantity를 위해 Flow<Map<Long, Int>>로 Flow로 처리한다
- [ ] 최근 본 상품을 Flow로 처리한다
