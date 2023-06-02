package woowacourse.shopping.ui.orders

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.remoteDataSourceImpl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repositoryImpl.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrdersBinding
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.orders.ordersAdapter.OrdersAdapter
import woowacourse.shopping.ui.serverSetting.ServerSettingActivity
import woowacourse.shopping.utils.RetrofitUtil

class OrdersActivity : AppCompatActivity(), OrdersContract.View {
    private lateinit var binding: ActivityOrdersBinding
    private lateinit var presenter: OrdersContract.Presenter

    private val adapter = OrdersAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initBinding()
        initPresenter()
        binding.recyclerView.adapter = adapter
        presenter.getOrderHistoryList()
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

    override fun showOrderHistories(orders: List<OrderUIModel>) {
        adapter.submitList(orders)
    }
}
