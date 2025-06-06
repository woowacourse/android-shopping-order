package woowacourse.shopping.presentation.payment

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding

class PaymentActivity : AppCompatActivity() {
    private lateinit var couponAdapter : CouponAdapter

    private val viewModel: PaymentViewModel  by viewModels{
        PaymentViewModel.FACTORY
    }
    private val binding: ActivityPaymentBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_payment)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpScreen()
        setupToolbar()
        setAdapter()
        setUpBinding()
        viewModel.getCoupons()
        observeViewModel()
    }

    private fun setUpScreen() {
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupToolbar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.text_payment_toolbar)
        }
    }

    private fun setUpBinding() {
        binding.apply {
            binding.viewModel = viewModel
            lifecycleOwner = this@PaymentActivity
            recyclerViewCoupon.adapter = couponAdapter
        }
    }

    private fun setAdapter() {
        couponAdapter =
            CouponAdapter()
    }

    private fun observeViewModel() {
        viewModel.coupons.observe(this) {  coupons->
            couponAdapter.submitList(coupons)
        }
    }
}