package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import woowacourse.shopping.R

class OrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
    }

    companion object {
        private const val EXTRA_KEY_ID = "id"

        fun createIntent(context: Context, id: Int) : Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(EXTRA_KEY_ID, id)
            return intent
        }
    }
}