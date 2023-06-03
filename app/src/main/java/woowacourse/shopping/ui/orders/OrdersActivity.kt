package woowacourse.shopping.ui.orders

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.remoteDataSourceImpl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repositoryImpl.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrdersBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.detailedProduct.DetailedProductActivity
import woowacourse.shopping.ui.orderDetail.OrderDetailActivity
import woowacourse.shopping.ui.orders.ordersAdapter.OrdersAdapter
import woowacourse.shopping.ui.orders.ordersAdapter.OrdersListener
import woowacourse.shopping.ui.serverSetting.ServerSettingActivity
import woowacourse.shopping.utils.RetrofitUtil

class OrdersActivity : AppCompatActivity(), OrdersContract.View {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var presenter: OrdersContract.Presenter

    private val adapter = OrdersAdapter(
        listener = object : OrdersListener {
            override fun onItemClick(productId: Int) {
                presenter.navigateToProductDetail(productId)
            }

            override fun onOrderDetailClick(orderId: Long) {
                presenter.navigateToOrderDetail(orderId)
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
        binding = ActivityOrdersBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initPresenter() {
        RetrofitUtil.url = ServerSettingActivity.SERVER_IO
        presenter = OrdersPresenter(
            this,
            OrderRepositoryImpl(OrderRemoteDataSourceImpl())
        )
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.recyclerView.adapter = adapter
        presenter.getOrderHistoryList()
    }

    override fun showOrderHistories(orders: List<OrderUIModel>) {
        adapter.submitList(orders)
    }

    override fun navigateToOrderDetail(orderId: Long) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
    }

    override fun navigateToProductDetail(productId: Int) {
        startActivity(DetailedProductActivity.getIntent(this, productId))
    }
}
