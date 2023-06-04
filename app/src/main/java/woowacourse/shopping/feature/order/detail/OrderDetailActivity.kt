package woowacourse.shopping.feature.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ServerType
import woowacourse.shopping.feature.order.OrderActivity

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
    }

    companion object {
        private const val ORDER_ID_KEY = "order_id_key"

        fun startActivity(context: Context, orderId: Long, serverUrl: String) {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(ORDER_ID_KEY, orderId)
            intent.putExtra(ServerType.INTENT_KEY, serverUrl)
            context.startActivity(intent)
        }
    }
}
