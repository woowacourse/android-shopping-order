package woowacourse.shopping.presentation.ui.detail

enum class DetailError(
    val message: String,
) {
    ProductItemsNotFound("상품을 찾을 수 없습니다."),
    CartItemNotFound("장바구니에 접근할 수 없습니다."),
    RecentItemNotFound("최근 본 상품을 찾을 수 없습니다."),
}
