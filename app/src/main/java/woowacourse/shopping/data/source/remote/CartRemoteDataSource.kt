package woowacourse.shopping.data.source.remote

import android.util.Log
import okio.IOException
import retrofit2.HttpException
import woowacourse.shopping.data.source.remote.api.CartService
import woowacourse.shopping.data.source.remote.dto.cart.request.AddItemRequest
import woowacourse.shopping.data.source.remote.dto.cart.request.QuantityRequest
import woowacourse.shopping.data.source.remote.dto.cart.response.CartContent

class CartRemoteDataSource(
    private val cartService: CartService,
) {
    suspend fun getCartItems(
        page: Int,
        size: Int,
    ): List<CartContent> =
        try {
            val response =
                cartService.requestItems(
                    page = page,
                    size = size,
                )
            Log.d("cartItem", "${response.cartContent}")
            response.cartContent
        } catch (err: Exception) {
            Log.e("cartItem", "Unknown Error : $err")
            emptyList()
        }

    suspend fun addItem(
        id: Long,
        quantity: Int,
    ) {
        try {
            cartService.requestAddItem(
                addItemRequest = AddItemRequest(id, quantity),
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

    suspend fun deleteItem(id: Long) {
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

    suspend fun changeQuantity(
        id: Long,
        quantity: Int,
    ) {
        try {
            cartService.requestChangeQuantity(
                id = id,
                quantity = QuantityRequest(quantity),
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
