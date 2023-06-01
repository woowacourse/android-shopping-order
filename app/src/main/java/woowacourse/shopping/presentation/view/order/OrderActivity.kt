package woowacourse.shopping.presentation.view.order

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.getSerializableCompat

class OrderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        val cartIds = intent.getSerializableCompat<ArrayList<Long>>(KEY_CART_IDS) ?: return finish()
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
        supportActionBar?.title = getString(R.string.toolbar_title_order)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        private const val KEY_CART_IDS = "KEY_CART_IDS"

        fun createIntent(
            context: Context,
            cartIds: ArrayList<Long>,
            url: Server.Url,
            token: Server.Token
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(KEY_CART_IDS, cartIds)
            intent.putExtra(KEY_SERVER_SERVER, url)
            intent.putExtra(KEY_SERVER_TOKEN, token)
            return intent
        }
    }
}
