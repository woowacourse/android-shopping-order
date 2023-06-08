package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.remote.RemoteOrderDataSource
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.support.framework.presentation.setThrottleFirstOnClickListener
import woowacourse.shopping.ui.model.UiOrder
import woowacourse.shopping.ui.orderdetail.OrderDetailDialog
import woowacourse.shopping.ui.orderdetail.OrderDetailDialogFragmentFactory

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var presenter: OrderHistoryContract.Presenter
    private lateinit var orderHistoryAdapter: OrderHistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_history)
        initOrderHistoryAdapter()
        initPresenter()
        initCloseButton()
    }

    private fun initOrderHistoryAdapter() {
        orderHistoryAdapter = OrderHistoryAdapter {
            supportFragmentManager.fragmentFactory = OrderDetailDialogFragmentFactory(it)
            val fragment: OrderDetailDialog = supportFragmentManager.fragmentFactory
                .instantiate(classLoader, OrderDetailDialog::class.java.name) as OrderDetailDialog
            fragment.show(supportFragmentManager, OrderDetailDialog::class.java.name)
        }
        binding.rvOrderHistory.adapter = orderHistoryAdapter
    }

    private fun initPresenter() {
        presenter = OrderHistoryPresenter(this, OrderRepositoryImpl(RemoteOrderDataSource()))
    }

    private fun initCloseButton() {
        binding.ivOrderHistoryClose.setThrottleFirstOnClickListener { finish() }
    }

    override fun updateOrdersInfo(ordersInfo: List<UiOrder>) {
        orderHistoryAdapter.submitList(ordersInfo)
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, OrderHistoryActivity::class.java)
    }
}
