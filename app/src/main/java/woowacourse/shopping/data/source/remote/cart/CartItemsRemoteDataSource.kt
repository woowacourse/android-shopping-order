package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.source.remote.api.CartApiService

class CartItemsRemoteDataSource(
    private val api: CartApiService
) : CartItemsDataSource {
    override fun getCartItems(onResult: (Result<CartItemResponse>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun addCartItem(id: Int, quantity: Int, onResult: (Result<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun deleteCartItem(id: Int, onResult: (Result<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun updateCartItem(id: Int, onResult: (Result<String>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getCarItemsCount(onResult: (Result<CartItemResponse>) -> Unit) {
        TODO("Not yet implemented")
    }
}