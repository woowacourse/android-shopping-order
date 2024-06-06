package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.coupon.CouponRepositoryImpl
import woowacourse.shopping.data.datasource.impl.CouponRemoteDataSourceImpl
import woowacourse.shopping.data.service.NetworkModule
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.payment.adapter.CouponAdapter
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModel
import woowacourse.shopping.ui.payment.viewmodel.PaymentViewModelFactory

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: CouponAdapter
    private val viewmodel: PaymentViewModel by viewModels {
        PaymentViewModelFactory(
            CouponRepositoryImpl(CouponRemoteDataSourceImpl(NetworkModule.couponService)),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setCouponAdapter()
        observeCoupons()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment)
        binding.lifecycleOwner = this
    }

    private fun setCouponAdapter() {
        binding.rvCoupon.itemAnimator = null
        adapter = CouponAdapter()
        binding.rvCoupon.adapter = adapter
    }

    private fun observeCoupons() {
        viewmodel.coupons.observe(this) {
            adapter.submitList(it)
        }
    }

    companion object {
        fun startActivity(context: Context) =
            Intent(context, PaymentActivity::class.java).run {
                context.startActivity(this)
            }
    }
}
