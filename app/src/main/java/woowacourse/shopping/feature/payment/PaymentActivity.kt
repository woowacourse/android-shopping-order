package woowacourse.shopping.feature.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.feature.goods.GoodsActivity
import woowacourse.shopping.feature.payment.adapter.PaymentCouponAdapter
import woowacourse.shopping.feature.payment.adapter.PaymentCouponViewHolder.CouponClickListener

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: PaymentCouponAdapter
    private val viewModel: PaymentViewModel by viewModels {
        val orderIds = intent.getLongArrayExtra(ORDER_IDS) ?: longArrayOf()
        (application as ShoppingApplication).paymentFactory(orderIds)
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

        viewModel.orderCompletedEvent.observe(this) {
            Toast
                .makeText(
                    this,
                    getString(R.string.payment_complete_order_message),
                    Toast.LENGTH_SHORT,
                ).show()
            navigate()
        }
    }

    private fun setupAdapter() {
        adapter =
            PaymentCouponAdapter(
                object : CouponClickListener {
                    override fun onCouponCheck(couponRule: Coupon) {
                        viewModel.toggleCheck(couponRule)
                    }
                },
            )
    }

    private fun navigate() {
        val orderedIds = viewModel.orderedCarts.map { it.product.id }
        val intent = GoodsActivity.newIntent(this, orderedIds)
        startActivity(intent)
        finish()
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
