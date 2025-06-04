package woowacourse.shopping.presentation.order

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

class OrderActivity :
    AppCompatActivity(),
    CouponClickListener {
    private lateinit var binding: ActivityOrderBinding
    private val viewModel: OrderViewModel by viewModels { OrderViewModelFactory() }
    private val couponAdapter: CouponAdapter by lazy {
        CouponAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setupBinding()
//        setupViewModel()
        initInsets()
        setupToolbar()
        initAdapter()
        observeViewModel()
    }

    private fun setupBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        binding.lifecycleOwner = this
    }

    private fun initInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.clOrder) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbarOrder)
        binding.toolbarOrder.apply {
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener {
                finish()
            }
        }
    }

    private fun initAdapter() {
        binding.rvCoupon.apply {
            adapter = couponAdapter
            itemAnimator = null
        }
    }

    private fun observeViewModel() {
        viewModel.coupons.observe(this) { coupons ->
            couponAdapter.submitList(coupons)
        }

        viewModel.toastMessage.observe(this) { resId ->
            showToast(resId)
        }
    }

    private fun showToast(
        @StringRes messageResId: Int,
    ) {
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, OrderActivity::class.java)
    }

    override fun onClickSelect(cartId: Long) {
        TODO("Not yet implemented")
    }
}
