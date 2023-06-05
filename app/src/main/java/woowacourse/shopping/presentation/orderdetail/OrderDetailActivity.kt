package woowacourse.shopping.presentation.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.order.OrderRemoteDataSource
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.orderdetail.adapter.OrderDetailProductAdapter

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private val binding by lazy { ActivityOrderDetailBinding.inflate(layoutInflater) }

    private val presenter by lazy {
        OrderDetailPresenter(
            this,
            OrderRepositoryImpl(OrderRemoteDataSource(PreferenceUtil(this)))
        )
    }

    override fun showOrderDetail(orderDetail: OrderDetailModel) {
        binding.orderDetail = orderDetail
        binding.rvOrderDetailProducts.adapter = OrderDetailProductAdapter(orderDetail.orderItems)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loadOrderInfo()
    }

    private fun loadOrderInfo() {
        val orderId = intent.extras?.getInt(CART_PRODUCT_KEY_VALUE) ?: -1
        presenter.loadOrderDetail(orderId)
    }

    companion object {
        private const val CART_PRODUCT_KEY_VALUE = "CART_PRODUCT_KEY_VALUE"
        fun getIntent(context: Context, orderId: Int): Intent {
            return Intent(context, OrderDetailActivity::class.java).apply {
                putExtra(CART_PRODUCT_KEY_VALUE, orderId)
            }
        }
    }
}
