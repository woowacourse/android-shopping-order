package woowacourse.shopping.presentation.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class CartFragmentFactory(private val cartViewModel: CartViewModel) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment {
        return when (className) {
            CartRecommendFragment::class.java.name -> CartRecommendFragment(cartViewModel)
            CartSelectionFragment::class.java.name -> CartSelectionFragment(cartViewModel)
            else -> super.instantiate(classLoader, className)
        }
    }
}
