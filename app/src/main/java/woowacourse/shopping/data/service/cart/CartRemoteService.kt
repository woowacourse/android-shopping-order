package woowacourse.shopping.data.service.cart

import com.example.domain.model.CartProduct
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.request.AddCartRequestDto
import woowacourse.shopping.data.dto.request.ChangeCartCountRequestDto
import woowacourse.shopping.data.dto.response.CartProductDto
import woowacourse.shopping.data.service.RetrofitApiGenerator
import woowacourse.shopping.user.ServerInfo
import java.net.URI

class CartRemoteService {

    private val authorization: String = "Basic ${ServerInfo.token}"

    fun requestCarts(onSuccess: (List<CartProduct>) -> Unit, onFailure: () -> Unit) {
        RetrofitApiGenerator.cartService.requestCarts(authorization)
            .enqueue(object : Callback<List<CartProductDto>> {
                override fun onResponse(
                    call: Call<List<CartProductDto>>,
                    response: Response<List<CartProductDto>>,
                ) {
                    if (response.isSuccessful) {
                        val result: List<CartProductDto>? = response.body()
                        val cartProducts = result?.map { it.toDomain() } ?: listOf()
                        onSuccess(cartProducts)
                    }
                }

                override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                    onFailure()
                }
            })
    }

    fun requestAddCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.cartService
            .requestAddCartProduct(authorization, AddCartRequestDto(productId))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        val responseHeader: String =
                            response.headers()["Location"] ?: return onFailure()
                        val cartId = URI(responseHeader).path.substringAfterLast("/").toLong()
                        onSuccess(cartId)
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    fun requestChangeCartProductCount(
        cartId: Long,
        count: Int,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.cartService.requestChangeCartProductCount(
            authorization,
            cartId,
            ChangeCartCountRequestDto(count),
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful) return onSuccess(cartId)
                onFailure()
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }

    fun requestDeleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        RetrofitApiGenerator.cartService.requestDeleteCartProduct(authorization, cartId)
            .enqueue(object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>,
                ) {
                    if (response.isSuccessful) return onSuccess(cartId)
                    onFailure()
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    fun requestSize(onSuccess: (cartCount: Int) -> Unit, onFailure: () -> Unit) {
        RetrofitApiGenerator.cartService.requestCarts(authorization)
            .enqueue(object : Callback<List<CartProductDto>> {
                override fun onResponse(
                    call: Call<List<CartProductDto>>,
                    response: Response<List<CartProductDto>>,
                ) {
                    if (response.isSuccessful) {
                        val result: List<CartProductDto>? = response.body()
                        val cartProducts = result?.map { it.toDomain() } ?: listOf()
                        val count = cartProducts.sumOf { it.count }
                        onSuccess(count)
                    }
                }

                override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                    onFailure()
                }
            })
    }
}
