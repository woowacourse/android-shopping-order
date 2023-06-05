package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.CartAddRequest
import woowacourse.shopping.data.dto.CartGetResponse
import woowacourse.shopping.data.dto.CartPatchRequest
import woowacourse.shopping.data.dto.mapper.toDomain
import woowacourse.shopping.data.service.cart.CartService
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.repository.CartProductId
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductId

class DefaultCartRepository(private val service: CartService) : CartRepository {

    override fun getAllCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.getAllCartProduct().enqueue(object : Callback<List<CartGetResponse>> {
            override fun onResponse(
                call: Call<List<CartGetResponse>>,
                response: Response<List<CartGetResponse>>,
            ) {
                onSuccess(response.body()?.map { it.toDomain() } ?: emptyList())
            }

            override fun onFailure(call: Call<List<CartGetResponse>>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun findCartProductByProductId(
        productId: ProductId,
        onSuccess: (CartProduct) -> Unit,
        onFailed: () -> Unit,
    ) {
        service.findCartProductByProductId(productId).enqueue(object : Callback<CartGetResponse> {
            override fun onResponse(
                call: Call<CartGetResponse>,
                response: Response<CartGetResponse>,
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.toDomain())
                    return
                }
                onFailed()
            }

            override fun onFailure(call: Call<CartGetResponse>, throwable: Throwable) {
                onFailed()
            }
        })
    }

    override fun saveCartProductByProductId(
        productId: ProductId,
        onSuccess: (cartItemId: Int) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.saveCartProduct(requestBody = CartAddRequest(productId))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful && response.body() != null) {
                        val header = response.headers()["location"]
                        val cartItemId = header?.substringAfterLast("/")?.toIntOrNull() ?: run {
                            onFailed(Throwable(response.message()))
                            return
                        }
                        onSuccess(cartItemId)
                        return
                    }
                    onFailed(Throwable(response.message()))
                }

                override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                    onFailed(throwable)
                }
            })
    }

    override fun updateProductCountById(
        cartProductId: CartProductId,
        count: ProductCount,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.updateProductCountById(
            cartItemId = cartProductId,
            requestBody = CartPatchRequest(count.value)
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess()
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun deleteCartProductById(
        cartProductId: CartProductId,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.deleteCartProductById(cartProductId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess()
                    return
                }
                onFailed(Throwable(response.message()))
            }

            override fun onFailure(call: Call<Unit>, throwable: Throwable) {
                onFailed(throwable)
            }
        })
    }

    override fun increaseProductCountByProductId(
        productId: ProductId,
        addCount: ProductCount,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        findCartProductByProductId(
            productId = productId,
            onSuccess = { cartProduct ->
                val updatedCount = cartProduct.selectedCount + addCount
                updateProductCountById(cartProduct.id, updatedCount, onSuccess, onFailed)
            },
            onFailed = {
                saveCartProductByProductId(
                    productId = productId,
                    onSuccess = { cartItemId ->
                        updateProductCountById(cartItemId, addCount, onSuccess, onFailed)
                    },
                    onFailed = onFailed
                )
            })
    }
}
