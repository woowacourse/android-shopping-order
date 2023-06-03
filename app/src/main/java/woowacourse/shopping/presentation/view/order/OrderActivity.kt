package woowacourse.shopping.presentation.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.getSerializableCompat

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    override lateinit var presenter: OrderContract.Presenter

    private val server by lazy { intent.getSerializableCompat<Server>(KEY_SERVER_SERVER) ?: finish() }
    private val cartIds by lazy { intent.getLongArrayExtra(KEY_CART_IDS)?.toList() ?: finish() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
    }

    companion object {
        private const val KEY_CART_IDS = "KEY_CART_IDS"

        internal fun createIntent(
            context: Context,
            cartIds: List<Long>,
            server: Server,
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(KEY_CART_IDS, cartIds.toLongArray())
            intent.putExtra(KEY_SERVER_SERVER, server)

            return intent
        }
    }
}
