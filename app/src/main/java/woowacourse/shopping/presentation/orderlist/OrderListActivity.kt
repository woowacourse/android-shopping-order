package woowacourse.shopping.presentation.orderlist

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.order.OrderRemoteDataSource
import woowacourse.shopping.data.order.OrderRepositoryDefault
import woowacourse.shopping.data.shoppingpref.ShoppingOrderSharedPreference
import woowacourse.shopping.databinding.ActivityOrderListBinding
import woowacourse.shopping.presentation.model.OrderModel

class OrderListActivity : AppCompatActivity(), OrderListContract.View {

    private lateinit var binding: ActivityOrderListBinding

    private val presenter: OrderListContract.Presenter by lazy {
        val sharedPref = ShoppingOrderSharedPreference(applicationContext)
        val orderRepository =
            OrderRepositoryDefault(OrderRemoteDataSource(sharedPref.baseUrl, sharedPref.userInfo))
        OrderListPresenter(this, orderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar()
        presenter.loadOrderList()
    }

    override fun showOrderList(orderModels: List<OrderModel>) {
        binding.recyclerOrderList.adapter = OrderListAdapter(orderModels)
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarOrderList.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, OrderListActivity::class.java)
        }
    }
}
