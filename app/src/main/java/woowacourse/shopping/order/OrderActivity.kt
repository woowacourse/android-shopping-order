package woowacourse.shopping.order

import android.content.Context
import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.product.catalog.ProductUiModel

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
        observeOrderViewModel()
        setCouponAdapter()
    }

    private fun setViewModel() {
        val products: Array<ProductUiModel> =
            intent.parcelableArray<ProductUiModel>("PRODUCTS") ?: emptyArray()

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
        viewModel.availableCoupons.observe(this) {
            (binding.recyclerViewOrder.adapter as CouponAdapter).submitList(it)
        }
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

    inline fun <reified T : Parcelable> Intent.parcelableArray(key: String): Array<T>? =
        when {
            SDK_INT >= 33 -> {
                @Suppress("DEPRECATION")
                getParcelableArrayExtra(key)?.filterIsInstance<T>()?.toTypedArray()
            }

            else -> {
                @Suppress("DEPRECATION")
                getParcelableArrayExtra(key)?.filterIsInstance<T>()?.toTypedArray()
            }
        }

    companion object {
        fun newIntent(
            context: Context,
            products: Array<ProductUiModel>,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra("PRODUCTS", products)
            }
    }
}
