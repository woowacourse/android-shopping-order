package woowacourse.shopping.presentation.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.ViewModelFactory

class PaymentActivity : BindingActivity<ActivityPaymentBinding>() {
    override val layoutResourceId = R.layout.activity_payment
    val viewModel: PaymentViewModel by viewModels { ViewModelFactory() }
    private lateinit var couponAdapter: CouponAdapter

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        couponAdapter = CouponAdapter()
        binding.rvCoupon.adapter = couponAdapter
        binding.rvCoupon.layoutManager = LinearLayoutManager(this)

        initActionBarTitle()
        viewModel.fetchInitialData()
        viewModel.coupon.observe(this) {
            couponAdapter.submitList(it)
        }
    }

    private fun initActionBarTitle() {
        title = getString(R.string.cart_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        fun start(context: Context) {
            Intent(context, PaymentActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }
}
