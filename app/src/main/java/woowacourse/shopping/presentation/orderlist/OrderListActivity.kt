package woowacourse.shopping.presentation.orderlist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.remote.order.OrderRemoteDataSource
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.presentation.model.OrderModel
import woowacourse.shopping.presentation.orderdetail.OrderDetailActivity
import woowacourse.shopping.presentation.orderlist.adapter.OrderListAdapter

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private val binding: ActivityOrderListBinding by lazy {
        ActivityOrderListBinding.inflate(layoutInflater)
    }
    private val presenter by lazy {
        OrderListPresenter(
            this,
            OrderRepositoryImpl(OrderRemoteDataSource(PreferenceUtil(this)))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        presenter.loadOrderList()
    }

    override fun showOrderList(orders: List<OrderModel>) {
        binding.rvOrderList.adapter = OrderListAdapter(orders) {
            startActivity(OrderDetailActivity.getIntent(this, it))
        }
    }
}
