package woowacourse.shopping.feature.orderDetail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.TokenSharedPreference
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.repository.order.OrderRemoteRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.feature.order.OrderProductAdapter
import woowacourse.shopping.model.OrderDetailProductUiModel
import woowacourse.shopping.model.OrderInfoUiModel
import woowacourse.shopping.model.OrderStateUiModel

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {

    private val binding by lazy { ActivityOrderDetailBinding.inflate(layoutInflater) }
    private lateinit var presenter: OrderDetailContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = getString(R.string.order_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val orderId = intent.getIntExtra(ORDER_ID_KEY, -1)

        initPresenter(orderId)
        presenter.loadProducts()
    }

    private fun initPresenter(orderId: Int) {
        val token = TokenSharedPreference.getInstance(this).getToken("") ?: ""
        presenter =
            OrderDetailPresenter(
                this, orderId,
                OrderRemoteRepositoryImpl(OrderDataSourceImpl(token))
            )
    }

    override fun initAdapter(orderProducts: List<OrderDetailProductUiModel>) {
        binding.recyclerviewOrderProducts.adapter = OrderProductAdapter(products = orderProducts)
    }

    override fun setUpView(orderInfo: OrderInfoUiModel) {
        with(binding) {
            this.orderInfo = orderInfo
            tvProductsPrice.text = getString(R.string.price_format)
                .format(orderInfo.products.sumOf { it.productUiModel.price })
            if (orderInfo.orderState != OrderStateUiModel.PENDING) {
                tvCancelOrder.visibility = View.GONE
            }
            tvCancelOrder.setOnClickListener {
                presenter.cancelOrder(orderInfo.orderId)
            }
        }
    }

    override fun successCancel() {
        finish()
    }

    override fun showErrorMessage(t: Throwable) {
        Toast.makeText(this, "${t.message}", Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val ORDER_ID_KEY = "ORDER_ID_KEY"

        fun getIntent(
            context: Context,
            id: Int,
        ): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(ORDER_ID_KEY, id)
            return intent
        }
    }
}
