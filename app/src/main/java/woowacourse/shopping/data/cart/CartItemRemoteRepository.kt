package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.domain.CartItem
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.utils.ServerConfiguration
import woowacourse.shopping.utils.UserData

class CartItemRemoteRepository(
    private val cache: CartItemMemoryCache = CartItemMemoryCache
) : CartItemRepository {

    private val cartItemRemoteService: CartItemRemoteService = Retrofit.Builder()
        .baseUrl(ServerConfiguration.host.url)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CartItemRemoteService::class.java)

    override fun save(cartItem: CartItem, onFinish: (CartItem) -> Unit) {
        cartItemRemoteService.requestToSave(
            "Basic ${UserData.credential}",
            CartItemSaveRequestBody(cartItem.product.id)
        )
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not()) return
                    val cartItemId = response.headers()["Location"]
                        ?.removePrefix("/cart-items/")
                        ?.toLong()
                        ?: return
                    val savedCartItem =
                        CartItem(cartItemId, cartItem.product, cartItem.addedTime, cartItem.count)
                    cache.save(savedCartItem)
                    onFinish(savedCartItem)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                }
            })
    }

    override fun findAll(onFinish: (List<CartItem>) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll())
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    onFinish(cartItems)
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun findAllByIds(ids: List<Long>, onFinish: (List<CartItem>) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll().filter { it.id in ids })
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    onFinish(cartItems.filter { it.id in ids })
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun findAllOrderByAddedTime(
        limit: Int,
        offset: Int,
        onFinish: (List<CartItem>) -> Unit
    ) {
        if (cache.isActivated) {
            val cartItems = cache.findAll(limit, offset)
            onFinish(cartItems)
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    val slicedCartItems = cartItems.slice(offset until cartItems.size)
                        .take(limit)
                    onFinish(slicedCartItems)
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun findById(id: Long, onFinish: (CartItem) -> Unit) {
        if (cache.isActivated) {
            cache.findAll().find { it.id == id }?.run(onFinish)
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    cartItems.find { it.id == id }?.run(onFinish)
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun findByProductId(productId: Long, onFinish: (CartItem?) -> Unit) {
        if (cache.isActivated) {
            val cartItem = cache.findAll().find { it.product.id == productId }
            cartItem?.run(onFinish)
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    cartItems.find { it.product.id == productId }?.run(onFinish)
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun countAll(onFinish: (Int) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll().size)
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    onFinish(cartItems.size)
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun existByProductId(productId: Long, onFinish: (Boolean) -> Unit) {
        if (cache.isActivated) {
            onFinish(cache.findAll().any { it.product.id == productId })
            return
        }

        cartItemRemoteService.requestCartItems("Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<List<CartItemDto>> {
                override fun onResponse(
                    call: Call<List<CartItemDto>>,
                    response: Response<List<CartItemDto>>
                ) {
                    if (response.isSuccessful.not()) return
                    val cartItems = response.body()?.map { it.toDomain() } ?: return
                    if (cache.isActivated.not()) cache.activate(cartItems)
                    onFinish(cartItems.any { it.product.id == productId })
                }

                override fun onFailure(call: Call<List<CartItemDto>>, t: Throwable) {
                }
            })
    }

    override fun updateCountById(id: Long, count: Int, onFinish: () -> Unit) {
        cartItemRemoteService.requestToUpdateCount(
            id,
            "Basic ${UserData.credential}",
            CartItemQuantityUpdateRequestBody(count)
        )
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    cache.updateCountById(id, count)
                    onFinish()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                }
            })
    }

    override fun deleteById(id: Long, onFinish: () -> Unit) {
        cartItemRemoteService.requestToDelete(id, "Basic ${UserData.credential}")
            .enqueue(object : retrofit2.Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    cache.deleteById(id)
                    onFinish()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                }
            })
    }
}
