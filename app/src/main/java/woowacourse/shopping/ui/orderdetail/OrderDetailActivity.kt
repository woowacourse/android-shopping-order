package woowacourse.shopping.ui.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.ui.model.OrderUiModel
import woowacourse.shopping.ui.shopping.ShoppingActivity
import woowacourse.shopping.util.getSerializableCompat

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private val presenter: OrderDetailContract.Presenter by lazy {
        OrderDetailPresenter(
            view = this,
            orderRepository = OrderRepositoryImpl(
                orderRemoteDataSource = OrderRemoteDataSourceImpl()
            ),
            orderId = intent.getIntExtra(ORDER_ID_KEY, -1),
            order = intent.getSerializableCompat(ORDER_KEY)
        )
    }
    override val navigator: OrderDetailNavigator by lazy {
        OrderDetailNavigatorImpl(this)
    }
    private val binding: ActivityOrderDetailBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_order_detail)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_detail)

        initToolbar()
        presenter.getOrder()
    }

    private fun initToolbar() {
        binding.tbOrder.setNavigationOnClickListener {
            presenter.handleNavigator()
        }
    }

    override fun initView(order: OrderUiModel) {
        binding.rvOrderProducts.adapter = OrderDetailRecyclerAdapter(order)
    }

    override fun showErrorMessage(errorMessage: String) {
        val intent = ShoppingActivity.getIntent(this)

        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        startActivity(intent)
    }

    companion object {
        private const val ORDER_KEY = "order"
        private const val ORDER_ID_KEY = "order_id"

        fun getIntent(
            context: Context,
            order: OrderUiModel? = null,
            orderId: Int? = null,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
                .apply {
                    order?.let {
                        putExtra(ORDER_KEY, it)
                    }
                    orderId?.let {
                        putExtra(ORDER_ID_KEY, it)
                    }
                }

            return intent
        }
    }
}
