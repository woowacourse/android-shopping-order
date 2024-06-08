package woowacourse.shopping.exception

sealed class ShoppingError {
    data object ProductNotFound : ShoppingError()

    data class ProductNotFoundWithId(val id: Long) : ShoppingError()

    data object RecentProductNotFound : ShoppingError()

    data object CartNotFound : ShoppingError()

    data object CartItemCountConfirmError : ShoppingError()

    data object CouponsNotFound : ShoppingError()
}

fun handleError(it: Throwable) =
    when (it as ShoppingError) {
        is ShoppingError.ProductNotFound -> "상품 정보를 불러오지 못했습니다"
        is ShoppingError.ProductNotFoundWithId -> "${(it as ShoppingError.ProductNotFoundWithId).id} 에 해당하는 productId가 없습니다"
        is ShoppingError.RecentProductNotFound -> "최근 본 상품 정보를 불러오지 못했습니다"
        is ShoppingError.CartNotFound -> "장바구니 정보를 불러올 수 없습니다."
        is ShoppingError.CartItemCountConfirmError -> "장바구니 아이템 수량을 조회할 수 없습니다."
        is ShoppingError.CouponsNotFound -> "쿠폰 정보를 불러오지 못했습니다."
    }
