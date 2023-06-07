package woowacourse.shopping.data.datasource.remote.cart

import com.example.domain.model.CartProduct
import com.example.domain.model.CartProducts
import com.example.domain.model.FailureInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.remote.RetrofitService
import woowacourse.shopping.data.model.CartProductDto
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.util.failureInfo

class CartDataSourceImpl : CartRemoteDataSource {

    private val cartService = RetrofitService.cartService

    override fun loadAll(
        onSuccess: (CartProducts) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        cartService.getAll().enqueue(object : Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>
            ) {
                if (response.code() >= 400) onFailure(failureInfo(response.code()))
                response.body()?.let { cartProducts ->
                    onSuccess(CartProducts(cartProducts.map { it.toDomain() }))
                }
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                onFailure(FailureInfo.Default(throwable = t))
            }
        })
    }

    override fun addCartProduct(
        productId: Int,
        onSuccess: (cartItemId: Int) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        cartService.addProduct(productId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() >= 400) onFailure(failureInfo(response.code()))
                val location = response.headers()["Location"]
                onSuccess(location?.filter { it.isDigit() }?.toInt() ?: -1)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(FailureInfo.Default(throwable = t))
            }
        })
    }

    override fun updateCartProductCount(
        cartItemId: Int,
        count: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        cartService.updateQuantity(cartItemId, count).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() >= 400) onFailure(failureInfo(response.code()))
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(FailureInfo.Default(throwable = t))
            }
        })
    }

    override fun deleteCartProduct(
        cartItemId: Int,
        onSuccess: () -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        cartService.deleteProduct(cartItemId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.code() >= 400) onFailure(failureInfo(response.code()))
                onSuccess()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure(FailureInfo.Default(throwable = t))
            }
        })
    }

    override fun getCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct?) -> Unit,
        onFailure: (FailureInfo) -> Unit
    ) {
        cartService.getAll().enqueue(object : Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>
            ) {
                if (response.code() >= 400) onFailure(failureInfo(response.code()))
                response.body()?.let { cartProducts ->
                    val found = cartProducts.map { it.toDomain() }.find { cartProduct ->
                        cartProduct.product.id.toInt() == productId
                    }
                    onSuccess(found)
                }
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                onFailure(FailureInfo.Default(throwable = t))
            }
        })
    }
}
