package woowacourse.shopping.view.cart.selection

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import woowacourse.shopping.view.cart.ShoppingCartViewModel

class CartProductSelectionFragmentFactory(
    private val viewModel: ShoppingCartViewModel,
) : FragmentFactory() {
    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment =
        when (className) {
            CartProductSelectionFragment::class.java.name -> CartProductSelectionFragment(viewModel)
            else -> super.instantiate(classLoader, className)
        }
}
