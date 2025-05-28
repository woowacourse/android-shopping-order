package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.CartDataSource2
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.cart.Cart

class DefaultCartRepository(
    private val dataSource: CartDataSource2,
) {
    fun addCart(
        cart: Cart,
        callback: (Result<Unit>) -> Unit,
    ) {
        dataSource.addCart(cart.toRequest()) { response ->
            response.fold(
                onSuccess = { value ->
                    if (value != null) {
                        callback(Result.success(value))
                    } else {
                        callback(Result.failure(NullPointerException()))
                    }
                },
                onFailure = { throwable ->
                    callback(Result.failure(throwable))
                },
            )
        }
    }
}
