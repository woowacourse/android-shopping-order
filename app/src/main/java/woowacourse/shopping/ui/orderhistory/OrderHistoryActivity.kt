package woowacourse.shopping.ui.orderhistory

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderHistoryBinding
import woowacourse.shopping.ui.model.Order
import woowacourse.shopping.ui.orderfinish.OrderDetailActivity

class OrderHistoryActivity : AppCompatActivity(), OrderHistoryContract.View {

    private val binding: ActivityOrderHistoryBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_order_history)
    }
    private val presenter: OrderHistoryContract.Presenter by lazy {
        OrderHistoryPresenter(
            view = this,
            repository = OrderRepositoryImpl(OrderRemoteDataSourceImpl())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        presenter.getOrders()
    }

    override fun setUpView(orders: List<Order>) {
        Log.d("woogi", "setUpView: $orders")
        binding.rvOrders.adapter = OrderHistoryAdapter(
            orders = orders,
            onClicked = ::showOrderDetail
        )
    }

    private fun showOrderDetail(order: Order) {
        val intent: Intent = OrderDetailActivity.getIntent(
            context = this,
            order = order
        )

        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context): Intent {

            return Intent(context, OrderHistoryActivity::class.java)
        }
    }
}
