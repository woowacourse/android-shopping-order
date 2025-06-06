package woowacourse.shopping.data.repository

import woowacourse.shopping.data.source.CartProductDataSource
import woowacourse.shopping.data.source.CatalogProductDataSource
import woowacourse.shopping.data.source.RecentlyViewedProductDataSource
import woowacourse.shopping.product.catalog.ProductUiModel

class CartRecommendationRepositoryImpl(
    private val cartProductDataSource: CartProductDataSource,
    private val catalogProductDataSource: CatalogProductDataSource,
    private val recentlyViewProductDataSource: RecentlyViewedProductDataSource
) : CartRecommendationRepository {
    override fun getRecommendedProducts(
        callback: (List<ProductUiModel>) -> Unit
    ) {
        recentlyViewProductDataSource.getLatestViewedProduct { id ->
            catalogProductDataSource.getProduct(id) { categoryProduct ->
                val category = categoryProduct.category ?: ""
                catalogProductDataSource.getRecommendedProducts(
                    category,
                    ZERO,
                    TOTAL_RECOMMENDATION_PRODUCTS_COUNT
                ) { products ->
                    callback(products)
                }
            }
        }
    }

    override fun getSelectedProductsCount(
        callback: (Int) -> Unit
    ) {
        cartProductDataSource.getTotalElements { count ->
            callback(count)
        }
    }

    override fun insertCartProduct(
        cartProduct: ProductUiModel,
        callback: (ProductUiModel) -> Unit,
    ) {
        cartProductDataSource.insertCartProduct(cartProduct) { product ->
            callback(product)
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


    companion object {
        private const val ZERO: Int = 0
        private const val TOTAL_RECOMMENDATION_PRODUCTS_COUNT: Int = 10


        private var instance: CartRecommendationRepository? = null

        @Synchronized
        fun initialize(
            cartProductDataSource: CartProductDataSource,
            catalogProductDataSource: CatalogProductDataSource,
            recentlyViewProductDataSource: RecentlyViewedProductDataSource
        ): CartRecommendationRepository = instance ?: CartRecommendationRepositoryImpl(
            cartProductDataSource,
            catalogProductDataSource,
            recentlyViewProductDataSource
        ).also { instance = it }
    }
}
