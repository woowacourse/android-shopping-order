package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import kotlin.math.min

class CategoryBasedProductRecommendationRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
    private val historySource: ProductHistoryDataSource,
) : ProductsRecommendationRepository {
    override suspend fun recommendedProducts(): Result<List<Product>> = runCatching {
        val latestProductId = historySource.loadLatestProduct2()
            .map { it.id }
            .recover {
                productsSource.findByPaged(1).getOrThrow().random().id
            }.getOrThrow()

        val latestProduct = productsSource.findById(latestProductId).getOrThrow()

        val allCartItemsProductsIds = cartSource.loadAllCartItems2().getOrThrow().map { it.product.id }

        val productsWithCategory = productsSource.findByCategory(latestProduct.category).getOrThrow()

        val filteredProducts = productsWithCategory.filterNot { productData ->
            allCartItemsProductsIds.contains(productData.id)
        }

        val minimumCount = min(filteredProducts.size, 10)

        filteredProducts.subList(0, minimumCount).map { productData ->
            productData.toDomain()
        }
    }

    companion object {
        private const val TAG = "CategoryBasedProductRecommendationRepository"
    }
}
