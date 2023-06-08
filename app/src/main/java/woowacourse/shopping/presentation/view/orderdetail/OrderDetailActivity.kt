package woowacourse.shopping.presentation.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.model.OrderDetailEntity
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.order.adapter.OrderProductAdapter
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.RetrofitUtil
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding
    override lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var server: Server

    private val orderId by lazy { intent.getLongExtra(KEY_ORDER_ID, ERROR_ORDER_ID) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (orderId == ERROR_ORDER_ID) return finish()

        setPresenter()
        presenter.initView()
    }

    private fun setPresenter() {
        server = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()

        val retrofit = RetrofitUtil(server).createRetrofit()
        val orderRepository = OrderRepositoryImpl(retrofit)
        presenter = OrderDetailPresenter(this, orderId, orderRepository)
    }

    override fun setView(orderDetail: OrderDetailEntity, products: List<CartModel>) {
        binding.orderDetail = orderDetail

        val orderProductAdapter = OrderProductAdapter()
        orderProductAdapter.setItems(products)
        binding.rvOrderDetailProductList.adapter = orderProductAdapter
    }

    override fun setTotalPriceView(totalPrice: Int) {
        binding.tvOrderDetailTotalPrice.text = getString(R.string.product_price_format, totalPrice)
    }

    override fun handleErrorView() {
        binding.root.post {
            showToast(getString(R.string.toast_message_system_error))
        }
    }

    companion object {
        private const val KEY_ORDER_ID = "order_id"
        private const val ERROR_ORDER_ID = -1L

        internal fun createIntent(
            context: Context,
            server: Server,
            orderId: Long,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(KEY_ORDER_ID, orderId)
            intent.putExtra(KEY_SERVER_SERVER, server)

            return intent
        }
    }
}
