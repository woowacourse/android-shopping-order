package woowacourse.shopping.presentation.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.presentation.payment.event.CouponEventHandlerImpl
import woowacourse.shopping.presentation.product.catalog.CatalogActivity
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.recommend.OrderEvent
import woowacourse.shopping.presentation.recommend.OrderEvent.OrderItemSuccess
import woowacourse.shopping.presentation.util.IntentCompat

class PaymentActivity : AppCompatActivity() {
    private lateinit var couponAdapter: CouponAdapter
    private val orderItems: List<ProductUiModel> by lazy { requireOrderItems() }

    private val paymentViewModel: PaymentViewModel by viewModels {
        PaymentViewModel.provideFactory(orderItems)
    }
    private val binding: ActivityPaymentBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_payment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAdapter()
        setUpBinding()
        setUpScreen()
        setupToolbar()
        observeViewModel()
        paymentViewModel.getCoupons()
    }

    private fun setUpScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_payment_toolbar)
        }
    }

    private fun setUpBinding() {
        binding.apply {
            viewModel = paymentViewModel
            lifecycleOwner = this@PaymentActivity
            recyclerViewCoupon.adapter = couponAdapter
        }
    }

    private fun setAdapter() {
        val handler = createHandler()
        couponAdapter = CouponAdapter(handler)
    }

    private fun observeViewModel() {
        paymentViewModel.coupons.observe(this) { coupons ->
            couponAdapter.submitList(coupons)
        }

        paymentViewModel.selectedCouponId.observe(this) { selectedCouponId ->
            couponAdapter.couponCheck(selectedCouponId)
        }

        paymentViewModel.orderEvent.observe(this) { state ->
            when (state) {
                is OrderItemSuccess -> {
                    navigateToMain()
                    showToast(R.string.message_order_success)
                }

                is OrderEvent.OrderItemFailure -> {
                    showToast(R.string.message_order_fail)
                }
            }
        }
    }

    private fun showToast(messageResId: Int) {
        Toast.makeText(this, getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    private fun navigateToMain() {
        val intent = Intent(this, CatalogActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

    private fun requireOrderItems(): List<ProductUiModel> {
        return IntentCompat.getParcelableArrayListExtra(
            intent,
            ORDER_ITEMS_KEY,
        ) ?: emptyList()
    }

    private fun createHandler(): CouponEventHandlerImpl = CouponEventHandlerImpl(paymentViewModel)

    companion object {
        private const val ORDER_ITEMS_KEY = "OrderItems"

        fun newIntent(
            context: Context,
            checkedItems: ArrayList<ProductUiModel>,
        ): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putParcelableArrayListExtra(ORDER_ITEMS_KEY, checkedItems)
            }
        }
    }
}
