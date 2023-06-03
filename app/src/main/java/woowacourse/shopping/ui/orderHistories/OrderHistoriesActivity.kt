package woowacourse.shopping.ui.orderHistories

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.remoteDataSourceImpl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repositoryImpl.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoriesBinding
import woowacourse.shopping.model.OrderHistoryUIModel
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orderHistories.historiesAdapter.HistoriesAdapter
import woowacourse.shopping.ui.orderHistories.historiesAdapter.HistoriesListener
import woowacourse.shopping.ui.orderHistory.OrderHistoryActivity
import woowacourse.shopping.ui.serverSetting.ServerSettingActivity
import woowacourse.shopping.utils.PhDividerItemDecoration
import woowacourse.shopping.utils.RetrofitUtil

class OrderHistoriesActivity : AppCompatActivity(), OrderHistoriesContract.View {
    private lateinit var binding: ActivityOrderHistoriesBinding
    private lateinit var presenter: OrderHistoriesContract.Presenter

    private val adapter = HistoriesAdapter(
        listener = object : HistoriesListener {
            override fun onItemClick(productId: Int) {
                presenter.navigateToProductDetail(productId)
            }

            override fun onOrderDetailClick(orderId: Long) {
                presenter.navigateToOrderHistory(orderId)
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
        RetrofitUtil.url = ServerSettingActivity.SERVER_IO
        presenter = OrderHistoriesPresenter(
            this,
            OrderRepositoryImpl(OrderRemoteDataSourceImpl())
        )
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.rvOrderHistories.adapter = adapter
        binding.rvOrderHistories.addItemDecoration(PhDividerItemDecoration(20f, 0xffebebeb.toInt()))

        presenter.getOrderHistories()
    }

    override fun showOrderHistories(orderHistories: List<OrderHistoryUIModel>) {
        adapter.submitList(orderHistories)
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
