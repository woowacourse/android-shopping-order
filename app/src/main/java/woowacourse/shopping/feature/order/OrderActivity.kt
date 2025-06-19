package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.application.ShoppingApplication
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.cart.adapter.CartGoodsItem

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels {
        val app = application as ShoppingApplication
        OrderViewModelFactory(
            app.couponRepository,
            app.orderRepository,
        )
    }
    private lateinit var adapter: CouponAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        adapter = CouponAdapter(viewModel.couponHandler)
        binding.couponAdapter = adapter
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        val orders = intent.getSerializableExtra(EXTRA_CARTS) as List<CartGoodsItem>

        viewModel.fetchCoupons(orders)
        viewModel.uiState.observe(this) {
            adapter.submitList(it.coupons)
        }
//        viewModel.moveToMainEvent.observe(this) {
//            finish()
//        }
    }

    companion object {
        fun newIntent(
            context: Context,
            carts: List<CartGoodsItem>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra(EXTRA_CARTS, ArrayList(carts))
            }

        const val EXTRA_CARTS = "carts"
    }
}
