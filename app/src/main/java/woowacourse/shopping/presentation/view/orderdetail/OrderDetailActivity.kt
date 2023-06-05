package woowacourse.shopping.presentation.view.orderdetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.RetrofitBuilder
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.data.respository.order.source.remote.OrderRemoteDataSourceImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.view.order.adapter.OrderProductListAdapter
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_TOKEN
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var binding: ActivityOrderDetailBinding

    private lateinit var url: Server.Url
    private lateinit var token: Server.Token

    private lateinit var retrofitBuilder: RetrofitBuilder

    private lateinit var presenter: OrderDetailContract.Presenter

    private val orderId: Long by lazy {
        intent.getLongExtra(KEY_ORDER_ID, -1)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)

        if (orderId == -1L) return finish()
        url = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        token = intent.getSerializableCompat(KEY_SERVER_TOKEN) ?: return finish()
        retrofitBuilder = RetrofitBuilder.getInstance(url, token)

        setToolbar()
        setPresenter()
        presenter.loadOrderDetail(orderId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }

        return true
    }

    private fun setToolbar() {
        supportActionBar?.title = getString(R.string.toolbar_title_order_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setPresenter() {
        val orderDataSource = OrderRemoteDataSourceImpl(retrofitBuilder.createOrderService())
        presenter = OrderDetailPresenter(
            this,
            orderRepository = OrderRepositoryImpl(orderDataSource)
        )
    }

    override fun showOrderProductItemView(orderProducts: List<CartProductModel>) {
        binding.rvOrderDetailProductList.post {
            binding.rvOrderDetailProductList.adapter = OrderProductListAdapter(orderProducts)
        }
    }

    override fun showOrderDateView(oderDate: String) {
        binding.orderDate = oderDate
    }

    override fun showOrderPriceView(orderPrice: Int) {
        binding.orderPrice = orderPrice
    }

    override fun showTotalPriceView(totalPrice: Int) {
        binding.totalPrice = totalPrice
    }

    override fun showUsedPointView(usedPoint: Int) {
        binding.usedPoint = usedPoint
    }

    override fun showSavedPointView(savedPoint: Int) {
        binding.savedPoint = savedPoint
    }

    override fun handleErrorView(message: String) {
        binding.root.post {
            showToast(message)
            finish()
        }
    }

    companion object {
        private const val KEY_ORDER_ID = "KEY_ORDER_ID"

        fun createIntent(
            context: Context,
            orderId: Long,
            url: Server.Url,
            token: Server.Token
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(KEY_ORDER_ID, orderId)
            intent.putExtra(KEY_SERVER_SERVER, url)
            intent.putExtra(KEY_SERVER_TOKEN, token)
            return intent
        }
    }
}
