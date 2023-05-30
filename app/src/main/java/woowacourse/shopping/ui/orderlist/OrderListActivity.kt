package woowacourse.shopping.ui.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.ui.orderlist.adapter.OrderListAdapter
import woowacourse.shopping.ui.orderlist.uistate.OrderUIState

class OrderListActivity : AppCompatActivity(), OrderListContract.View {
    private val binding: ActivityOrderListBinding by lazy {
        ActivityOrderListBinding.inflate(layoutInflater)
    }

    private val presenter: OrderListContract.Presenter by lazy {
        OrderListPresenter(this)
    }

    private val orderListAdapter: OrderListAdapter by lazy {
        OrderListAdapter(mutableListOf())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initToolbar()
        initRecycler()
        initPresenter()
    }

    override fun showOrders(orders: List<OrderUIState>) {
        orderListAdapter.setOrders(orders)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.orderListToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initRecycler() {
        binding.orderListRecycler.adapter = orderListAdapter
    }

    private fun initPresenter() {
        presenter.loadOrders()
    }

    companion object {
        fun startActivity(context: Context) {
            Intent(context, this::class.java).run {
                context.startActivity(this)
            }
        }
    }
}
