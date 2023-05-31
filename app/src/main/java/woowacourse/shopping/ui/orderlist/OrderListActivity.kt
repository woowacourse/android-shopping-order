package woowacourse.shopping.ui.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, OrderListActivity::class.java)
    }
}
