package woowacourse.shopping.data.network.cart

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.network.cart.dto.CartItemInsertDto
import woowacourse.shopping.data.network.cart.dto.Quantity
import woowacourse.shopping.domain.CartContent

class CartRetrofitDaoImpl(
    val retrofitCartService: RetrofitCartService,
) : CartServerDao {
    override suspend fun pagination(
        startIndex: Int,
        pageSize: Int,
        sort: List<String>,
    ): List<CartContent> {
        val response = retrofitCartService
            .requestCartItems(page = startIndex, size = pageSize)

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        return body.content.map { it.toDomain() }
    }

    override suspend fun getTotalQuantity(): Int {
        val response = retrofitCartService
            .getCartItemTotalCount()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        return body.quantity
    }

    override suspend fun insert(item: CartContent) {
        val response = retrofitCartService
            .insertCartItem(
                cartItemInsertDto = CartItemInsertDto(
                    productId = item.product.id.toLong(),
                    quantity = item.quantity,
                ),
            )

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun update(item: CartContent) {
        val response = retrofitCartService
            .updateCartItemQuantity(
                id = item.id,
                quantity = Quantity(item.quantity),
            )

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun deleteById(id: String) {
        val response = retrofitCartService
            .deleteCartItem(id = id)

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }
}
