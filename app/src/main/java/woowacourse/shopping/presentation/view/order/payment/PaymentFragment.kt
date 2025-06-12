package woowacourse.shopping.presentation.view.order.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.ShoppingActivity
import woowacourse.shopping.presentation.view.order.OrderViewModel
import woowacourse.shopping.presentation.view.order.payment.adapter.CouponAdapter
import woowacourse.shopping.presentation.view.order.toMessageResId

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private val orderViewModel: OrderViewModel by activityViewModels()
    private val paymentViewModel: PaymentViewModel by viewModels {
        PaymentViewModel.Factory(
            orderViewModel.selectedCartIds,
        )
    }
    private val couponAdapter: CouponAdapter by lazy { CouponAdapter(paymentViewModel) }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initCoupons()
        setupActionBar()
    }

    private fun initCoupons() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewCoupons.adapter = couponAdapter
        binding.paymentViewModel = paymentViewModel
        paymentViewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            couponAdapter.submitList(coupons)
        }

        orderViewModel.toastOrderEvent.observe(viewLifecycleOwner) { event ->
            showToast(event.toMessageResId())
        }

        binding.textViewPayment.setOnClickListener {
            orderViewModel.orderCartItems()
            navigateToShopping()
        }
    }

    private fun setupActionBar() {
        binding.toolbarPayment.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarPayment.setNavigationOnClickListener {
            navigateToSuggestion()
        }
    }

    private fun navigateToSuggestion() {
        parentFragmentManager.commit {
            parentFragmentManager.popBackStack()
        }
    }

    private fun navigateToShopping() {
        val intent = ShoppingActivity.newIntent(requireContext())
        startActivity(intent)
        requireActivity().finish()
    }
}
