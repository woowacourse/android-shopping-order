package woowacourse.shopping.data.source.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import woowacourse.shopping.data.source.remote.api.AddItemRequestBody
import woowacourse.shopping.data.source.remote.api.CartService
import woowacourse.shopping.data.source.remote.api.QuantityRequestBody
import woowacourse.shopping.data.source.remote.dto.cart.CartContent

class CartRemoteDataSource(
    private val cartService: CartService,
) {
    suspend fun getCartItems(
        offset: Int,
        limit: Int,
    ): List<CartContent> =
        withContext(Dispatchers.IO) {
            try {
                val response =
                    cartService.requestItems(
                        page = offset,
                        size = limit,
                    )
                Log.d("cartItem", "${response.cartContent}")
                response.cartContent
            } catch (err: Exception) {
                Log.e("cartItem", "Unknown Error : $err")
                emptyList()
            }
        }

    suspend fun addItem(
        id: Long,
        quantity: Int,
    ) {
        withContext(Dispatchers.IO) {
            try {
                cartService.requestAddItem(
                    addItemRequestBody = AddItemRequestBody(id, quantity),
                )
            } catch (err: HttpException) {
                when (err.code()) {
                    400 -> Log.e("cartItem", "Bad Request")
                    401 -> Log.e("cartItem", "Unauthorized")
                    403 -> Log.e("cartItem", "Forbidden")
                    404 -> Log.e("cartItem", "Not Found")
                    500 -> Log.e("cartItem", "Internal Server Error")
                }
            } catch (err: IOException) {
                Log.e("cartItem", "Network Error : $err")
            } catch (err: Exception) {
                Log.e("cartItem", "Unknown Error : $err")
            }
        }
    }

    suspend fun deleteItem(id: Long) {
        withContext(Dispatchers.IO) {
            try {
                cartService.requestDeleteItem(
                    id = id,
                )
            } catch (err: HttpException) {
                when (err.code()) {
                    400 -> Log.e("cartItem", "Bad Request: $err")
                    401 -> Log.e("cartItem", "Unauthorized")
                    403 -> Log.e("cartItem", "Forbidden")
                    404 -> Log.e("cartItem", "Not Found")
                    500 -> Log.e("cartItem", "Internal Server Error")
                }
            } catch (err: IOException) {
                Log.e("cartItem", "Network Error : $err")
            } catch (err: Exception) {
                Log.e("cartItem", "Unknown Error : $err")
            }
        }
    }

    suspend fun changeQuantity(
        id: Long,
        quantity: Int,
    ) {
        withContext(Dispatchers.IO) {
            try {
                cartService.requestChangeQuantity(
                    id = id,
                    quantity = QuantityRequestBody(quantity),
                )
            } catch (err: HttpException) {
                when (err.code()) {
                    400 -> Log.e("cartItem", "Bad Request: $err")
                    401 -> Log.e("cartItem", "Unauthorized")
                    403 -> Log.e("cartItem", "Forbidden")
                    404 -> Log.e("cartItem", "Not Found")
                    500 -> Log.e("cartItem", "Internal Server Error")
                }
            } catch (err: IOException) {
                Log.e("cartItem", "Network Error : $err")
            } catch (err: Exception) {
                Log.e("cartItem", "Unknown Error : $err")
            }
        }
    }
}
