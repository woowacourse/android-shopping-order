package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ProductHistoryDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.local.source.LocalHistoryProductDataSource
import kotlin.math.min

class CategoryBasedProductRecommendationRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
    private val historySource: ProductHistoryDataSource,
) : ProductsRecommendationRepository {
    override fun recommendedProducts(productId: Long): List<Product> {
        val latest = productsSource.findById(productId)

        val allCartItemProductIds: List<Long> = cartSource.loadAllCartItems().map { it.product.id }

        val productsWithCategory: List<ProductData> = productsSource.findByCategory(latest.category)

        val filteredProducts =
            productsWithCategory.filterNot { productData ->
                allCartItemProductIds.contains(productData.id)
            }

        val minimumCount = min(filteredProducts.size, 10)

        return filteredProducts.map { productData ->
            productData.toDomain()
        }.subList(0, minimumCount)
    }

    override suspend fun recommendedProducts2(): Result<List<Product>> = runCatching {
        val latestProductId = historySource.loadLatestProduct2().getOrThrow().id

        val latestProduct = productsSource.findById2(latestProductId).getOrThrow()

        val allCartItemsProductsIds = cartSource.loadAllCartItems2().getOrThrow().map { it.product.id }

        val productsWithCategory = productsSource.findByCategory2(latestProduct.category).getOrThrow()

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
