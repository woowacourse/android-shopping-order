package woowacourse.shopping.view.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.view.payment.adapter.PaymentAdapter
import woowacourse.shopping.view.product.catalog.ProductCatalogActivity

class PaymentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val app = application as ShoppingApplication
        ViewModelProvider(
            this,
            PaymentViewModelFactory(
                app.couponRepository,
                app.orderRepository,
            ),
        )[PaymentViewModel::class.java]
    }
    private val adapter by lazy {
        PaymentAdapter {
            viewModel.selectCoupon(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.title = getString(R.string.order_action_bar_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setUpView()
        initBindings()
        initObservers()

        viewModel.initSelectedProducts(intent.getSerializableExtra(KEY_SELECTED_PRODUCTS) as List<CartProduct>)
    }

    private fun setUpView() {
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initBindings() {
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.rvCoupons.adapter = adapter
    }

    private fun initObservers() {
        viewModel.coupons.observe(this) {
            adapter.submitList(it)
        }

        viewModel.onFinishOrder.observe(this) {
            Toast.makeText(this, R.string.finish_order, Toast.LENGTH_SHORT).show()
            val intent = ProductCatalogActivity.newIntent(this)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }

        viewModel.onError.observe(this) {
            Toast.makeText(this, it.messageId, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val KEY_SELECTED_PRODUCTS = "selected_products"

        fun newIntent(
            context: Context,
            selectedProducts: ArrayList<CartProduct>,
        ): Intent =
            Intent(
                context,
                PaymentActivity::class.java,
            ).apply {
                putExtra(KEY_SELECTED_PRODUCTS, selectedProducts)
            }
    }
}
