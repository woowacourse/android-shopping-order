package woowacourse.shopping.feature.order.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.feature.order.detail.OrderDetailActivity
import woowacourse.shopping.model.OrderMinInfoItemUiModel
import woowacourse.shopping.module.ApiModule

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var presenter: OrderListContract.Presenter

    private val adapter: OrderListAdapter by lazy {
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
    }

    private fun initPresenter() {
        presenter = OrderListPresenter(this, OrderRepositoryImpl(ApiModule.createOrderService()))
    }

    private fun initAdapter() {
        binding
    }

    override fun setOrderListItems(orderItems: List<OrderMinInfoItemUiModel>) {
        adapter.setOrderItems(orderItems)
    }

    override fun showOrderDetail(orderId: Long) {
        startActivity(OrderDetailActivity.getIntent(this, orderId))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderListActivity::class.java)
        }
    }
}
