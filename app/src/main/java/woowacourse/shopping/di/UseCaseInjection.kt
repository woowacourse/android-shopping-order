package woowacourse.shopping.di

import woowacourse.shopping.di.RepositoryInjection.cartRepository
import woowacourse.shopping.di.RepositoryInjection.couponRepository
import woowacourse.shopping.di.RepositoryInjection.historyRepository
import woowacourse.shopping.di.RepositoryInjection.orderRepository
import woowacourse.shopping.di.RepositoryInjection.productRepository
import woowacourse.shopping.domain.usecase.AddSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.DecreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsQuantityUseCase
import woowacourse.shopping.domain.usecase.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.GetCartRecommendProductsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsByProductIdsUseCase
import woowacourse.shopping.domain.usecase.GetCatalogProductsUseCase
import woowacourse.shopping.domain.usecase.GetCouponsUseCase
import woowacourse.shopping.domain.usecase.GetRecentSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.GetSearchHistoryUseCase
import woowacourse.shopping.domain.usecase.IncreaseCartProductQuantityUseCase
import woowacourse.shopping.domain.usecase.OrderProductsUseCase
import woowacourse.shopping.domain.usecase.RemoveCartProductUseCase
import woowacourse.shopping.domain.usecase.UpdateCartProductUseCase

object UseCaseInjection {
    val getCartProductsUseCase by lazy {
        GetCartProductsUseCase(cartRepository)
    }

    val increaseCartProductQuantityUseCase by lazy {
        IncreaseCartProductQuantityUseCase(cartRepository)
    }

    val decreaseCartProductQuantityUseCase by lazy {
        DecreaseCartProductQuantityUseCase(cartRepository)
    }

    val removeCartProductUseCase by lazy {
        RemoveCartProductUseCase(cartRepository)
    }

    val updateCartProductUseCase by lazy {
        UpdateCartProductUseCase(cartRepository)
    }

    val getSearchHistoryUseCase by lazy {
        GetSearchHistoryUseCase(historyRepository)
    }

    val addSearchHistoryUseCase by lazy {
        AddSearchHistoryUseCase(historyRepository)
    }

    val getRecentSearchHistoryUseCase by lazy {
        GetRecentSearchHistoryUseCase(historyRepository)
    }

    val getCatalogProductsUseCase by lazy {
        GetCatalogProductsUseCase(productRepository, cartRepository)
    }

    val getCatalogProductUseCase by lazy {
        GetCatalogProductUseCase(productRepository, cartRepository)
    }

    val getCatalogProductsByProductIdsUseCase by lazy {
        GetCatalogProductsByProductIdsUseCase(productRepository, cartRepository)
    }

    val getCartProductsQuantityUseCase by lazy {
        GetCartProductsQuantityUseCase(cartRepository)
    }

    val getCartRecommendProductsUseCase by lazy {
        GetCartRecommendProductsUseCase(productRepository, cartRepository, historyRepository)
    }

    val orderProductsUseCase by lazy {
        OrderProductsUseCase(orderRepository)
    }
    val getCouponsUseCase by lazy {
        GetCouponsUseCase(couponRepository)
    }
}
