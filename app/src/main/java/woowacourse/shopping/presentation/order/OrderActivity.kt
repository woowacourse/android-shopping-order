package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.presentation.util.getArrayListExtraCompat
import java.util.ArrayList

class OrderActivity : AppCompatActivity() {
    private val binding: ActivityOrderBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_order)
    }

    private val viewModel: OrderViewModel by viewModels {
        OrderViewModel.FACTORY
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        setUpBinding()
        setUpCheckedProducts()
        setUpCoupons()
        supportActionBar?.hide()
    }

    private fun setUpScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setUpBinding() {
        binding.apply {
            lifecycleOwner = this@OrderActivity
            vm = viewModel
        }
    }

    private fun setUpCheckedProducts() {
        val checkedProducts =
            intent.getArrayListExtraCompat(ORDER_KEY, ProductUiModel::class.java) ?: emptyList()
        viewModel.loadSelectedProducts(checkedProducts)
    }

    private fun setUpCoupons() {
        val adapter = CouponAdapter {
            viewModel.selectCoupon(it)
        }
        binding.rvCoupons.adapter = adapter
        viewModel.coupons.observe(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        private const val ORDER_KEY = "Order"

        fun newIntent(
            context: Context,
            products: List<ProductUiModel>,
        ): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putParcelableArrayListExtra(ORDER_KEY, ArrayList(products))
            }
        }
    }
}