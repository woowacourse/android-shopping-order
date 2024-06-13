package woowacourse.shopping.data.exception

sealed class ShoppingError(message: String) : Exception(message) {
    data object ProductNotFound : ShoppingError("상품 정보를 불러오지 못했습니다")

    data class ProductNotFoundWithId(val id: Long) : ShoppingError("$id 에 해당하는 productId가 없습니다")

    data object RecentProductNotFound : ShoppingError("최근 본 상품 정보를 불러오지 못했습니다")

    data object CartNotFound : ShoppingError("장바구니 정보를 불러올 수 없습니다.")

    data object CartItemCountConfirmError : ShoppingError("장바구니 아이템 수량을 조회할 수 없습니다.")

    data object CouponsNotFound : ShoppingError("쿠폰 정보를 불러오지 못했습니다.")
}
