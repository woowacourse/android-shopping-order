package woowacourse.shopping.presentation.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.order.OrderRemoteDataSource
import woowacourse.shopping.data.order.OrderRepositoryDefault
import woowacourse.shopping.data.util.RetrofitUtil
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.presentation.model.OrderModel

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private lateinit var binding: ActivityOrderDetailBinding

    private val presenter: OrderDetailContract.Presenter by lazy {
        val retrofit = RetrofitUtil.getInstance().retrofit
        val orderRepository = OrderRepositoryDefault(OrderRemoteDataSource(retrofit))
        OrderDetailPresenter(this, orderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBar()
        loadOrderInfo()
    }

    private fun loadOrderInfo() {
        val orderId = intent.getLongExtra(ORDER_ID, -1)
        presenter.loadOrderInfo(orderId)
    }

    override fun showOrderInfo(orderModel: OrderModel) {
        binding.recyclerOrderDetail.adapter = OrderDetailAdapter(orderModel.orderProductModels)
        binding.orderModel = orderModel
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarOrderDetail.toolbarOrderDetail)
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
        private const val ORDER_ID = "ORDER_ID"

        fun getIntent(context: Context, orderId: Long): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(ORDER_ID, orderId)
            }
        }
    }
}
