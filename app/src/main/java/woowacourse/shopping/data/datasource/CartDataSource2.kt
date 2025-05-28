package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.network.request.CartItemRequest
import woowacourse.shopping.data.network.service.CartService

class CartDataSource2(
    private val service: CartService,
) {
    fun addCart(
        request: CartItemRequest,
        callback: (Result<Unit?>) -> Unit,
    ) {
        val result = service.addCarat(request)
        result.enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) {
                        callback(Result.success(Unit))
                    } else {
                        callback(Result.failure(NullPointerException()))
                    }
                }

                override fun onFailure(
                    call: Call<Unit>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }
}
