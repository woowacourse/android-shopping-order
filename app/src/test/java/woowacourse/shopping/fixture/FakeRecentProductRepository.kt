package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class FakeRecentProductRepository : RecentProductRepository {
    private val recentProducts = mutableListOf<RecentProduct>()

    override fun getLastViewedProduct(onResult: (Result<RecentProduct?>) -> Unit) {
        onResult(Result.success(recentProducts.lastOrNull()))
    }

    override fun getPagedProducts(
        limit: Int,
        offset: Int,
        onResult: (Result<List<RecentProduct>>) -> Unit,
    ) {
        onResult(
            Result.success(
                recentProducts
                    .sortedByDescending { it.viewedAt }
                    .drop(offset)
                    .take(limit),
            ),
        )
    }

    override fun replaceRecentProduct(
        recentProduct: RecentProduct,
        onResult: (Result<Unit>) -> Unit,
    ) {
        recentProducts.removeIf { it.product.id == recentProduct.product.id }
        recentProducts.add(recentProduct)
        onResult(Result.success(Unit))
    }
}
