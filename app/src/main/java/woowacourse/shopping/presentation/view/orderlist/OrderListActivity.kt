package woowacourse.shopping.presentation.view.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.getSerializableCompat

class OrderListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderListBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)

        url = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        token = intent.getSerializableCompat(KEY_SERVER_TOKEN) ?: return finish()

        setToolbar()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return true
    }

    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.toolbar_title_order_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        fun createIntent(context: Context, url: Server.Url, token: Server.Token): Intent {
            val intent = Intent(context, OrderListActivity::class.java)
            intent.putExtra(KEY_SERVER_SERVER, url)
            intent.putExtra(KEY_SERVER_TOKEN, token)

            return intent
        }
    }
}
