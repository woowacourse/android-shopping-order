package woowacourse.shopping.data.cart

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.entity.CartItemEntity
import woowacourse.shopping.data.entity.ProductIdEntity
import woowacourse.shopping.data.entity.QuantityEntity
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.network.RetrofitErrorHandlerProvider
import woowacourse.shopping.network.retrofit.CartItemRetrofitService

class CartItemRemoteSource(private val cartItemService: CartItemRetrofitService) :
    CartItemDataSource {
    override fun save(productId: Long, user: User, onFinish: (Result<Long>) -> Unit) {
        cartItemService.postCartItem("Basic ${user.token}", ProductIdEntity(productId))
            .enqueue(object : Callback<Unit> {
                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFinish(Result.failure(t))
                }

                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    if (response.code() != 201) return onFinish(Result.failure(Throwable(response.message())))
                    val header = response.headers()["Location"] ?: return onFinish(
                        Result.failure(
                            Throwable(response.message())
                        )
                    )
                    onFinish(Result.success(header.toLong()))
                }
            })
    }

    override fun findAll(userToken: String, onFinish: (Result<List<CartItemEntity>>) -> Unit) {
        cartItemService.selectCartItems("Basic $userToken")
            .enqueue(RetrofitErrorHandlerProvider.callbackWithBody(200, onFinish))
    }

    override fun findAll(
        limit: Int,
        offset: Int,
        userToken: String,
        onFinish: (Result<List<CartItemEntity>>) -> Unit
    ) {
        cartItemService.selectCartItems("Basic $userToken")
            .enqueue(
                RetrofitErrorHandlerProvider.callbackWithCustomBody(200, onFinish) {
                    it.slice(offset until it.size).take(limit)
                }
            )
    }

    override fun updateCountById(
        id: Long,
        count: Int,
        userToken: String,
        onFinish: (Result<Unit>) -> Unit
    ) {
        cartItemService.updateCountCartItem("Basic $userToken", id, QuantityEntity(count))
            .enqueue(RetrofitErrorHandlerProvider.callbackWithoutBody(200, onFinish))
    }

    override fun deleteById(id: Long, userToken: String, onFinish: (Result<Unit>) -> Unit) {
        cartItemService.deleteCartItem("Basic $userToken", id)
            .enqueue(RetrofitErrorHandlerProvider.callbackWithoutBody(204, onFinish))
    }
}
