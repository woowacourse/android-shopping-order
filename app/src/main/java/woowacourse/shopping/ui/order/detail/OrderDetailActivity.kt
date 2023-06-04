package woowacourse.shopping.ui.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
    }

    companion object {
        private const val ORDER_DETAIL_KEY = "order_detail_key"

        fun getIntent(context: Context, orderId: Int): Intent =
            Intent(context, OrderDetailActivity::class.java)
                .putExtra(ORDER_DETAIL_KEY, orderId)
    }
}
