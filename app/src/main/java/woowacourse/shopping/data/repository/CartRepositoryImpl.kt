package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.CartProductDataSource
import woowacourse.shopping.product.catalog.ProductUiModel

class CartRepositoryImpl(
    private val cartProductDataSource: CartProductDataSource,
) : CartRepository {
    override fun getTotalProductsCount(callback: (Int) -> Unit) {
        cartProductDataSource.getTotalElements { count ->
            callback(count)
        }
    }

    override fun updateCartProduct(
        cartProduct: ProductUiModel,
        newCount: Int,
        callback: (Boolean) -> Unit,
    ) {
        cartProductDataSource.updateProduct(cartProduct, newCount) { result ->
            callback(result)
        }
    }

    override fun deleteCartProduct(
        cartProduct: ProductUiModel,
        callback: (Boolean) -> Unit,
    ) {
        cartProductDataSource.deleteCartProduct(cartProduct) { result ->
            callback(result)
        }
    }

    override fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        cartProductDataSource.getCartProductsInRange(currentPage, pageSize) { products ->
            callback(products)
        }
    }

    companion object {
        private var instance: CartRepositoryImpl? = null

        @Synchronized
        fun initialize(cartProductDataSource: CartProductDataSource): CartRepositoryImpl =
            instance ?: CartRepositoryImpl(
                cartProductDataSource,
            ).also { instance = it }
    }
}
