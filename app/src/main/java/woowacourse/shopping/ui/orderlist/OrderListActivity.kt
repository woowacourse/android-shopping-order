package woowacourse.shopping.ui.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.dataSource.RemoteOrderDataSource
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.ui.orderlist.orderListAdapter.OrderListAdapter
import woowacourse.shopping.uimodel.OrderHistoryUIModel

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var presenter: OrderListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initView()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)
    }

    private fun initPresenter() {
        presenter = OrderListPresenter(this, OrderRepositoryImpl(RemoteOrderDataSource()))
    }

    private fun initView() {
        binding.rvOrderHistoryList.addItemDecoration(
            DividerItemDecoration(this, LinearLayoutManager.VERTICAL),
        )
        presenter.getOrderList()
    }

    override fun setOrderList(orderHistories: List<OrderHistoryUIModel>) {
        binding.rvOrderHistoryList.adapter = OrderListAdapter(orderHistories)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderListActivity::class.java)
        }
    }
}
