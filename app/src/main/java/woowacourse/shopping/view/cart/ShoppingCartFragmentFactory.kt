package woowacourse.shopping.view.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.view.cart.recommend.CartProductRecommendFragment
import woowacourse.shopping.view.cart.select.CartProductSelectFragment

class ShoppingCartFragmentFactory(
    private val application: ShoppingApplication,
) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment =
        when (className) {
            CartProductSelectFragment::class.java.name -> CartProductSelectFragment(application)
            CartProductRecommendFragment::class.java.name -> CartProductRecommendFragment(application)
            else -> super.instantiate(classLoader, className)
        }
}
