package woowacourse.shopping.feature.order

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.data.repository.order.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.common.load.LoadAdapter
import woowacourse.shopping.feature.orderDetail.OrderDetailActivity
import woowacourse.shopping.model.OrderUiModel

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var loadAdapter: LoadAdapter

    private val concatAdapter: ConcatAdapter by lazy {
        val config = ConcatAdapter.Config.Builder().apply {
            setIsolateViewTypes(false)
        }.build()
        ConcatAdapter(config, orderAdapter, loadAdapter)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initAdapter()
        initPresenter()

        presenter.loadOrders()
    }

    private fun initAdapter() {
        orderAdapter = OrderAdapter { showOrderDetailScreen(it) }
        loadAdapter = LoadAdapter { presenter.loadOrders() }

        binding.recyclerview.adapter = concatAdapter
    }

    private fun initPresenter() {
        presenter = OrderPresenter(this, OrderRepositoryImpl())
    }

    override fun showOrders(orders: List<OrderUiModel>) {
        orderAdapter.submitList(orders)
    }

    private fun showOrderDetailScreen(orderId: Int) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
    }
}
