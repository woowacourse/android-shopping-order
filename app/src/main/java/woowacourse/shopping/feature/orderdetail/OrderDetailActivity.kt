package woowacourse.shopping.feature.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        supportActionBar?.title = getString(R.string.order_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val orderId: Long = intent.getLongExtra(ORDER_ID_KEY, 0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val ORDER_ID_KEY = "orderKey"
        fun getIntent(context: Context, orderId: Long): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(ORDER_ID_KEY, orderId)
            return intent
        }
    }
}
