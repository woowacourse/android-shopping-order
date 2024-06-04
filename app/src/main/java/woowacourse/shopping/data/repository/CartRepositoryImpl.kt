package woowacourse.shopping.data.repository

import woowacourse.shopping.data.dto.CartItemRequest
import woowacourse.shopping.data.dto.CartQuantityDto
import woowacourse.shopping.data.dto.CartResponse
import woowacourse.shopping.data.remote.RemoteCartDataSource
import woowacourse.shopping.domain.repository.CartRepository
import kotlin.concurrent.thread

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override fun getCartResponse(
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
                    val cartItemRequest = CartItemRequest(productId, quantity)
                    val request =
                        remoteCartDataSource.addCartItem(cartItemRequest)
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
                    val cartQuantityDto = CartQuantityDto(quantity)
                    val response =
                        remoteCartDataSource.updateCartItem(cartItemId, cartQuantityDto)
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

    override fun getCartTotalQuantity(): Result<CartQuantityDto> {
        var result: Result<CartQuantityDto>? = null
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
