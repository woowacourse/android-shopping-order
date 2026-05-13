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
    ): List<CartContent> = withContext(Dispatchers.IO) {
        val response = retrofitCartService
            .requestCartItems(page = startIndex, size = pageSize)
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        body.content.map { it.toDomain() }
    }

    override suspend fun getTotalQuantity(): Int? = withContext(Dispatchers.IO) {
        val response = retrofitCartService
            .getCartItemTotalCount()
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
        body.quantity
    }

    override suspend fun insert(item: CartContent) = withContext(Dispatchers.IO) {
        val response = retrofitCartService
            .insertCartItem(
                cartItemInsertDto = CartItemInsertDto(
                    productId = item.id.toLong(),
                    quantity = item.quantity,
                ),
            )
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }
    }

    override suspend fun update(item: CartContent) = withContext(Dispatchers.IO) {
        val response = retrofitCartService
            .updateCartItemQuantity(
                id = item.id,
                quantity = Quantity(item.quantity),
            )
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
    }

    override suspend fun deleteById(id: String) = withContext(Dispatchers.IO) {
        val response = retrofitCartService
            .deleteCartItem(id = id)
            .execute()

        check(response.isSuccessful) { "products 요청 실패: ${response.code()}" }

        val body = response.body()
            ?: error("empty body")
    }
}
