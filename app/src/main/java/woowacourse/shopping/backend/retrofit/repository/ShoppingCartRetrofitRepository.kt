package woowacourse.shopping.backend.retrofit.repository

import woowacourse.shopping.backend.retrofit.api.ShoppingCartRetrofit
import woowacourse.shopping.backend.retrofit.bodyOrThrow
import woowacourse.shopping.backend.retrofit.throwOnFailure
import woowacourse.shopping.backend.retrofit.dto.CartQuantity
import woowacourse.shopping.backend.retrofit.dto.CartRequest
import woowacourse.shopping.backend.retrofit.dto.ShoppingCartResponse

class ShoppingCartRetrofitRepository(
    private val apiService: ShoppingCartRetrofit,
) {
    suspend fun requestCartItems(
        page: Int = DEFAULT_PAGE,
        size: Int = DEFAULT_SIZE,
        sort: List<String>? = null,
    ): ShoppingCartResponse =
        apiService.requestCartItems(
            page = page,
            size = size,
            sort = sort,
        ).bodyOrThrow(errorPrefix = "장바구니 조회 실패")

    suspend fun addCartItem(product: CartRequest) {
        apiService.addCartItem(
            product = product,
        ).throwOnFailure(errorPrefix = "장바구니 추가 실패")
    }

    suspend fun deleteCartItem(id: Int) {
        apiService.deleteCartItem(
            id = id,
        ).throwOnFailure(errorPrefix = "장바구니 삭제 실패")
    }

    suspend fun updateQuantityCartItem(
        id: Int,
        product: CartQuantity,
    ) {
        apiService.updateQuantityCartItem(
            id = id,
            product = product,
        ).throwOnFailure(errorPrefix = "장바구니 수량 수정 실패")
    }

    suspend fun requestQuantityCartItem(): CartQuantity =
        apiService.requestQuantityCartItem().bodyOrThrow(errorPrefix = "장바구니 수량 조회 실패")

    companion object {
        private const val DEFAULT_PAGE = 0
        private const val DEFAULT_SIZE = 5
    }
}
