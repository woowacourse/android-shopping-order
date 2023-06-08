package woowacourse.shopping.ui.orderHistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.remote.OrderRetrofitDataSource
import woowacourse.shopping.data.repository.OrderDefaultRepository
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.model.OrderHistoryUIModel
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orderHistories.historyAdapter.HistoryItemAdapter

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var presenter: OrderHistoryContract.Presenter

    private val adapter = HistoryItemAdapter(
        onItemClick = { productId -> presenter.processToProductDetail(productId) }
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initToolbar()
        initView()
    }

    private fun initBinding() {
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initPresenter() {
        presenter = OrderHistoryPresenter(
            this,
            OrderDefaultRepository(
                OrderRetrofitDataSource()
            ),
            intent.getLongExtra(KEY_ORDER_ID, -1)
        )
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.rvOrderProduct.adapter = adapter
        presenter.fetchOrderHistory()
    }

    override fun setOrderHistory(orderHistory: OrderHistoryUIModel) {
        runOnUiThread {
            binding.orderHistory = modifyFormat(orderHistory)
            adapter.submitList(orderHistory.orderItems)
        }
    }

    private fun modifyFormat(order: OrderHistoryUIModel): OrderHistoryUIModel {
        val orderDate = order.orderDate
        val newOrderDate = orderDate.substring(0, 10).replace("-", ".")
        return order.copy(orderDate = newOrderDate)
    }

    override fun navigateToProductDetail(productId: Int) {
        startActivity(DetailedProductActivity.getIntent(this, productId))
    }

    companion object {
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun getIntent(context: Context, orderId: Long): Intent =
            Intent(context, OrderHistoryActivity::class.java).apply {
                putExtra(KEY_ORDER_ID, orderId)
            }
    }
}
