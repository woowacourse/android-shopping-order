package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
        supportActionBar?.title = getString(R.string.order)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cartIds: List<Long> =
            intent.getLongArrayExtra(PRODUCTS_ID_KEY)?.toList() ?: listOf()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
//                presenter.exit()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val PRODUCTS_ID_KEY = "productsId"
        fun getIntent(context: Context, cartId: List<Long>): Intent {
            return Intent(context, OrderActivity::class.java)
                .putExtra(PRODUCTS_ID_KEY, cartId.toLongArray())
        }
    }
}
