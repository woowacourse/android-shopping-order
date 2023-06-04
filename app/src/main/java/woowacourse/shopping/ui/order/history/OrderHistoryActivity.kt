package woowacourse.shopping.ui.order.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.order.history.OrderHistoryContract.Presenter
import woowacourse.shopping.ui.order.history.OrderHistoryContract.View
import woowacourse.shopping.ui.order.main.recyclerview.adapter.OrderAdapter
import woowacourse.shopping.util.inject.injectOrderHistoryPresenter

class OrderHistoryActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var adapter: OrderAdapter
    private val presenter: Presenter by lazy {
        injectOrderHistoryPresenter(view = this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
    }
}
