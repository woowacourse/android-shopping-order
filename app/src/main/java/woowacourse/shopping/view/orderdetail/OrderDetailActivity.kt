package woowacourse.shopping.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            return intent
        }
    }
}
