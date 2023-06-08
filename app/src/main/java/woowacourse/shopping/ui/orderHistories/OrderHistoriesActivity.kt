package woowacourse.shopping.ui.orderHistories

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.data.remote.OrderRetrofitDataSource
import woowacourse.shopping.data.repository.OrderDefaultRepository
import woowacourse.shopping.databinding.ActivityOrderHistoriesBinding
import woowacourse.shopping.model.OrderHistoryUIModel
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orderHistories.historiesAdapter.HistoriesAdapter
import woowacourse.shopping.ui.orderHistories.historiesAdapter.HistoriesListener
import woowacourse.shopping.ui.orderHistory.OrderHistoryActivity
import woowacourse.shopping.utils.PhDividerItemDecoration

class OrderHistoriesActivity : AppCompatActivity(), OrderHistoriesContract.View {
    private lateinit var binding: ActivityOrderHistoriesBinding
    private lateinit var presenter: OrderHistoriesContract.Presenter

    private val adapter = HistoriesAdapter(
        listener = object : HistoriesListener {
            override fun onItemClick(productId: Int) {
                presenter.processToProductDetail(productId)
            }

            override fun onOrderDetailClick(orderId: Long) {
                presenter.processToOrderHistory(orderId)
            }
        }
    )

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
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
        binding = ActivityOrderHistoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initPresenter() {
        presenter = OrderHistoriesPresenter(
            this,
            OrderDefaultRepository(OrderRetrofitDataSource())
        )
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.rvOrderHistories.adapter = adapter
        binding.rvOrderHistories.addItemDecoration(PhDividerItemDecoration(20f, 0xffebebeb.toInt()))

        binding.rvOrderHistories.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                if (visibleItemCount + firstVisibleItemPosition >= totalItemCount &&
                    firstVisibleItemPosition >= 0
                ) {
                    loadMoreData()
                }
            }
        })
        presenter.fetchOrderHistories()
    }

    private fun loadMoreData() {
        presenter.fetchOrderHistories()
    }

    override fun setOrderHistories(orderHistories: List<OrderHistoryUIModel>) {
        runOnUiThread {
            adapter.submitList(orderHistories)
        }
    }

    override fun navigateToOrderHistory(orderId: Long) {
        startActivity(OrderHistoryActivity.getIntent(this, orderId))
    }

    override fun navigateToProductDetail(productId: Int) {
        startActivity(DetailedProductActivity.getIntent(this, productId))
    }

    companion object {
        fun getIntent(context: Context): android.content.Intent {
            return android.content.Intent(context, OrderHistoriesActivity::class.java)
        }
    }
}