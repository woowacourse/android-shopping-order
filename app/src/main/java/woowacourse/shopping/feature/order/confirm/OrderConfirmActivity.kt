package woowacourse.shopping.feature.order.confirm

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderConfirmBinding

class OrderConfirmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderConfirmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_confirm)
    }
}
