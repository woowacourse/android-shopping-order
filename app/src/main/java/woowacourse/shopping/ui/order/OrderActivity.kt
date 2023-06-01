package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.remoteDataSourceImpl.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repositoryImpl.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.OrderListUIModel
import woowacourse.shopping.ui.order.orderProductAdapter.OrderProductAdapter

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var binding: ActivityOrderBinding

    private val adapter = OrderProductAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        initPresenter()
        initBinding()
        binding.rvOrders.adapter = adapter
        initToolbar()
        presenter.getOrderList()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    private fun initPresenter() {
        presenter = OrderPresenter(
            view = this,
            intent.getIntegerArrayListExtra(KEY_CART_IDS) ?: listOf(),
            orderRepository = OrderRepositoryImpl(
                orderRemoteDataSource = OrderRemoteDataSourceImpl()
            )
        )
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        binding.price = 1000
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        private const val KEY_CART_IDS = "KEY_CART_IDS"
        fun getIntent(context: Context, cartIds: List<Int>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putIntegerArrayListExtra(KEY_CART_IDS, cartIds as ArrayList<Int>)
            }
        }
    }

    override fun showOrderList(orderListUIModel: OrderListUIModel) {
        adapter.submitList(orderListUIModel.cartProducts)
    }
}
