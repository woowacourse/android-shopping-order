package woowacourse.shopping.view.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.App
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.view.payment.adapter.CouponAdapter
import woowacourse.shopping.view.payment.vm.PaymentViewModel
import woowacourse.shopping.view.payment.vm.PaymentViewModelFactory

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private val viewModel: PaymentViewModel by viewModels {
        val container = (application as App).container
        PaymentViewModelFactory(container.couponRepository, container.cartRepository, container.orderRepository)
    }
    private val couponAdapter: CouponAdapter by lazy {
        CouponAdapter(viewModel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        val orders = intent.getParcelableArrayListExtra<ShoppingCart>(EXTRA_ORDERS) ?: arrayListOf()
        viewModel.setCartItems(orders)
        setUpSystemBars()
        setUpBinding()
        observeViewModel()
        binding.paymentRvCoupon.adapter = couponAdapter
    }

    private fun setUpSystemBars() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.action_bar_title_payment_screen)
    }

    private fun setUpBinding() {
        with(binding) {
            vm = viewModel
            lifecycleOwner = this@PaymentActivity
        }
    }

    private fun observeViewModel() {
        viewModel.paymentUiState.observe(this) {
            couponAdapter.updateItems(it.coupons)
        }

        viewModel.isCompletedOrder.observe(this) {
            Toast.makeText(this, getString(R.string.text_completed_order), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val EXTRA_ORDERS = "orders"

        fun newIntent(
            context: Context,
            orders: List<ShoppingCart>,
        ): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putParcelableArrayListExtra(EXTRA_ORDERS, ArrayList(orders))
            }
        }
    }
}
