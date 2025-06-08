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
import woowacourse.shopping.view.util.getSerializableExtraCompat

class PaymentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }

    private lateinit var viewModel: PaymentViewModel

    private val paymentAdapter: PaymentAdapter by lazy { PaymentAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpView()
        val app = application as ShoppingApplication
        val selectedProducts =
            intent
                .getSerializableExtraCompat<ArrayList<CartProduct>>(KEY_SELECTED_PRODUCTS)
                .orEmpty()
        viewModel =
            ViewModelProvider(
                this,
                PaymentViewModelFactory(selectedProducts, app.couponRepository),
            )[PaymentViewModel::class.java]

        initBindings()
        initObservers()
        supportActionBar?.title = getString(R.string.pay)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
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
        binding.handler = viewModel
        binding.rvPayment.adapter = paymentAdapter
    }

    private fun initObservers() {
        viewModel.paymentItems.observe(this) { value ->
            paymentAdapter.submitList(value)
        }

        viewModel.finishOrderEvent.observe(this) {
            finish()
            Toast.makeText(this, R.string.finish_order, Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val KEY_SELECTED_PRODUCTS = "selectedProducts"

        fun newIntent(
            context: Context,
            selectedProducts: Set<CartProduct>,
        ): Intent =
            Intent(context, PaymentActivity::class.java).apply {
                putExtra(KEY_SELECTED_PRODUCTS, ArrayList(selectedProducts))
            }
    }
}
