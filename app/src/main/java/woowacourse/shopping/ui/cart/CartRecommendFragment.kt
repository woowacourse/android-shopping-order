package woowacourse.shopping.ui.cart

import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommandBinding
import woowacourse.shopping.ui.common.DataBindingFragment

class CartRecommendFragment : DataBindingFragment<FragmentCartRecommandBinding>(R.layout.fragment_cart_recommand) {
    private val viewModel: CartViewModel by activityViewModels<CartViewModel>()
}
