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
import woowacourse.shopping.ui.model.UiOrder

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    private lateinit var binding: ActivityOrderHistoryBinding
    private lateinit var presenter: OrderHistoryContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_history)
    }

    private fun initPresenter() {
        presenter = OrderHistoryPresenter(this, OrderRepositoryImpl(RemoteOrderDataSource()))
    }

    override fun updateOrdersInfo(ordersInfo: List<UiOrder>) {
        TODO("Not yet implemented")
    }

    companion object {
        fun getIntent(context: Context): Intent =
            Intent(context, OrderHistoryActivity::class.java)
    }
}
