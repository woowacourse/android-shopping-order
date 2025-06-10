package woowacourse.shopping.domain.usecase.product

import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.RecentProductRepository

class SaveRecentlyViewedProductUseCase(
    private val recentProductRepository: RecentProductRepository,
) {
    suspend operator fun invoke(lastViewedProduct: RecentProduct): Result<Unit> =
        recentProductRepository.saveRecentlyViewedProduct(lastViewedProduct)
}
