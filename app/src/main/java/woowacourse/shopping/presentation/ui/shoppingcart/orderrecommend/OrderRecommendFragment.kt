package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication
import woowacourse.shopping.databinding.FragmentOrderRecommendBinding
import woowacourse.shopping.presentation.base.BaseFragment

class OrderRecommendFragment : BaseFragment<FragmentOrderRecommendBinding>() {
    override val layoutResourceId: Int get() = R.layout.fragment_order_recommend

    private val viewModel: OrderRecommendViewModel by viewModels {
        OrderRecommendViewModel.factory(
            (requireContext().applicationContext as ShoppingApplication).productHistoryRepository,
            (requireContext().applicationContext as ShoppingApplication).orderRepository,
        )
    }

    override fun initViewCreated() {
        initDataBinding()
    }

    private fun initDataBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    companion object {
        const val TAG = "OrderRecommendFragment"
    }
}
