package woowacourse.shopping.view.payment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCouponApplyBinding

class CouponApplyActivity : AppCompatActivity() {
    private val binding: ActivityCouponApplyBinding by lazy {
        ActivityCouponApplyBinding.inflate(layoutInflater)
    }
    private val viewModel: CouponApplyViewModel by viewModels()
    private val adapter = CouponsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.couponApplyRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.onClickBackButton = { finish() }

        binding.couponApplyCoupons.adapter = adapter

        viewModel.event.observe(this) { event ->
            when (event) {
                else -> {}
            }
        }

        viewModel.state.observe(this) { state: CouponApplyState ->
            adapter.submitList(state.coupons)
            binding.couponApplyOrderAmount.text = state.orderAmount.toString()
            binding.couponApplyCouponDiscountAmount.text = state.discountAmount.toString()
            binding.couponApplyDeliveryFee.text = state.deliveryFee.toString()
            binding.couponApplyTotalPaymentAmount.text = state.totalPaymentAmount.toString()
        }
    }
}
