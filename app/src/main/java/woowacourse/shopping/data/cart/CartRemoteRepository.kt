package woowacourse.shopping.data.cart

import android.util.Log
import com.example.domain.Pagination
import com.example.domain.cart.CartProduct
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.create
import woowacourse.shopping.data.cart.model.dto.response.CartProductResponse
import woowacourse.shopping.data.cart.model.dto.response.CartResponse
import woowacourse.shopping.data.cart.model.toDomain
import woowacourse.shopping.data.util.RetrofitCallback

class CartRemoteRepository(
    retrofit: Retrofit
) : CartRepository {

    private val retrofitCartService: RetrofitCartService = retrofit.create()

    override fun requestFetchCartProductsUnit(
        unitSize: Int,
        page: Int,
        success: (List<CartProduct>, Pagination) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<CartResponse>() {
            override fun onSuccess(response: CartResponse?) {
                if (response == null) return
                val products: List<CartProduct> =
                    response.cartItems.map(CartProductResponse::toDomain)
                val pagination: Pagination = response.pagination.toDomain()
                success(products, pagination)
            }
        }
        retrofitCartService
            .requestFetchCartProductsUnit(unitSize = unitSize, page = page)
            .enqueue(retrofitCallback)
    }

    override fun addCartProduct(
        productId: Long,
        success: (cartId: Long) -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                val cartId = response.headers()["Location"]?.split("/")?.last()?.toLong() ?: return
                success(cartId)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Log.e("Request Failed", t.toString())
            }
        }
        retrofitCartService.requestAddCartProduct(productId).enqueue(retrofitCallback)
    }

    override fun updateCartProductQuantity(
        id: Long,
        quantity: Int,
        success: () -> Unit,
        failure: () -> Unit
    ) {
        val retrofitCallback = object : RetrofitCallback<Unit>() {
            override fun onSuccess(response: Unit?) {
                success()
            }
        }
        retrofitCartService.requestUpdateQuantity(id, quantity).enqueue(retrofitCallback)
    }

    override fun deleteCartProduct(id: Long, success: () -> Unit, failure: () -> Unit) {
        val retrofitCallback = object : RetrofitCallback<Unit>() {
            override fun onSuccess(response: Unit?) {
                success()
            }
        }
        retrofitCartService.requestDeleteCartProduct(id).enqueue(retrofitCallback)
    }
}
