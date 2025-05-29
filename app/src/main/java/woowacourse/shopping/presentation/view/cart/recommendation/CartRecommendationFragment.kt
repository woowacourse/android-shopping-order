package woowacourse.shopping.presentation.view.cart.recommendation

import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommendationBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.model.ProductUiModel
import woowacourse.shopping.presentation.view.ItemCounterListener
import woowacourse.shopping.presentation.view.cart.CartViewModel

class CartRecommendationFragment :
    BaseFragment<FragmentCartRecommendationBinding>(R.layout.fragment_cart_recommendation),
    ItemCounterListener {
    private val viewModel: CartViewModel by viewModels(
        ownerProducer = { requireParentFragment() },
        factoryProducer = { CartViewModel.Factory },
    )

    override fun increase(product: ProductUiModel) {
        TODO("Not yet implemented")
    }

    override fun decrease(product: ProductUiModel) {
        TODO("Not yet implemented")
    }
}
