package woowacourse.shopping.data.datasource

import okhttp3.ResponseBody
import retrofit2.HttpException
import woowacourse.shopping.BuildConfig
import woowacourse.shopping.data.model.cart.AddCartItemCommand
import woowacourse.shopping.data.model.cart.CartItemResponse
import woowacourse.shopping.data.model.cart.Quantity
import woowacourse.shopping.data.model.common.PageableResponse
import woowacourse.shopping.data.service.CartService

class CartRemoteDataSourceImpl(
    private val cartService: CartService,
) : CartRemoteDataSource {
    private val authorizationKey = "Basic ${BuildConfig.AUTHORIZATION_KEY}"

    override fun fetchCartItems(
        page: Int,
        size: Int,
    ): Result<PageableResponse<CartItemResponse>> = safeApiCall { cartService.fetchCartItems(authorizationKey, page, size).execute() }

    override fun addCartItem(addCartItemCommand: AddCartItemCommand): Result<ResponseBody> =
        safeApiCall {
            cartService.addCartItem(authorizationKey, addCartItemCommand).execute()
        }

    override fun deleteCartItem(cartId: Long): Result<ResponseBody> =
        safeApiCall { cartService.deleteCartItem(authorizationKey, cartId).execute() }

    override fun patchCartItemQuantity(
        cartId: Long,
        quantity: Quantity,
    ): Result<ResponseBody> =
        safeApiCall {
            cartService.patchCartItemQuantity(authorizationKey, cartId, quantity).execute()
        }

    override fun fetchCartItemCount(): Result<Quantity> = safeApiCall { cartService.fetchCartItem(authorizationKey).execute() }

    private inline fun <T> safeApiCall(apiCall: () -> retrofit2.Response<T>): Result<T> =
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                Result.success(
                    response.body() ?: Unit as T,
                )
            } else {
                Result.failure(HttpException(response))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
}
