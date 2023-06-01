package woowacourse.shopping.feature.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderDetailBinding

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
        val orderId: Long = intent.getLongExtra(ORDER_ID_KEY, -1L)
    }

    companion object {
        private val ORDER_ID_KEY = "order_id_key"
        fun getIntent(context: Context, orderId: Long): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(ORDER_ID_KEY, orderId)
            }
        }
    }
}
