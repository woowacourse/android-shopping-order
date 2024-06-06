package woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication
import woowacourse.shopping.databinding.FragmentOrderRecommendBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.orderrecommend.adapter.RecommendAdapter
import woowacourse.shopping.presentation.ui.shoppingcart.payment.PaymentFragment

class OrderRecommendFragment :
    BaseFragment<FragmentOrderRecommendBinding>(R.layout.fragment_order_recommend) {
    private val viewModel: OrderRecommendViewModel by viewModels {
        OrderRecommendViewModel.factory(
            (requireContext().applicationContext as ShoppingApplication).productHistoryRepository,
            (requireContext().applicationContext as ShoppingApplication).shoppingCartRepository,
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
        binding.vm = viewModel
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
                is OrderRecommendNavigateAction.NavigateToPayment -> {
                    val bundle = Bundle()

                    bundle.putParcelableArray(
                        PaymentFragment.PUT_EXTRA_CART_IDS_KEY,
                        navigateAction.orderCarts.toTypedArray(),
                    )

                    val paymentFragment = PaymentFragment()
                    paymentFragment.arguments = bundle
                    parentFragmentManager.commit {
                        replace(R.id.fragment_container_view_main, paymentFragment)
                        addToBackStack(PaymentFragment.TAG)
                    }
                }
            }
        }
    }

    companion object {
        const val TAG = "OrderRecommendFragment"

        const val PUT_EXTRA_CART_IDS_KEY = "PUT_EXTRA_CART_IDS_KEY"
    }
}
