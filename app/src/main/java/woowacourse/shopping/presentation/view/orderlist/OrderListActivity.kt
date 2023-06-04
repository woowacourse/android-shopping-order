package woowacourse.shopping.presentation.view.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.getSerializableCompat

class OrderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var server: Server

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        server = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
    }

    companion object {
        internal fun createIntent(
            context: Context,
            server: Server,
        ): Intent {
            val intent = Intent(context, OrderListActivity::class.java)
            intent.putExtra(KEY_SERVER_SERVER, server)
            return intent
        }
    }
}
