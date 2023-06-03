package woowacourse.shopping.feature.order.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)
    }
}
