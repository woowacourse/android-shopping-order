package woowacourse.shopping.presentation.order

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.Extra
import woowacourse.shopping.presentation.product.ProductActivity

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels { OrderViewModelFactory() }
    private val couponAdapter: CouponAdapter by lazy { CouponAdapter(viewModel) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        viewModel.loadOrderInfos(
            intent.getLongArrayExtra(Extra.KEY_ORDER_PRODUCT_IDS) ?: longArrayOf(),
        )

        initInsets()
        initAdapter()
        setupToolbar()
        observeViewModel()
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initAdapter() {
        binding.rvCoupon.adapter = couponAdapter
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.tbOrder)
        binding.tbOrder.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.coupons.observe(this) { coupons ->
            couponAdapter.submitList(coupons)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }

        viewModel.navigateTo.observe(this) {
            val intent = Intent(this, ProductActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newIntent(
            context: Context,
            productIds: LongArray,
        ): Intent =
            Intent(context, OrderActivity::class.java).apply {
                putExtra(Extra.KEY_ORDER_PRODUCT_IDS, productIds)
            }
    }
}
