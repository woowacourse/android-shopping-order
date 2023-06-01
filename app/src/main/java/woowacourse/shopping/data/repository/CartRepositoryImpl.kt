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
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailed: (Throwable) -> Unit,
    ) {
        getAllCartProducts(
            onSuccess = { cartProducts ->
                val cartProduct = cartProducts.find { it.product.id == productId } ?: run {
                    onFailed(IllegalStateException("해당 상품이 존재하지 않습니다."))
                    return@getAllCartProducts
                }
                onSuccess(cartProduct)
            },
            onFailed = { onFailed(it) }
        )
    }

    override fun addCartProductByProductId(productId: ProductId) {
        service.addCartProduct(requestBody = CartAddRequest(productId))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, throwable: Throwable) {}
            })
    }

    override fun updateProductCountById(cartProductId: CartProductId, count: ProductCount) {
        service.updateProductCountById(
            cartItemId = cartProductId,
            requestBody = CartPatchRequest(count.value)
        ).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
            override fun onFailure(call: Call<Unit>, throwable: Throwable) {}
        })
    }

    override fun deleteCartProductById(cartProductId: CartProductId) {
        service.deleteCartProductById(cartProductId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
            override fun onFailure(call: Call<Unit>, throwable: Throwable) {}
        })
    }

    override fun increaseProductCountByProductId(productId: Int, addCount: ProductCount) {
        findCartProductByProductId(
            productId = productId,
            onSuccess = { cartProduct ->
                val updatedCount = cartProduct.selectedCount + addCount
                updateProductCountById(cartProduct.id, updatedCount)
            },
            onFailed = {
                addCartProductByProductId(productId)
                findCartProductByProductId(
                    productId = productId,
                    onSuccess = { newCartProduct ->
                        updateProductCountById(newCartProduct.id, addCount)
                    },
                    onFailed = {}
                )
            })
    }
}
