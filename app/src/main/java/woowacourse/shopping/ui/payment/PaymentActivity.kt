package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.payment.adapter.CouponAdapter

class PaymentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaymentBinding
    private lateinit var adapter: CouponAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setCouponAdapter()
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

    companion object {
        fun startActivity(context: Context) =
            Intent(context, PaymentActivity::class.java).run {
                context.startActivity(this)
            }
    }
}
