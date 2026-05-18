package woowacourse.shopping.data.source.remote

import retrofit2.Retrofit
import woowacourse.shopping.data.source.remote.api.AddItemRequestBody
import woowacourse.shopping.data.source.remote.api.CartService
import woowacourse.shopping.data.source.remote.api.QuantityRequestBody
import woowacourse.shopping.data.source.remote.dto.cart.CartContent
import kotlin.jvm.java

class CartRemoteDataSource(
    retrofit: Retrofit,
) {
    private val cartService = retrofit.create(CartService::class.java)

    suspend fun getCartItems(
        offset: Int,
        limit: Int,
    ): List<CartContent> =
        try {
            val response =
                cartService.requestItems(page = offset, size = limit)
            response.cartContent
        } catch (err: Exception) {
            emptyList()
        }

    suspend fun addItem(
        id: Long,
        quantity: Int,
    ) = cartService.requestAddItem(addItemRequestBody = AddItemRequestBody(id, quantity))

    suspend fun deleteItem(id: Long) {
        cartService.requestDeleteItem(id = id)
    }

    suspend fun changeQuantity(
        id: Long,
        quantity: Int,
    ) {
        cartService.requestChangeQuantity(id = id, quantity = QuantityRequestBody(quantity))
    }
}
