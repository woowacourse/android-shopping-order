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

class CartRepositoryImpl(private val service: CartService) : CartRepository {

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
        getAllCartProducts(
            onSuccess = { cartProducts ->
                val cartProduct = cartProducts.find { it.product.id == productId } ?: run {
                    onFailed()
                    return@getAllCartProducts
                }
                onSuccess(cartProduct)
            },
            onFailed = { onFailed() }
        )
    }

    override fun saveCartProductByProductId(
        productId: ProductId,
        onSuccess: () -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        service.saveCartProduct(requestBody = CartAddRequest(productId))
            .enqueue(object : Callback<Unit> {
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
                saveCartProductByProductId(productId, onSuccess, onFailed)
                findCartProductByProductId(
                    productId = productId,
                    onSuccess = { newCartProduct ->
                        updateProductCountById(newCartProduct.id, addCount, onSuccess, onFailed)
                    },
                    onFailed = {}
                )
            })
    }
}
