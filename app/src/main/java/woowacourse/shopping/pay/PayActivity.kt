package woowacourse.shopping.pay

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPayBinding
import woowacourse.shopping.product.catalog.ProductUiModel


class PayActivity : AppCompatActivity() {
    private val binding: ActivityPayBinding by lazy {
        DataBindingUtil.setContentView(
            this,
            R.layout.activity_pay
        )
    }
    private val viewModel: PayViewModel by lazy {
        ViewModelProvider(
            this,
            PayViewModelFactory(),
        )[PayViewModel::class.java]
    }
    private val adapter: CouponAdapter by lazy {
        CouponAdapter(
            coupons = viewModel.couponList.value.orEmpty(),
            onCheckClick = { coupon, position -> viewModel.updateCoupon(coupon) }
        )
    }
    private val orderProducts by lazy {
        intent.getParcelableArrayListExtra<ProductUiModel>(KEY_PRODUCT_ORDER)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initActionBar()
        initDataBinding()
        observeData()
        setOrderProducts()
    }

    private fun setOrderProducts() {
        val products = intent.getParcelableArrayListExtra<ProductUiModel>(KEY_PRODUCT_ORDER)
        viewModel.getOrderProducts(products?.toList().orEmpty())
    }

    private fun initActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.pay)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }


    private fun initView() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun observeData() {
        viewModel.orderProducts.observe(this) {
            viewModel.getOrderAmount()
            viewModel.getTotalAmount()
        }
        viewModel.couponList.observe(this) { couponList -> }
    }

    private fun initDataBinding() {
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.recyclerViewPay.adapter = adapter
    }

    companion object {
        private const val KEY_PRODUCT_ORDER = "orderProducts"

        fun newIntent(context: Context, orderProducts: List<ProductUiModel>): Intent =
            Intent(context, PayActivity::class.java).apply {
                putParcelableArrayListExtra(KEY_PRODUCT_ORDER, ArrayList(orderProducts))
            }
    }
}
