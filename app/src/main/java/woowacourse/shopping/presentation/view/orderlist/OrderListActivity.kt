package woowacourse.shopping.presentation.view.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.view.orderdetail.OrderDetailActivity
import woowacourse.shopping.presentation.view.orderlist.adapter.OrderListAdapter
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.createRetrofit
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var server: Server

    override lateinit var presenter: OrderListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter()
        presenter.initOrders()
    }

    private fun setPresenter() {
        server = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        val retrofit = createRetrofit(server)
        val orderRepository = OrderRepositoryImpl(retrofit)
        presenter = OrderListPresenter(this, orderRepository)
    }

    override fun setOrderView(orders: List<OrderDetailModel>) {
        val orderListAdapter = OrderListAdapter { order ->
            val intent = OrderDetailActivity.createIntent(this, server, order.id)
            startActivity(intent)
        }
        orderListAdapter.setItems(orders)
        binding.rvOrderList.adapter = orderListAdapter
    }

    override fun handleErrorView() {
        binding.root.post {
            showToast(getString(R.string.toast_message_system_error))
        }
    }

    companion object {
        internal fun createIntent(
            context: Context,
            server: Server,
        ): Intent {
            val intent = Intent(context, OrderListActivity::class.java)
            intent.putExtra(KEY_SERVER_SERVER, server)
            return intent
        }
    }
}
