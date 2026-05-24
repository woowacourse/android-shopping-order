package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.RecentProductRepository

class GetLastSeenProductUseCase(
    private val recentProductRepository: RecentProductRepository,
) {
    suspend operator fun invoke(): Product? = runCatching {
        recentProductRepository.getRecentProducts(limit = 1).firstOrNull()
    }.getOrNull()
}