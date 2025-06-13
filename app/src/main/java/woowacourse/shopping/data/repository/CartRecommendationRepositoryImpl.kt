package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.CartProductDataSource
import woowacourse.shopping.data.source.CatalogProductDataSource
import woowacourse.shopping.data.source.RecentlyViewedProductDataSource
import woowacourse.shopping.product.catalog.ProductUiModel

class CartRecommendationRepositoryImpl(
    private val cartProductDataSource: CartProductDataSource,
    private val catalogProductDataSource: CatalogProductDataSource,
    private val recentlyViewProductDataSource: RecentlyViewedProductDataSource,
) : CartRecommendationRepository {
    override suspend fun getRecommendedProducts(): List<ProductUiModel> {
        val id = recentlyViewProductDataSource.getLatestViewedProduct()
        val categoryProduct = catalogProductDataSource.getProduct(id)
        val category = categoryProduct?.category ?: ""
        val products = catalogProductDataSource.getRecommendedProducts(
            category,
            ZERO,
            TOTAL_RECOMMENDATION_PRODUCTS_COUNT,
        )
        return products
    }

    override suspend fun getSelectedProductsCount(): Int {
        return cartProductDataSource.getTotalElements()
    }

    override suspend fun insertCartProduct(
        cartProduct: ProductUiModel,
    ): ProductUiModel =
        cartProductDataSource.insertCartProduct(cartProduct)


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

    companion object {
        private const val ZERO: Int = 0
        private const val TOTAL_RECOMMENDATION_PRODUCTS_COUNT: Int = 10

        private var instance: CartRecommendationRepository? = null

        @Synchronized
        fun initialize(
            cartProductDataSource: CartProductDataSource,
            catalogProductDataSource: CatalogProductDataSource,
            recentlyViewProductDataSource: RecentlyViewedProductDataSource,
        ): CartRecommendationRepository =
            instance ?: CartRecommendationRepositoryImpl(
                cartProductDataSource,
                catalogProductDataSource,
                recentlyViewProductDataSource,
            ).also { instance = it }
    }
}
