package woowacourse.shopping.di

import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.FetchProductsWithCartItemUseCase
import woowacourse.shopping.domain.usecase.GetAvailableCouponUseCase
import woowacourse.shopping.domain.usecase.RecommendProductsUseCase

object UseCaseModule {
    val fetchProductsWithCartItemUseCase: FetchProductsWithCartItemUseCase by lazy {
        val cartRepository: CartRepository = RepositoryModule.cartRepository
        val productRepository: ProductRepository = RepositoryModule.productRepository
        FetchProductsWithCartItemUseCase(cartRepository, productRepository)
    }

    val recommendProductsUseCase: RecommendProductsUseCase by lazy {
        val cartRepository: CartRepository = RepositoryModule.cartRepository
        val productRepository: ProductRepository = RepositoryModule.productRepository
        RecommendProductsUseCase(cartRepository, productRepository)
    }

    val getAvailableCouponUseCase: GetAvailableCouponUseCase by lazy {
        val couponRepository: CouponRepository = RepositoryModule.couponRepository
        GetAvailableCouponUseCase(couponRepository)
    }
}
