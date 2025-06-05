package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
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
        initPaymentProductsInfo()
        initCouponsView()
        initObservers()
        initSupportActionBar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun initPaymentProductsInfo() {
        val productIds: List<Long> = intent.getLongArrayExtra(KEY_PRODUCT_IDS)?.toList() ?: emptyList()
        viewModel.loadProducts(productIds)
    }

    private fun initCouponsView() {
        binding.paymentCouponsContainer.adapter = paymentCouponAdapter
        binding.paymentCouponsContainer.isNestedScrollingEnabled = false
        binding.paymentCouponsContainer.itemAnimator = null
    }

    private fun initObservers() {
        viewModel.coupons.observe(this) {
            paymentCouponAdapter.submitList(it.value)
        }
        viewModel.isNetworkError.observe(this) { errorMessage ->
            errorMessage?.let {
                Snackbar.make(binding.root, it, Snackbar.LENGTH_SHORT).show()
            }
        }
        viewModel.isOrderSuccess.observe(this) {
            Toast.makeText(this, getString(R.string.payment_order_success), Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.cart_title)
    }

    companion object {
        private const val KEY_PRODUCT_IDS = "PRODUCT_IDS"

        fun newIntent(
            context: Context,
            productIds: LongArray,
        ): Intent =
            Intent(context, PaymentActivity::class.java).apply {
                putExtra(KEY_PRODUCT_IDS, productIds)
            }
    }
}
