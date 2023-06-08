package woowacourse.shopping.data.respository.cart.source.remote

import retrofit2.Retrofit
import woowacourse.shopping.data.model.CartRemoteEntity
import woowacourse.shopping.data.respository.cart.source.remote.service.CartService

class CartRemoteDataSourceImpl(
    retrofit: Retrofit,
) : CartRemoteDataSource {
    private val cartService = retrofit.create(CartService::class.java)

    override fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<CartRemoteEntity>) -> Unit,
    ) {
        cartService.requestCartProducts()
            .enqueue(object : retrofit2.Callback<List<CartRemoteEntity>> {
                override fun onResponse(
                    call: retrofit2.Call<List<CartRemoteEntity>>,
                    response: retrofit2.Response<List<CartRemoteEntity>>,
                ) {
                    if (response.isSuccessful) {
                        val products = response.body() ?: return onFailure()
                        onSuccess(products)
                    }
                }

                override fun onFailure(call: retrofit2.Call<List<CartRemoteEntity>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestPatchCartItem(
        cartEntity: CartRemoteEntity,
        onFailure: () -> Unit,
        onSuccess: () -> Unit,
    ) {
        cartService.requestPatchCartProduct(
            cartEntity.id,
            cartEntity.quantity,
        )
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        onSuccess()
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestPostCartItem(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (cartId: Long) -> Unit,
    ) {
        cartService.requestPostCartProduct(productId)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        val location = response.headers()["Location"] ?: return onFailure()
                        val cartId = location.substringAfterLast("cart-items/").toLong()
                        onSuccess(cartId)
                    }
                }

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun requestDeleteCartItem(cartId: Long) {
        cartService.requestDeleteCartProduct(cartId)
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(
                    call: retrofit2.Call<Unit>,
                    response: retrofit2.Response<Unit>,
                ) {}

                override fun onFailure(call: retrofit2.Call<Unit>, t: Throwable) {}
            })
    }
}
