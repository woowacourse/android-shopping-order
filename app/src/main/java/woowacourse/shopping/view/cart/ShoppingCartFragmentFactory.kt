package woowacourse.shopping.view.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import woowacourse.shopping.view.cart.recommendation.CartProductRecommendationFragment
import woowacourse.shopping.view.cart.selection.CartProductSelectionFragment

class ShoppingCartFragmentFactory(
    private val viewModel: ShoppingCartViewModel,
) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment =
        when (className) {
            CartProductSelectionFragment::class.java.name -> CartProductSelectionFragment(viewModel)
            CartProductRecommendationFragment::class.java.name -> CartProductRecommendationFragment(viewModel)
            else -> super.instantiate(classLoader, className)
        }
}
