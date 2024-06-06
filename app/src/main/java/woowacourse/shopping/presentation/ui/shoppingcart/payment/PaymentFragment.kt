package woowacourse.shopping.presentation.ui.shoppingcart.payment

import android.os.Build
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.base.observeEvent

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModel.factory()
    }

    override fun initViewCreated() {
        initDataBinding()
        initObserve()

        val orderCarts =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arguments?.getParcelableArray(PUT_EXTRA_CART_IDS_KEY, Cart::class.java)
            } else {
                arguments?.getParcelableArray(PUT_EXTRA_CART_IDS_KEY)
            }
    }

    private fun initDataBinding() {
        binding.vm = viewModel
    }

    private fun initObserve() {
        viewModel.navigateAction.observeEvent(viewLifecycleOwner) { navigateAction ->
            when (navigateAction) {
                is PaymentNavigateAction.NavigateToProductList -> activity?.finish()
            }
        }
    }

    companion object {
        const val TAG = "PaymentFragment"

        const val PUT_EXTRA_CART_IDS_KEY = "PUT_EXTRA_CART_IDS_KEY"
    }
}
