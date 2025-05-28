package woowacourse.shopping.data.repository

import retrofit2.HttpException
import woowacourse.shopping.data.datasource.CartDataSource2
import woowacourse.shopping.data.network.request.toRequest
import woowacourse.shopping.domain.cart.Cart
import woowacourse.shopping.domain.cart.CartsSinglePage

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

    fun loadSinglePage(
        page: Int,
        pageSize: Int,
        callback: (Result<CartsSinglePage>) -> Unit,
    ) {
        dataSource.singlePage(page, pageSize) { result ->
            result.fold(
                onSuccess = { response ->
                    if (response != null) {
                        val cartSinglePage = response.toDomain()
                        callback(Result.success(cartSinglePage))
                    } else {
                        val error = HttpException(response)
                        callback(Result.failure(error))
                    }
                },
                onFailure = { throwable ->
                    callback(Result.failure(throwable))
                },
            )
        }
    }
}
