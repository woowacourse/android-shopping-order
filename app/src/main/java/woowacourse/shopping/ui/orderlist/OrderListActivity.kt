package woowacourse.shopping.ui.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.dataSource.RemoteOrderDataSource
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.orderlist.orderListAdapter.OrderListAdapter
import woowacourse.shopping.uimodel.OrderHistoryUIModel

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private lateinit var binding: ActivityOrderListBinding
    private lateinit var presenter: OrderListContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initToolBar()
        initPresenter()
        initView()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)
    }

    private fun initToolBar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbar.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbar.navigationIcon = navigationIcon
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
        binding.rvOrderHistoryList.adapter =
            OrderListAdapter(orderHistories, presenter::showOrderDetail)
    }

    override fun navigateToOrderDetail(orderId: Int) {
        val intent = OrderDetailActivity.getIntent(this, orderId)
        startActivity(intent)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderListActivity::class.java)
        }
    }
}
