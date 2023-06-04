package woowacourse.shopping.presentation.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityOrderDetailBinding

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding
    private val orderId = intent.getLongExtra(KEY_ORDER_ID, ERROR_ORDER_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (orderId == ERROR_ORDER_ID) return finish()
    }

    companion object {
        private const val KEY_ORDER_ID = "order_id"
        private const val ERROR_ORDER_ID = -1L

        internal fun createIntent(
            context: Context,
            orderId: Long,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(KEY_ORDER_ID, orderId)

            return intent
        }
    }
}
