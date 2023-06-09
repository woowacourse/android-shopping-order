package woowacourse.shopping.feature.order.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.remote.OrderRepositoryImpl
import woowacourse.shopping.data.service.order.OrderRemoteService
import woowacourse.shopping.databinding.ActivityOrderDetailBinding
import woowacourse.shopping.feature.order.order.OrderProductAdapter
import woowacourse.shopping.model.OrderDetailUiModel
import woowacourse.shopping.model.OrderProductUiModel
import woowacourse.shopping.model.ProductUiModel
import woowacourse.shopping.util.toMoneyFormat

class OrderDetailActivity : AppCompatActivity(), OrderDetailContract.View {
    private lateinit var presenter: OrderDetailContract.Presenter
    private lateinit var binding: ActivityOrderDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_detail)

        supportActionBar?.title = getString(R.string.order_detail)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val orderId: Long = intent.getLongExtra(ORDER_ID_KEY, 0)
        presenter = OrderDetailPresenter(this, OrderRepositoryImpl(OrderRemoteService()))
        presenter.requestOrderDetail(orderId)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        private const val ORDER_ID_KEY = "orderKey"
        fun getIntent(context: Context, orderId: Long): Intent {
            val intent = Intent(context, OrderDetailActivity::class.java)
            intent.putExtra(ORDER_ID_KEY, orderId)
            return intent
        }
    }

    override fun showOrderDetail(orderDetail: OrderDetailUiModel) {
        binding.textOrderDate.text = orderDetail.toDateFormat()
        initOrderPayInfo(orderDetail)
        initOrderedProducts(orderDetail.orderItems)
    }

    override fun failToLoadOrder(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initOrderPayInfo(orderDetail: OrderDetailUiModel) {
        binding.textPayPrice.text = formatMoney(orderDetail.priceBeforeDiscount)
        val discountAmount = orderDetail.priceBeforeDiscount - orderDetail.priceAfterDiscount
        binding.textDiscountPrice.text = formatMoney(discountAmount)
        binding.textFinalPrice.text = formatMoney(orderDetail.priceAfterDiscount)
    }

    private fun formatMoney(money: Int): String {
        return getString(R.string.price_format, money.toMoneyFormat())
    }

    private fun initOrderedProducts(orderItems: List<OrderProductUiModel>) {
        val products = orderItems.map {
            ProductUiModel(it.productId, it.name, it.imageUrl, it.price, it.count)
        }
        binding.recyclerviewOrderedProducts.adapter = OrderProductAdapter(products)
    }
}
