package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.local.LocalRecentViewedDataSource
import woowacourse.shopping.data.datasource.remote.RemoteProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecommendRepository
import woowacourse.shopping.remote.NetworkResult

class RecommendRepositoryImpl(
    private val recentViewedDataSource: LocalRecentViewedDataSource,
    private val productDataSource: RemoteProductDataSource,
) : RecommendRepository {
    override suspend fun generateRecommendProducts(existProductIds: List<Long>): Result<List<Product>> {
        return runCatching {
            val recentViewed = recentViewedDataSource.getMostRecent()?.toDomain()
            if (recentViewed == null) {
                emptyList<Product>()
            } else {
                val existingProductIdsSet = existProductIds.toSet()
                val result = productDataSource.getProductsByCategory(recentViewed.category, 0, 20)
                when (result) {
                    is NetworkResult.Success -> {
                        result.data.content
                            .asSequence()
                            .filter { it.id.toLong() !in existingProductIdsSet }
                            .map { it.toProduct() }
                            .toList()
                    }
                    is NetworkResult.Error -> {
                        throw result.exception
                    }
                }
            }
        }
    }
}
