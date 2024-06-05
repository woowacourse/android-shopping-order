package woowacourse.shopping.presentation.ui.cart

enum class CartError(
    val message: String,
) {
    CartItemsNotFound("장바구니를 불러올 수 없습니다."),
    CartItemNotDeleted("장바구니에서 상품을 지울 수 없습니다."),
    CartItemsNotModified("장바구니 수량을 변경할 수 없습니다."),
}
