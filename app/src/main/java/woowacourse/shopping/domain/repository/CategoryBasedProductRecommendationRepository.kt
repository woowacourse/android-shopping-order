package woowacourse.shopping.domain.repository

import android.util.Log
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
        val latest = productsSource.findById(productId).also {
            Log.d(TAG, "latest: $it")
        }

        val allCartItemProductIds: List<Long> = cartSource.loadAllCartItems().map { it.product.id }.also {
            Log.d(TAG, "allCartItemProductIds: $it")
        }

        val productsWithCategory: List<ProductData> = productsSource.findByCategory(latest.category).also {
            Log.d(TAG, "productsWithCategory: $it")
        }

        val filteredProducts = productsWithCategory.filterNot { productData ->
            allCartItemProductIds.contains(productData.id)
        }.also {
            Log.d(TAG, "filteredProducts: $it")
        }

        val minimumCount = min(filteredProducts.size, 10)


        return filteredProducts.map { productData ->
            productData.toDomain()
        }.subList(0, minimumCount).also {
            Log.d(TAG, "recommendedProducts: $it")
        }
    }

    companion object {
        private const val TAG = "CategoryBasedProductRecommendationRepository"
    }

}