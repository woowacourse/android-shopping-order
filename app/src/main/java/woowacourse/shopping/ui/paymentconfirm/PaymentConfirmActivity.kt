package woowacourse.shopping.ui.paymentconfirm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentConfirmBinding

class PaymentConfirmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaymentConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_confirm)
    }
}
