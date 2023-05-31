package woowacourse.shopping.presentation.myorder

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.order.OrderServiceHelper
import woowacourse.shopping.databinding.ActivityMyOrderBinding
import woowacourse.shopping.presentation.model.OrderModel

class MyOrderActivity : AppCompatActivity(), MyOrderContract.View {
    private lateinit var binding: ActivityMyOrderBinding
    private lateinit var myOrdersAdapter: MyOrdersAdapter
    private val presenter: MyOrderContract.Presenter by lazy {
        MyOrderPresenter(
            view = this,
            orderRepository = OrderRepositoryImpl(OrderServiceHelper(PreferenceUtil(this)))
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        setUpToolBar()
        initMyOrderItems()
    }

    private fun setUpBinding() {
        binding = ActivityMyOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initMyOrderItems() {
        myOrdersAdapter = MyOrdersAdapter()
        binding.recyclerMyOrder.adapter = myOrdersAdapter
        presenter.loadOrders()
    }

    private fun setUpToolBar() {
        setSupportActionBar(binding.toolbarMyOrder.toolbar)
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

    override fun setOrders(orderModels: List<OrderModel>) {
        myOrdersAdapter.submitList(orderModels)
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, MyOrderActivity::class.java)
        }
    }
}
