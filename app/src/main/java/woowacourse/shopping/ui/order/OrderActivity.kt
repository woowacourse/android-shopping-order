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
import woowacourse.shopping.model.OrderUIModel
import woowacourse.shopping.ui.order.orderProductAdapter.OrderProductAdapter
import woowacourse.shopping.ui.orderHistory.OrderHistoryActivity

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var binding: ActivityOrderBinding

    private val adapter = OrderProductAdapter()

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPresenter()
        initBinding()
        initToolbar()
        initView()
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
        setContentView(binding.root)
    }

    private fun initToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun initView() {
        binding.rvOrders.adapter = adapter
        binding.confirmOrder = { presenter.confirmOrder(0) }
        presenter.getOrderList()
    }

    override fun showOrderList(orderUIModel: OrderUIModel) {
        adapter.submitList(orderUIModel.cartProducts)
    }

    override fun navigateOrder(orderId: Long) {
        startActivity(OrderHistoryActivity.getIntent(this, orderId))
    }

    companion object {
        private const val KEY_CART_IDS = "KEY_CART_IDS"
        fun getIntent(context: Context, cartIds: List<Int>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putIntegerArrayListExtra(KEY_CART_IDS, cartIds as ArrayList<Int>)
            }
        }
    }
}
