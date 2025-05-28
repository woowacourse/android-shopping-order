package woowacourse.shopping.view.cart

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory

class CartProductSelectionFragmentFactory(private val viewModel: ShoppingCartViewModel) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment {
        return when (className) {
            CartProductSelectionFragment::class.java.name -> CartProductSelectionFragment(viewModel)
            else -> super.instantiate(classLoader, className)
        }
    }
}
