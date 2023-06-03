package woowacourse.shopping.data.repository

import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.remote.cart.CartProductService
import woowacourse.shopping.data.model.dto.request.AddCartProductDto
import woowacourse.shopping.data.model.dto.request.ChangeCartProductCountDto
import woowacourse.shopping.data.model.dto.response.CartProductDto
import java.net.URI

class CartRepositoryImpl(
    private val cartProductService: CartProductService,
) : CartRepository {
    override fun fetchAll(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductService.getAllCartProduct().enqueue(object : Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>
            ) {
                if (response.isSuccessful.not()) return onFailure()
                val cartProductDtos = response.body() ?: emptyList()
                val cartProducts = cartProductDtos.map(CartProductDto::toDomain)
                onSuccess(cartProducts)
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun fetchSize(onSuccess: (cartCount: Int) -> Unit, onFailure: () -> Unit) {
        cartProductService.getAllCartProduct().enqueue(object : Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>
            ) {
                if (response.isSuccessful.not()) return onFailure()
                val cartProductDtos = response.body() ?: emptyList()
                val cartProducts = cartProductDtos.map(CartProductDto::toDomain)
                onSuccess(cartProducts.sumOf { it.count })
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                onFailure()
            }
        })
    }

    override fun addCartProduct(
        productId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductService.addCartProduct(AddCartProductDto(productId))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not()) return onFailure()
                    val responseHeader = response.headers()["Location"] ?: return onFailure()
                    val cartId = URI(responseHeader).path.substringAfterLast("/").toLong()
                    onSuccess(cartId)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            })
    }

    override fun changeCartProductCount(
        cartId: Long,
        count: Int,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductService.changeCartProduct(cartId, ChangeCartProductCountDto(count)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not()) return onFailure()
                    onSuccess(cartId)
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    onFailure()
                }
            }
        )
    }

    override fun deleteCartProduct(
        cartId: Long,
        onSuccess: (cartId: Long) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductService.deleteCartProduct(cartId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful.not()) return onFailure()
                onSuccess(cartId)
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                onFailure()
            }
        })
    }
}
