package woowacourse.shopping.data.datasource.cart

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
    ): List<CartContent> {
        val response = cartService
            .requestCartItems(page = startIndex, size = pageSize)

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        return body.content.map { it.toDomain() }
    }

    override suspend fun getTotalQuantity(): Int {
        val response = cartService
            .getCartItemTotalCount()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        return body.quantity
    }

    override suspend fun insert(
        productId: String,
        quantity: Int,
    ) {
        val response = cartService
            .insertCartItem(
                request = CartItemAddRequest(
                    productId = productId.toLong(),
                    quantity = quantity,
                ),
            )

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun update(
        contentId: String,
        quantity: Int,
    ) {
        val response = cartService
            .updateCartItemQuantity(
                id = contentId,
                quantity = QuantityDto(quantity),
            )

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun deleteById(contentId: String) {
        val response = cartService
            .deleteCartItem(id = contentId)

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }
}
