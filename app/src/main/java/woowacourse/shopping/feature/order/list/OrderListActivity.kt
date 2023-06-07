package woowacourse.shopping.feature.order.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.preferences.UserPreference
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.feature.order.detail.OrderDetailActivity
import woowacourse.shopping.model.OrderMinInfoItemUiModel
import woowacourse.shopping.module.ApiModule
import woowacourse.shopping.util.showToastNetworkError
import woowacourse.shopping.util.showToastShort

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var presenter: OrderListContract.Presenter

    private val orderListAdapter: OrderListAdapter by lazy {
        OrderListAdapter(
            object : OrderItemClickListener {
                override fun onClick(orderId: Long) {
                    presenter.requestOrderDetail(orderId)
                }
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)
        initPresenter()
        initAdapter()
        supportActionBar?.title = getString(R.string.order_list_bar_title)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        presenter.loadOrderItems()
    }

    private fun initPresenter() {
        presenter = OrderListPresenter(
            this,
            OrderRepositoryImpl(ApiModule.getInstance(UserPreference).createOrderService())
        )
    }

    private fun initAdapter() {
        binding.orderRecyclerView.adapter = orderListAdapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showFailedLoadOrderList() {
        showToastShort(R.string.failed_load_order_list_info)
    }

    override fun setOrderListItems(orderItems: List<OrderMinInfoItemUiModel>) {
        orderListAdapter.setOrderItems(orderItems)
    }

    override fun showOrderDetail(orderId: Long) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
    }

    override fun showNetworkError() {
        showToastNetworkError()
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderListActivity::class.java)
        }
    }
}
