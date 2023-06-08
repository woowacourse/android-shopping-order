package woowacourse.shopping.data.cart

import com.example.domain.Pagination
import com.example.domain.cart.CartProduct
import com.example.domain.cart.CartRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.data.cart.model.dto.response.CartProductResponse
import woowacourse.shopping.data.cart.model.dto.response.CartResponse
import woowacourse.shopping.data.cart.model.toDomain

class CartRemoteRepository(
    retrofit: Retrofit
) : CartRepository {

    private val retrofitCartService: RetrofitCartService = retrofit.create()

    override fun requestFetchCartProductsUnit(
        unitSize: Int,
        page: Int,
        onSuccess: (List<CartProduct>, Pagination) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitCartService.requestFetchCartProductsUnit(unitSize, page)
            .enqueue(object : Callback<CartResponse> {
                override fun onResponse(
                    call: Call<CartResponse>,
                    response: Response<CartResponse>
                ) {
                    val result = response.body()!!
                    if (response.code() >= 400) return onFailure()
                    onSuccess(
                        result.cartItems.map(CartProductResponse::toDomain),
                        result.pagination.toDomain()
                    )
                }

                override fun onFailure(call: Call<CartResponse>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitCartService.requestAddCartProduct(productId)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    val result: Long =
                        response.headers()["Location"]?.split("/")?.last()?.toLong() ?: 0L
                    if (400 <= response.code()) return onFailure()
                    onSuccess(result)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun updateCartProductQuantity(
        id: Long,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        retrofitCartService.requestUpdateQuantity(id, quantity)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun deleteCartProduct(id: Long, onSuccess: () -> Unit, onFailure: () -> Unit) {
        retrofitCartService.requestDeleteCartProduct(id)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    onSuccess()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
