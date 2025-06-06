package woowacourse.shopping.ui.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.ui.common.DataBindingActivity

class PaymentActivity : DataBindingActivity<ActivityPaymentBinding>(R.layout.activity_payment) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initSupportActionBar()
    }

    private fun initSupportActionBar() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.payment_title)
    }

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, PaymentActivity::class.java).apply {
            }
    }
}
