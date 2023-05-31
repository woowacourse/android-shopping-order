package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.data.entity.CartItemEntity.Companion.toDomain
import woowacourse.shopping.data.entity.ProductIdEntity
import woowacourse.shopping.data.entity.QuantityEntity
import woowacourse.shopping.domain.cart.CartItem
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.network.retrofit.CartItemRetrofitService

class CartItemRemoteSource(retrofit: Retrofit) : CartItemDataSource {
    private val cartItemService = retrofit.create(CartItemRetrofitService::class.java)
    override fun save(cartItem: CartItem, user: User, onFinish: (CartItem) -> Unit) {
        cartItemService.postCartItem("Basic ${user.token}", ProductIdEntity(cartItem.product.id))
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                }

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() != 201) return
                    val id =
                        response.headers()["Location"]?.substringAfterLast("/")?.toLong() ?: return
                    val savedCartItem = CartItem(
                        id, cartItem.quantity, cartItem.product
                    )
                    onFinish(savedCartItem)
                }
            })
    }

    override fun findAll(user: User, onFinish: (List<CartItem>) -> Unit) {
        cartItemService.selectCartItems("Basic ${user.token}")
            .enqueue(object : Callback<List<CartItemEntity>> {
                override fun onFailure(call: Call<List<CartItemEntity>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<CartItemEntity>>,
                    response: Response<List<CartItemEntity>>
                ) {
                    if (response.code() != 200) return
                    onFinish(response.body()?.map { it.toDomain() } ?: return)
                }
            })
    }

    override fun findAll(limit: Int, offset: Int, user: User, onFinish: (List<CartItem>) -> Unit) {
        cartItemService.selectCartItems("Basic ${user.token}")
            .enqueue(object : Callback<List<CartItemEntity>> {
                override fun onFailure(call: Call<List<CartItemEntity>>, t: Throwable) {
                }

                override fun onResponse(
                    call: Call<List<CartItemEntity>>,
                    response: Response<List<CartItemEntity>>
                ) {
                    if (response.code() != 200) return
                    val cartItems = response.body() ?: return
                    val pagedCartItems = cartItems.slice(offset until cartItems.size).take(limit)
                    onFinish(pagedCartItems.map { it.toDomain() })
                }
            })
    }

    override fun updateCountById(id: Long, count: Int, user: User, onFinish: () -> Unit) {
        cartItemService.updateCountCartItem("Basic ${user.token}", id, QuantityEntity(count))
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                }

                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.code() == 200) onFinish()
                }
            })
    }

    override fun deleteById(id: Long, user: User, onFinish: () -> Unit) {
        cartItemService.deleteCartItem("Basic ${user.token}", id).enqueue(object : Callback<Unit> {
            override fun onFailure(call: Call<Unit>, t: Throwable) {
            }

            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() == 204) onFinish()
            }
        })
    }
}
