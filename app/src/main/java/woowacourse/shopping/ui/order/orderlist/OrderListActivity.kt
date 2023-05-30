package woowacourse.shopping.ui.order.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.order.DefaultOrderRepository
import woowacourse.shopping.data.order.OrderRemoteSource
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.domain.user.User
import woowacourse.shopping.ui.order.adapter.OrderListAdapter
import woowacourse.shopping.ui.order.orderdetail.OrderDetailActivity
import woowacourse.shopping.ui.order.uistate.OrderUIState
import woowacourse.shopping.utils.ServerConfiguration

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private val binding: ActivityOrderListBinding by lazy {
        ActivityOrderListBinding.inflate(layoutInflater)
    }

    private val presenter: OrderListContract.Presenter by lazy {
        OrderListPresenter(
            this, DefaultOrderRepository(OrderRemoteSource(ServerConfiguration.host))
        )
    }

    private val orderListAdapter: OrderListAdapter by lazy {
        OrderListAdapter(mutableListOf(), presenter::openOrderDetail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initRecycler()
        initOrders()
        initUsers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun showUserList(users: List<User>) {
        binding.orderListUserSpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, users.map { it.name })
        binding.orderListUserSpinner.setSelection(0, false)
        binding.orderListUserSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                presenter.loadOrders()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    override fun showOrders(orders: List<OrderUIState>) {
        orderListAdapter.setOrders(orders)
    }

    override fun showOrderDetail(orderId: Long) {
        OrderDetailActivity.startActivity(this, orderId)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.orderListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRecycler() {
        binding.orderListRecycler.adapter = orderListAdapter
    }

    private fun initOrders() {
        presenter.loadOrders()
    }

    private fun initUsers() {
        presenter.loadUsers()
    }

    companion object {
        fun startActivity(context: Context) {
            Intent(context, OrderListActivity::class.java).run {
                context.startActivity(this)
            }
        }
    }
}
