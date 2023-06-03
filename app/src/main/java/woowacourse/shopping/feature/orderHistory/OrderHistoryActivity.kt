package woowacourse.shopping.feature.orderHistory

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.feature.main.load.LoadAdapter
import woowacourse.shopping.model.OrderHistoryProductUiModel

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    private val binding by lazy { ActivityOrderHistoryBinding.inflate(layoutInflater) }
    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var loadAdapter: LoadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        presenter = OrderHistoryPresenter(this)

        initAdapter()
        presenter.loadOrderHistory()
    }

    private fun initAdapter() {
        orderHistoryAdapter = OrderHistoryAdapter(listOf())
        loadAdapter = LoadAdapter { presenter.loadProducts() }
        binding.recyclerviewOrderHistory.adapter =
            ConcatAdapter(orderHistoryAdapter, loadAdapter)
    }

    override fun addOrderHistory(orderHistory: List<OrderHistoryProductUiModel>) {
        orderHistoryAdapter.addItems(orderHistory)
    }
}
