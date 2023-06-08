package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ordercomplete.OrderCompleteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistoryDataSourceImpl
import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.orderhistory.adapter.OrderHistoryAdapter
import woowacourse.shopping.ui.orderhistory.presenter.OrderHistoryContract
import woowacourse.shopping.ui.orderhistory.presenter.OrderHistoryPresenter
import woowacourse.shopping.ui.orderhistory.uimodel.OrderHistory

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {
    private lateinit var binding: ActivityOrderHistoryBinding
    private val presenter: OrderHistoryContract.Presenter by lazy { initPresenter() }
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    private fun initPresenter() =
        OrderHistoryPresenter(
            this,
            OrderRepositoryImpl(
                OrderDataSourceImpl(
                    ServiceFactory.orderService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
                OrderCompleteDataSourceImpl(
                    ServiceFactory.orderCompleteService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
                OrderHistoryDataSourceImpl(
                    ServiceFactory.orderHistoryService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
            ),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.fetchOrderHistory()
    }

    override fun setOrderHistory(history: List<OrderHistory>) {
        initAdapter(history)
    }

    private fun initAdapter(history: List<OrderHistory>) {
        orderHistoryAdapter = OrderHistoryAdapter(history)
        binding.rvOrderHistoryList.adapter = orderHistoryAdapter
    }

    companion object {
        fun from(context: Context): Intent = Intent(context, OrderHistoryActivity::class.java)
    }
}
