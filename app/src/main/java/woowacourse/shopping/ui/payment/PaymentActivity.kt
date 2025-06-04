package woowacourse.shopping.ui.payment

import android.os.Bundle
import androidx.activity.viewModels
import com.google.android.material.snackbar.Snackbar
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.common.DataBindingActivity
import woowacourse.shopping.ui.payment.adapter.PaymentCouponAdapter

class PaymentActivity : DataBindingActivity<ActivityPaymentBinding>(R.layout.activity_payment) {
    private val viewModel: PaymentViewModel by viewModels { PaymentViewModel.Factory }
    private val paymentCouponAdapter: PaymentCouponAdapter by lazy {
        PaymentCouponAdapter { couponId -> viewModel.toggleCoupon(couponId) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.paymentCouponsContainer.adapter = paymentCouponAdapter
        initObservers()
    }

    private fun initObservers() {
        viewModel.coupons.observe(this) {
            paymentCouponAdapter.submitList(it.value)
        }
        viewModel.isError.observe(this) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}
