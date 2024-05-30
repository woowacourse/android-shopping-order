package woowacourse.shopping.data.repository

import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.CartResponse
import woowacourse.shopping.data.remote.RemoteCartDataSource
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override fun getCartItems(
        page: Int,
        size: Int,
        sort: String,
    ): Result<CartResponse> {
        var result: Result<CartResponse>? = null
        thread {
            result =
                runCatching {
                    val response =
                        remoteCartDataSource.getCartItems(page, size, sort)
                            .execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()
        return result ?: throw Exception()
    }

    override fun addCartItem(
        productId: Int,
        quantity: Int,
    ): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val cartItemRequestBody = CartItemRequestBody(productId, quantity)
                    val request =
                        remoteCartDataSource.addCartItem(cartItemRequestBody)
                    val response = request.execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun deleteCartItem(cartItemId: Int): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val response =
                        remoteCartDataSource.deleteCartItem(cartItemId).execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun updateCartItem(
        cartItemId: Int,
        quantity: Int,
    ): Result<Unit> {
        var result: Result<Unit>? = null
        thread {
            result =
                runCatching {
                    val cartQuantity = CartQuantity(quantity)
                    val response =
                        remoteCartDataSource.updateCartItem(cartItemId, cartQuantity)
                            .execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }

    override fun getCartTotalQuantity(): Result<CartQuantity> {
        var result: Result<CartQuantity>? = null
        thread {
            result =
                runCatching {
                    val response =
                        remoteCartDataSource.getCartTotalQuantity().execute()
                    if (response.isSuccessful) {
                        response.body() ?: throw Exception("No data available")
                    } else {
                        throw Exception("Error fetching data")
                    }
                }
        }.join()

        return result ?: throw Exception()
    }
}
