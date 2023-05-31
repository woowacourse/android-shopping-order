package woowacourse.shopping.feature.order.result

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderResultBinding

class OrderResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_result)
    }
}
