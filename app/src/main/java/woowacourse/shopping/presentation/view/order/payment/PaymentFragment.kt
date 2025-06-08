package woowacourse.shopping.presentation.view.order.payment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentPaymentBinding
import woowacourse.shopping.presentation.base.BaseFragment
import woowacourse.shopping.presentation.view.order.OrderViewModel
import woowacourse.shopping.presentation.view.order.payment.adapter.CouponAdapter

class PaymentFragment : BaseFragment<FragmentPaymentBinding>(R.layout.fragment_payment) {
    private val orderViewModel: OrderViewModel by activityViewModels()
    private val paymentViewModel: PaymentViewModel by viewModels { PaymentViewModel.Factory }
    private val couponAdapter: CouponAdapter by lazy { CouponAdapter() }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initCoupons()
    }

    private fun initCoupons() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerViewCoupons.adapter = couponAdapter
        paymentViewModel.coupons.observe(viewLifecycleOwner) { coupons ->
            couponAdapter.submitList(coupons)
        }
    }
}
