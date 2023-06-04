package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderHistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, OrderHistoryActivity::class.java)
    }
}
