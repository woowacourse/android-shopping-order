package woowacourse.shopping.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.product.catalog.CatalogActivity
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.util.parcelableArray

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var viewModel: OrderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        binding.lifecycleOwner = this

        applyWindowInsets()
        setSupportActionBar()
        setViewModel()
        setCouponAdapter()
        observeOrderViewModel()
    }

    private fun setViewModel() {
        val products: Array<ProductUiModel> =
            intent.parcelableArray<ProductUiModel>(KEY_PRODUCTS) ?: emptyArray()

        viewModel =
            ViewModelProvider(
                this,
                OrderViewModelFactory(products),
            )[OrderViewModel::class.java]

        binding.vm = viewModel
    }

    private fun setCouponAdapter() {
        val adapter =
            CouponAdapter(
                checkClickListener = { viewModel.applyCoupon(it) },
            )
        binding.recyclerViewOrder.adapter = adapter
    }

    private fun observeOrderViewModel() {
        val couponAdapter: CouponAdapter = binding.recyclerViewOrder.adapter as CouponAdapter

        viewModel.availableDisplayingCoupons.observe(this, couponAdapter::submitList)
        viewModel.checkSelected.observe(this, couponAdapter::applyCoupon)
        viewModel.isOrderMade.observe(this) {
            when (it) {
                true -> startActivityWhenOrderMade()
                false -> Toast.makeText(this, "주문에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startActivityWhenOrderMade() {
        Toast.makeText(this, "주문이 완료되었습니다.", Toast.LENGTH_SHORT).show()

        val intent =
            Intent(this, CatalogActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
        startActivity(intent)
        finish()
    }

    private fun setSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.text_order_action_bar)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    companion object {
        fun newIntent(
            context: Context,
            products: Array<ProductUiModel>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra(KEY_PRODUCTS, products)
            }

        private const val KEY_PRODUCTS = "PRODUCTS"
    }
}
