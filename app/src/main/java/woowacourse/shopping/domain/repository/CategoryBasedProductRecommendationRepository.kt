package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.model.ProductData
import woowacourse.shopping.data.model.toDomain
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import kotlin.math.min

class CategoryBasedProductRecommendationRepository(
    private val productsSource: ProductDataSource,
    private val cartSource: ShoppingCartDataSource,
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

    companion object {
        private const val TAG = "CategoryBasedProductRecommendationRepository"
    }
}
