package woowacourse.shopping.data.datasource.cart

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.network.cart.CartService
import woowacourse.shopping.data.network.cart.dto.CartItemAddRequest
import woowacourse.shopping.data.network.cart.dto.QuantityDto
import woowacourse.shopping.domain.CartContent

class CartRemoteDataSource(
    val cartService: CartService,
) : CartDataSource {
    override suspend fun pagination(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<CartContent> = withContext(Dispatchers.IO) {
        val response = cartService
            .requestCartItems(page = startIndex, size = pageSize)
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        body.content.map { it.toDomain() }
    }

    override suspend fun getTotalQuantity(): Int? = withContext(Dispatchers.IO) {
        val response = cartService
            .getCartItemTotalCount()
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        body.quantity
    }

    override suspend fun insert(item: CartContent) = withContext(Dispatchers.IO) {
        val response = cartService
            .insertCartItem(
                request = CartItemAddRequest(
                    productId = item.product.id.toLong(),
                    quantity = item.quantity,
                ),
            )
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun update(item: CartContent) = withContext(Dispatchers.IO) {
        val response = cartService
            .updateCartItemQuantity(
                id = item.id,
                quantity = QuantityDto(item.quantity),
            )
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        val response = cartService
            .deleteCartItem(id = id)
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }
}
