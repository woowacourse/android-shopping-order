package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.model.UiOrder
import woowacourse.shopping.ui.order.recyclerview.ListItem
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.Presenter
import woowacourse.shopping.ui.orderdetail.OrderDetailContract.View
import woowacourse.shopping.ui.orderdetail.recyclerview.adapter.OrderDetailRecyclerViewAdapter
import woowacourse.shopping.util.extension.getParcelableExtraCompat
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.inject.injectOrderDetailPresenter

class OrderDetailActivity : AppCompatActivity(), View {
    private val presenter: Presenter by lazy {
        injectOrderDetailPresenter(
            view = this,
            order = intent.getParcelableExtraCompat(ORDER_KEY)!!
        )
    }
    private lateinit var binding: ActivityOrderDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater).setContentView(this)
        binding.presenter = presenter
        binding.adapter = OrderDetailRecyclerViewAdapter()
        presenter.fetchOrderDetail()
    }

    override fun showOrderDetail(orders: List<ListItem>) {
        binding.adapter?.addAll(orders)
    }

    companion object {
        private const val ORDER_KEY = "order_key"

        fun getIntent(context: Context, order: UiOrder): Intent =
            Intent(context, OrderDetailActivity::class.java)
                .putExtra(ORDER_KEY, order)
    }
}
