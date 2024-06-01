# Step 1 코드 리뷰 반영할 것

- [x]: CartViewModel 에 increaseProductCount
- [x]: dialog 전역 변수 함수로 바꾸기
- [x]: bundle 을 가져오는 부분은 다이어트 (Util 로 빼기)
- [x]: 카트 주문하기 수량 버그 수정
- [x]: 카트에서 상품 개수를 증가시켰을 때 상품목록에 반영되지 않는 버그를 수정
- []: UiState 에 있는 로직들 ViewModel 로 옮기기
- [x]: RecommendCartProductViewModel.kt - 실패에 대한 예외처리 하기

## 카트에서 상품 개수를 증가시켰을 때 상품목록에 반영되지 않는 버그를 수정
버그 발생 이유: 업데이트 이벤트를 발행하자마자 해당 Cart 프래그먼트에서 이벤트를 소비하고 있어서 발생한 문제
해결 방법: EventBusViewModel 에 RefreshCartEvent 를 별도로 만들었음.. (SharedFlow 가 필요..)

```kotlin
private fun initObservers() {
    ..
    viewModel.updateCartEvent.observe(viewLifecycleOwner) {
        eventBusViewModel.sendUpdateCartEvent() // 1) event 발행 하자마자
    }
    eventBusViewModel.updateCartEvent.observe(viewLifecycleOwner) { // 2) 얘가 받아서 소비함..
        viewModel.loadTotalCartProducts()
}
```