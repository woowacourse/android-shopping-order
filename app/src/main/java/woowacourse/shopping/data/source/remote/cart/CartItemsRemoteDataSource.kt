package woowacourse.shopping.data.source.remote.cart

import woowacourse.shopping.data.model.CartItemResponse
import woowacourse.shopping.data.source.remote.api.CartApiService
import woowacourse.shopping.data.source.remote.util.enqueueResult

class CartItemsRemoteDataSource(
    private val api: CartApiService,
) : CartItemsDataSource {
    override fun getCartItems(
        page: Int,
        size: Int,
        onResult: (Result<CartItemResponse>) -> Unit,
    ) {
        api.getCartItems(page = page, size = size).enqueueResult(onResult)
    }

    override fun addCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        api.postCartItems(productId = id, quantity = quantity).enqueueResult(onResult)
    }

    override fun deleteCartItem(
        id: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        api.deleteCartItems(id = id).enqueueResult(onResult)
    }

    override fun updateCartItem(
        id: Int,
        quantity: Int,
        onResult: (Result<Unit>) -> Unit,
    ) {
        api.patchCartItems(id = id, quantity = quantity).enqueueResult(onResult)
    }

    override fun getCarItemsCount(onResult: (Result<Int>) -> Unit) {
        api.getCartItemsCounts().enqueueResult(onResult)
    }
}
