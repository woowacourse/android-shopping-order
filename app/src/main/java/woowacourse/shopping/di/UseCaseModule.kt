package woowacourse.shopping.di

import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.GetAvailableCouponUseCase
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase

object UseCaseModule {
    val recommendProductsUseCase: RecommendProductsUseCase by lazy {
        val productRepository: ProductRepository = RepositoryModule.productRepository
        RecommendProductsUseCase(productRepository)
    }

    val getAvailableCouponUseCase: GetAvailableCouponUseCase by lazy {
        val couponRepository: CouponRepository = RepositoryModule.couponRepository
        GetAvailableCouponUseCase(couponRepository)
    }
}
