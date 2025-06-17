package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.CartProductDataSource
import woowacourse.shopping.product.catalog.ProductUiModel

class CartRepositoryImpl(
    private val cartProductDataSource: CartProductDataSource,
) : CartRepository {
    override suspend fun getTotalProductsCount(): Int {
        return cartProductDataSource.getTotalElements()
    }

    override suspend fun updateCartProduct(
        cartProduct: ProductUiModel,
        newCount: Int,
    ): Boolean {
        return cartProductDataSource.updateProduct(cartProduct, newCount)
    }

    override suspend fun deleteCartProduct(
        cartProduct: ProductUiModel,
    ): Boolean {
        return cartProductDataSource.deleteCartProduct(cartProduct)
    }

    override suspend fun getCartProductsInRange(
        currentPage: Int,
        pageSize: Int,
    ): List<ProductUiModel> {
        return cartProductDataSource.getCartProductsInRange(currentPage, pageSize)
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
