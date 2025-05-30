package woowacourse.shopping.view.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.view.cart.recommend.CartProductRecommendFragment
import woowacourse.shopping.view.cart.select.CartProductSelectFragment

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
            CartProductSelectFragment::class.java.name -> CartProductSelectFragment(cartProductRepository)
            CartProductRecommendFragment::class.java.name ->
                CartProductRecommendFragment(
                    productRepository,
                    cartProductRepository,
                    recentProductRepository,
                )
            else -> super.instantiate(classLoader, className)
        }
}
