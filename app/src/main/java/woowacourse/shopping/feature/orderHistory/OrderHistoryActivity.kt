package woowacourse.shopping.feature.orderHistory

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.R
import woowacourse.shopping.data.OrderRemoteRepositoryImpl
import woowacourse.shopping.data.TokenSharedPreference
import woowacourse.shopping.data.service.OrderRemoteService
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.feature.main.load.LoadAdapter
import woowacourse.shopping.feature.orderDetail.OrderDetailActivity
import woowacourse.shopping.model.OrderHistoryProductUiModel

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    private val binding by lazy { ActivityOrderHistoryBinding.inflate(layoutInflater) }
    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter
    private lateinit var loadAdapter: LoadAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initPresenter()
        supportActionBar?.title = getString(R.string.order_history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initAdapter()
        presenter.loadOrderHistory()
    }

    private fun initPresenter() {
        val token = TokenSharedPreference.getInstance(this).getToken("") ?: ""
        presenter =
            OrderHistoryPresenter(this, OrderRemoteRepositoryImpl(OrderRemoteService(token)))
    }

    private fun initAdapter() {
        orderHistoryAdapter = OrderHistoryAdapter(
            products = listOf(),
            clickListener = ::navigateToDetail
        )
        loadAdapter = LoadAdapter { presenter.loadOrderHistory() }
        binding.recyclerviewOrderHistory.adapter =
            ConcatAdapter(orderHistoryAdapter, loadAdapter)
    }

    private fun navigateToDetail(id: Int) {
        startActivity(OrderDetailActivity.getIntent(this, id))
    }

    override fun addOrderHistory(orderHistory: List<OrderHistoryProductUiModel>) {
        orderHistoryAdapter.addItems(orderHistory)
    }

    override fun showErrorMessage(t: Throwable) {
        Toast.makeText(this, "${t.message}", Toast.LENGTH_SHORT).show()
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
