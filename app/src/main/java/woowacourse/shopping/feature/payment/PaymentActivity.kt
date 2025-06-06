package woowacourse.shopping.feature.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
    }

    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, PaymentActivity::class.java).apply {
//                putExtra(GOODS_KEY, id)
            }
    }
}
