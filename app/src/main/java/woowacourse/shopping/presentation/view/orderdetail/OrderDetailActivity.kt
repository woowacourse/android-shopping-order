package woowacourse.shopping.presentation.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.getSerializableCompat

class OrderDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderDetailBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    private val orderId: Long by lazy {
        intent.getLongExtra(KEY_ORDER_ID, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)

        url = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        token = intent.getSerializableCompat(KEY_SERVER_TOKEN) ?: return finish()
    }

    companion object {
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun createIntent(
            context: Context,
            orderId: Long,
            url: Server.Url,
            token: Server.Token
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(KEY_ORDER_ID, orderId)
            intent.putExtra(KEY_SERVER_SERVER, url)
            intent.putExtra(KEY_SERVER_TOKEN, token)
            return intent
        }
    }
}
