package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class GetRecentProductsUseCase(
    private val recentProductRepository: RecentProductRepository,
) {
    suspend operator fun invoke(
        limit: Int,
        offset: Int = 0,
    ): Result<List<RecentProduct>> = recentProductRepository.getPagedProducts(limit, offset)
}
