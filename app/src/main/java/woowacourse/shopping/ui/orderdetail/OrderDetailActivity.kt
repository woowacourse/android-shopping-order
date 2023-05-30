package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
    }

    companion object {
        fun startActivity(context: Context) {
            Intent(context, this::class.java).run {
                context.startActivity(this)
            }
        }
    }
}
