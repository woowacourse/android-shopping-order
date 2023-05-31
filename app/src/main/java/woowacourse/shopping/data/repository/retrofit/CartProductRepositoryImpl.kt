package woowacourse.shopping.data.repository.retrofit

import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.data.datasource.cart.CartProductDataSourceImpl
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.ProductCount
import woowacourse.shopping.domain.repository.CartProductId
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductId

class CartProductRepositoryImpl : CartRepository {
    private val cartProductDataSource = CartProductDataSourceImpl()
    private val token: String?
        get() = ShoppingApplication.pref.getToken()

    override fun getAllCartProducts(
        onSuccess: (List<CartProduct>) -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductDataSource.requestCartProducts(
            token!!,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun addCartProductByProductId(
        productId: ProductId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductDataSource.addCartProductByProductId(
            token = token!!,
            productId = productId.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun updateProductCountById(
        cartProductId: CartProductId,
        count: ProductCount,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductDataSource.updateCartProductCountById(
            token = token!!,
            cartItemId = cartProductId.toString(),
            quantity = count.value,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun deleteCartProductById(
        cartProductId: CartProductId,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        cartProductDataSource.deleteCartProductById(
            token = token!!,
            cartItemId = cartProductId.toString(),
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    override fun findCartProductByProductId(
        productId: Int,
        onSuccess: (CartProduct) -> Unit,
        onFailure: () -> Unit,
    ) {
        getAllCartProducts(
            onSuccess = { cartProducts ->
                val cartProduct = cartProducts.find { it.product.id == productId } ?: run {
                    return@getAllCartProducts
                }
                onSuccess(cartProduct)
            },
            onFailure = { },
        )
    }

    override fun increaseProductCountByProductId(
        productId: ProductId,
        addCount: ProductCount,
    ) {
        findCartProductByProductId(
            productId = productId,
            onSuccess = { cartProduct ->
                val updatedCount = cartProduct.selectedCount + addCount
                updateProductCountById(cartProduct.id, updatedCount, {}, {})
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
                                    onSuccess = {},
                                    onFailure = {},
                                )
                            },
                            onFailure = {},
                        )
                    },
                    onFailure = {},
                )
            },
        )
    }
}
