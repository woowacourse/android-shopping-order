package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.datasource.cart.CartProductDataSource
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.repository.CartProductId
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductId

class CartProductRemoteRepository(
    private val cartProductDataSource: CartProductDataSource,
) : CartRepository {
    override fun getAllCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        cartProductDataSource.requestCartProducts(
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun addCartProductByProductId(
        productId: ProductId,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        cartProductDataSource.addCartProductByProductId(
            productId = productId.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun updateProductCountById(
        cartProductId: CartProductId,
        count: ProductCount,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        cartProductDataSource.updateCartProductCountById(
            cartItemId = cartProductId.toString(),
            quantity = count.value,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun deleteCartProductById(
        cartProductId: CartProductId,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        cartProductDataSource.deleteCartProductById(
            cartItemId = cartProductId.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun findCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailure: (String) -> Unit,
    ) {
        cartProductDataSource.requestCartProductByProductId(
            productId = productId,
            onSuccess = { cartProduct ->
                onSuccess(cartProduct)
            },
            onFailure = { },
        )
    }

    override fun increaseProductCountByProductId(
        productId: ProductId,
        addCount: ProductCount,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        findCartProductByProductId(
            productId = productId,
            onSuccess = { cartProduct ->
                val updatedCount = cartProduct.selectedCount + addCount
                updateProductCountById(cartProduct.id, updatedCount, onSuccess, onFailure)
            },
            onFailure = {
                addCartProductByProductId(
                    productId,
                    onSuccess = {
                        findCartProductByProductId(
                            productId = productId,
                            onSuccess = { newCartProduct ->
                                updateProductCountById(
                                    newCartProduct.id,
                                    addCount,
                                    onSuccess = onSuccess,
                                    onFailure = onFailure,
                                )
                            },
                            onFailure = onFailure,
                        )
                    },
                    onFailure = {},
                )
            },
        )
    }
}
