package woowacourse.shopping.view.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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

class PaymentActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPaymentBinding.inflate(layoutInflater) }
    private val viewModel by lazy {
        val app = application as ShoppingApplication
        ViewModelProvider(
            this,
            PaymentViewModelFactory(
                app.cartProductRepository,
                app.couponRepository,
            ),
        )[PaymentViewModel::class.java]
    }
    private val adapter by lazy { PaymentAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            Log.d("hwannow_log", "$it")
            adapter.submitList(it)
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
