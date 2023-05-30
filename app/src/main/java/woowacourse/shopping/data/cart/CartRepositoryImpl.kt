package woowacourse.shopping.data.cart

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.CartProductInfoList
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.repository.CartRepository

class CartRepositoryImpl constructor(
    private val cartRemoteDataSource: CartRemoteDataSource,
) : CartRepository {
    override fun addCartItem(productId: Int, onSuccess: (Int?) -> Unit) {
        cartRemoteDataSource.addCartItem(productId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val locationHeader = response.headers()["Location"]
                val cartItemId = extractCartItemIdFromLocation(locationHeader)
                onSuccess(cartItemId)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HttpError", t.message.toString())
                throw (t)
            }
        })
    }

    override fun deleteCartItem(cartId: Int, onSuccess: () -> Unit) {
        cartRemoteDataSource.deleteCartItem(cartId).enqueue(object : Callback<CartDataModel> {
            override fun onResponse(call: Call<CartDataModel>, response: Response<CartDataModel>) {
                onSuccess()
            }

            override fun onFailure(call: Call<CartDataModel>, t: Throwable) {
                Log.d("HttpError", t.message.toString())
                throw (t)
            }
        })
    }

    override fun updateCartItemQuantity(cartId: Int, count: Int, onSuccess: () -> Unit) {
        cartRemoteDataSource.updateCartItemQuantity(
            cartId = cartId,
            count = count,
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.d("HttpError", t.message.toString())
                throw (t)
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
            .enqueue(object : Callback<List<CartDataModel>> {
                override fun onResponse(
                    call: Call<List<CartDataModel>>,
                    response: Response<List<CartDataModel>>,
                ) {
                    val cartItems = response.body()?.map { it.toDomain() }
                    if (cartItems == null) {
                        onSuccess(emptyList())
                    } else {
                        onSuccess(cartItems)
                    }
                }

                override fun onFailure(call: Call<List<CartDataModel>>, t: Throwable) {
                    Log.d("HttpError", t.message.toString())
                    throw (t)
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
}
