package woowacourse.shopping.presentation.ui.shoppingcart.payment

import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.app.ShoppingApplication
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.observeEvent
import woowacourse.shopping.presentation.ui.shoppingcart.payment.adapter.CouponsAdapter

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModel.factory(
            (requireContext().applicationContext as ShoppingApplication).couponRepository,
            (requireContext().applicationContext as ShoppingApplication).orderRepository,
        )
    }

    private val adapter: CouponsAdapter by lazy { CouponsAdapter(viewModel) }

    override fun initViewCreated() {
        initDataBinding()
        initObserve()
    }

    private fun initDataBinding() {
        binding.vm = viewModel
        binding.rvCoupons.adapter = adapter
    }

    private fun initObserve() {
        viewModel.navigateAction.observeEvent(viewLifecycleOwner) { navigateAction ->
            when (navigateAction) {
                is PaymentNavigateAction.NavigateToProductList -> {
                    showToastMessage(getString(R.string.payment_success_message))
                    activity?.finish()
                }
            }
        }

        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
            adapter.submitList(uiState.coupons)
        }
    }

    companion object {
        const val TAG = "PaymentFragment"
        const val PUT_EXTRA_CART_IDS_KEY = "PUT_EXTRA_CART_IDS_KEY"
    }
}
