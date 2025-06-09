package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.repository.ProductRepository

class CheckLastPageUseCase(
    private val productRepository: ProductRepository,
) {
    suspend operator fun invoke(page: Int): Result<Boolean> = productRepository.isLastPage(page)
}
