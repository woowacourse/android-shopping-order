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
import woowacourse.shopping.presentation.ui.payment.adapter.CouponAdapter
import woowacourse.shopping.presentation.ui.shopping.ShoppingActivity
import woowacourse.shopping.presentation.util.EventObserver

class PaymentActivity : BindingActivity<ActivityPaymentBinding>() {
    override val layoutResourceId = R.layout.activity_payment
    val viewModel: PaymentViewModel by viewModels { ViewModelFactory() }
    private lateinit var couponAdapter: CouponAdapter

    override fun initStartView(savedInstanceState: Bundle?) {
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        viewModel.fetchInitialData()
        initActionBarTitle()
        initRecyclerViewAdapter()
        observeCoupon()
        observePaymentEvent()
    }

    private fun initActionBarTitle() {
        title = getString(R.string.cart_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRecyclerViewAdapter() {
        couponAdapter = CouponAdapter(viewModel)
        binding.rvCoupon.adapter = couponAdapter
        binding.rvCoupon.layoutManager = LinearLayoutManager(this)
    }

    private fun observeCoupon() {
        viewModel.coupons.observe(this) {
            couponAdapter.submitList(it.toList())
        }
    }

    private fun observePaymentEvent() {
        viewModel.paymentEvent.observe(
            this,
            EventObserver { event ->
                when (event) {
                    PaymentEvent.FinishOrder -> {
                        showToast("상품을 주문했습니다.")
                        ShoppingActivity.start(this)
                    }
                }
            },
        )
    }

    companion object {
        fun start(context: Context) {
            Intent(context, PaymentActivity::class.java).apply {
                context.startActivity(this)
            }
        }
    }
}
