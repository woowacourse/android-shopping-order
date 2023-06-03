package woowacourse.shopping.feature.orderHistory

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.R
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

        supportActionBar?.title = getString(R.string.order_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
