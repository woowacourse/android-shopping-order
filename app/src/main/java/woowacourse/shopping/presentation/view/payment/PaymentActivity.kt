package woowacourse.shopping.presentation.view.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.presentation.view.ShoppingActivity
import woowacourse.shopping.presentation.view.payment.adapter.CouponAdapter
import woowacourse.shopping.presentation.view.payment.event.PaymentMessageEvent

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding

    private val viewModel: PaymentViewModel by viewModels {
        PaymentViewModel.Factory(getExtraOrderProductIds())
    }

    private val couponAdapter: CouponAdapter by lazy { CouponAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        setWindowInsets()
        setupUI()
        setObserver()
    }

    private fun initBinding() {
        binding = ActivityPaymentBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        setContentView(binding.root)
    }

    private fun setWindowInsets() {
        enableEdgeToEdge()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupUI() {
        setAdapter()
        setupActionBar()
    }

    private fun setAdapter() {
        binding.recyclerViewCoupon.adapter = couponAdapter
    }

    private fun setObserver() {
        binding.vm = viewModel

        viewModel.coupons.observe(this) {
            couponAdapter.submitList(it)
        }

        viewModel.toastEvent.observe(this) {
            Toast.makeText(this, it.toMessageResId(), Toast.LENGTH_SHORT).show()
        }

        viewModel.orderSuccessEvent.observe(this) {
            val intent = ShoppingActivity.newIntent(this)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun setupActionBar() {
        binding.toolbarCoupon.setNavigationIcon(R.drawable.ic_arrow)
        binding.toolbarCoupon.setNavigationOnClickListener { finish() }
    }

    private fun getExtraOrderProductIds(): List<Long> = intent.getLongArrayExtra(EXTRA_ORDER_PRODUCT_IDS)?.toList() ?: emptyList()

    private fun PaymentMessageEvent.toMessageResId(): Int =
        when (this) {
            PaymentMessageEvent.PAYMENT_SUMMARY_FAILURE ->
                R.string.payment_screen_event_message_payment_summary_failure

            PaymentMessageEvent.PAYMENT_CALCULATION_FAILURE ->
                R.string.payment_screen_event_message_calculation_payment_summary_failure

            PaymentMessageEvent.COUPON_FETCH_FAILURE ->
                R.string.payment_screen_event_message_fetch_coupon_failure

            PaymentMessageEvent.ORDER_SUCCESS ->
                R.string.payment_screen_event_message_order_success

            PaymentMessageEvent.ORDER_FAILURE ->
                R.string.payment_screen_event_message_order_failure
        }

    companion object {
        private const val EXTRA_ORDER_PRODUCT_IDS = "EXTRA_ORDER_PRODUCT_IDS"

        fun newIntent(
            context: Context,
            orderProductIds: List<Long>,
        ): Intent =
            Intent(context, PaymentActivity::class.java).putExtra(
                EXTRA_ORDER_PRODUCT_IDS,
                orderProductIds.toLongArray(),
            )
    }
}
