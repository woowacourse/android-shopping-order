package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import android.os.Build
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication
import woowacourse.shopping.databinding.FragmentOrderRecommendBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.adapter.RecommendAdapter

class OrderRecommendFragment : BaseFragment<FragmentOrderRecommendBinding>() {
    override val layoutResourceId: Int get() = R.layout.fragment_order_recommend

    private val viewModel: OrderRecommendViewModel by viewModels {
        OrderRecommendViewModel.factory(
            (requireContext().applicationContext as ShoppingApplication).productHistoryRepository,
            (requireContext().applicationContext as ShoppingApplication).shoppingCartRepository,
            (requireContext().applicationContext as ShoppingApplication).orderRepository,
        )
    }

    private val adapter: RecommendAdapter by lazy { RecommendAdapter(viewModel) }

    override fun initViewCreated() {
        initDataBinding()
        initAdapter()
        initObserve()

        val orderCarts =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArray(PUT_EXTRA_CART_IDS_KEY, Cart::class.java)
            } else {
                arguments?.getParcelableArray(PUT_EXTRA_CART_IDS_KEY)
            }

        orderCarts?.let { carts ->
            viewModel.load(carts.map { it as Cart })
        }
    }

    private fun initDataBinding() {
        binding.apply {
            vm = viewModel
            lifecycleOwner = viewLifecycleOwner
        }
    }

    private fun initAdapter() {
        binding.rvRecommendOrder.adapter = adapter
    }

    private fun initObserve() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            adapter.submitList(state.recommendCarts)
        }

        viewModel.navigateAction.observeEvent(viewLifecycleOwner) { navigateAction ->
            when (navigateAction) {
                is OrderRecommendNavigateAction.NavigateToProductList -> activity?.finish()
            }
        }
    }

    companion object {
        const val TAG = "OrderRecommendFragment"

        const val PUT_EXTRA_CART_IDS_KEY = "PUT_EXTRA_CART_IDS_KEY"
    }
}
