package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)
    }

    companion object {
        private const val EXTRA_KEY_IDS = "ids"

        fun createIntent(context: Context, ids: List<Int>): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(EXTRA_KEY_IDS, ids.toTypedArray())
            return intent
        }
    }
}