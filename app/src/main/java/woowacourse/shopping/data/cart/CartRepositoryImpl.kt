package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.Product
import woowacourse.shopping.data.HttpErrorHandler
import woowacourse.shopping.data.cart.model.CartDataModel
import woowacourse.shopping.data.common.model.BaseResponse
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl constructor(
    private val cartRemoteDataSource: CartRemoteDataSource,
    private val httpErrorHandler: HttpErrorHandler,
) : CartRepository {
    override fun addCartItem(productId: Int, onSuccess: (Int?) -> Unit) {
        cartRemoteDataSource.addCartItem(productId).enqueue(object : Callback<BaseResponse<Unit>> {
            override fun onResponse(call: Call<BaseResponse<Unit>>, response: Response<BaseResponse<Unit>>) {
                val locationHeader = response.headers()["Location"]
                val cartItemId = extractCartItemIdFromLocation(locationHeader)
                onSuccess(cartItemId)
            }

            override fun onFailure(call: Call<BaseResponse<Unit>>, t: Throwable) {
                httpErrorHandler.handleHttpError(t)
            }
        })
    }

    override fun deleteCartItem(cartId: Int, onSuccess: () -> Unit) {
        cartRemoteDataSource.deleteCartItem(cartId).enqueue(object : Callback<BaseResponse<CartDataModel>> {
            override fun onResponse(call: Call<BaseResponse<CartDataModel>>, response: Response<BaseResponse<CartDataModel>>) {
                onSuccess()
            }

            override fun onFailure(call: Call<BaseResponse<CartDataModel>>, t: Throwable) {
                httpErrorHandler.handleHttpError(t)
            }
        })
    }

    override fun updateCartItemQuantity(cartId: Int, count: Int, onSuccess: () -> Unit) {
        cartRemoteDataSource.updateCartItemQuantity(
            cartId = cartId,
            count = count,
        ).enqueue(object : Callback<BaseResponse<Unit>> {
            override fun onResponse(call: Call<BaseResponse<Unit>>, response: Response<BaseResponse<Unit>>) {
                if (count == 0) deleteCartItem(cartId) { onSuccess() }
                else onSuccess()
            }

            override fun onFailure(call: Call<BaseResponse<Unit>>, t: Throwable) {
                httpErrorHandler.handleHttpError(t)
            }
        })
    }

    private fun extractCartItemIdFromLocation(locationHeader: String?): Int? {
        if (locationHeader.isNullOrEmpty()) return null
        val lastSlashIndex = locationHeader.lastIndexOf('/')
        return locationHeader.substring(lastSlashIndex + 1).toInt()
    }

    override fun getAllCartItems(onSuccess: (List<CartProductInfo>) -> Unit) {
        cartRemoteDataSource.getAllCartProductsInfo()
            .enqueue(object : Callback<BaseResponse<List<CartDataModel>>> {
                override fun onResponse(
                    call: Call<BaseResponse<List<CartDataModel>>>,
                    response: Response<BaseResponse<List<CartDataModel>>>,
                ) {
                    val result = response.body()?.result
                    val cartItems = result?.map { it.toDomain() }
                    if (cartItems == null) {
                        onSuccess(emptyList())
                    } else {
                        onSuccess(cartItems)
                    }
                }

                override fun onFailure(call: Call<BaseResponse<List<CartDataModel>>>, t: Throwable) {
                    httpErrorHandler.handleHttpError(t)
                }
            })
    }

    override fun getCartItemByProductId(productId: Int, onSuccess: (CartProductInfo?) -> Unit) {
        getAllCartItems {
            val foundItem =
                CartProductInfoList(it).items.find { cartItem -> cartItem.product.id == productId }
            onSuccess(foundItem)
        }
    }

    override fun updateCartItemQuantityByProduct(
        product: Product,
        count: Int,
        onSuccess: () -> Unit
    ) {
        getAllCartItems { cartList ->
            val cartId = cartList.find { it.product.id == product.id }?.id
            if (cartId == null) addCartItem(product.id) { newCartId ->
                if (newCartId == null) return@addCartItem
                updateCartItemQuantity(newCartId, count, onSuccess)
            } else {
                updateCartItemQuantity(cartId, count, onSuccess)
            }
        }
    }
}
