package woowacourse.shopping.feature.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.feature.payment.adapter.PaymentCouponAdapter
import woowacourse.shopping.feature.payment.adapter.PaymentCouponViewHolder.CouponClickListener

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: PaymentCouponAdapter
    private val viewModel: PaymentViewModel by viewModels {
        (application as ShoppingApplication).paymentFactory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        setupAdapter()
        binding.rvCoupons.adapter = adapter
        binding.rvCoupons.isNestedScrollingEnabled = false

        val orderIds = intent.getLongArrayExtra(ORDER_IDS) ?: longArrayOf()
        viewModel.setOrderDetails(orderIds)
    }

    private fun setupAdapter() {
        adapter =
            PaymentCouponAdapter(
                object : CouponClickListener {
                    override fun onCouponCheck(coupon: Coupon) {
                        viewModel.toggleCheck(coupon)
                    }
                },
            )
    }

    companion object {
        private const val ORDER_IDS = "orderIds"

        fun newIntent(
            context: Context,
            orderIds: LongArray,
        ): Intent =
            Intent(context, PaymentActivity::class.java).apply {
                putExtra(ORDER_IDS, orderIds)
            }
    }
}
