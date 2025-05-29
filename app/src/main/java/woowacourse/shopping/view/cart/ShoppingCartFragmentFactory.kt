package woowacourse.shopping.view.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommendation.CartProductRecommendationFragment
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragment

class ShoppingCartFragmentFactory(
    private val productRepository: ProductRepository,
    private val cartProductRepository: CartProductRepository,
    private val recentProductRepository: RecentProductRepository,
) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment =
        when (className) {
            CartProductSelectionFragment::class.java.name -> CartProductSelectionFragment(cartProductRepository)
            CartProductRecommendationFragment::class.java.name ->
                CartProductRecommendationFragment(
                    productRepository,
                    cartProductRepository,
                    recentProductRepository,
                )
            else -> super.instantiate(classLoader, className)
        }
}
