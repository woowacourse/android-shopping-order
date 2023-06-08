package woowacourse.shopping.feature.order.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.remote.OrderRepositoryImpl
import woowacourse.shopping.data.service.order.OrderRemoteService
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.feature.order.detail.OrderDetailActivity
import woowacourse.shopping.model.OrderPreviewUiModel

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var presenter: OrderListContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)

        supportActionBar?.title = getString(R.string.order_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter = OrderListPresenter(this, OrderRepositoryImpl(OrderRemoteService()))
        presenter.requestOrders()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showOrders(orderPreviews: List<OrderPreviewUiModel>) {
        binding.recyclerviewOrder.adapter =
            OrderListAdapter(orderPreviews.reversed(), presenter::requestOrderDetail)
    }

    override fun showOrderDetail(orderId: Long) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
    }

    override fun failToLoadOrders(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderListActivity::class.java)
        }
    }
}
