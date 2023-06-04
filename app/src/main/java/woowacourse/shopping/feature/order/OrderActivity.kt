package woowacourse.shopping.feature.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.domain.repository.OrderRepository
import woowacourse.shopping.R
import woowacourse.shopping.ServerType
import woowacourse.shopping.data.order.OrderRemoteRepository
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.order.detail.OrderDetailActivity
import woowacourse.shopping.model.CartState
import woowacourse.shopping.model.mapper.toDomain

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private val url by lazy { intent.getStringExtra(ServerType.INTENT_KEY)!! }
    private val presenter: OrderContract.Presenter by lazy {
        val orderPendingCart: CartState by lazy { intent.getParcelableExtra(ORDER_PRODUCTS_KEY)!! }
        val orderRepository: OrderRepository = OrderRemoteRepository(url = url)
        OrderPresenter(
            view = this,
            orderPendingCart = orderPendingCart.toDomain(),
            orderRepository = orderRepository,
        )
    }
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderPendingCartListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        setToolBarBackButton()
        setPayButtonClickListener()
        presenter.loadOrderPendingCart()
        addOrderProductListDivider()
        presenter.calculatePrice()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
    }

    private fun setToolBarBackButton() {
        setSupportActionBar(binding.orderTb)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setPayButtonClickListener() {
        binding.payBtn.setOnClickListener {
            presenter.navigateToOrderDetail()
        }
    }

    private fun addOrderProductListDivider() {
        binding.orderPendingCartRv.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayout.VERTICAL
            )
        )
    }

    override fun setOrderPendingCart(orderPendingCart: CartState) {
        adapter = OrderPendingCartListAdapter(orderPendingCart)
        binding.orderPendingCartRv.adapter = adapter
    }

    override fun setProductsSum(productsSum: Int) {
        binding.productsSumTv.text = getString(R.string.order_price_format, productsSum)
    }

    override fun setDiscountPrice(discountPrice: Int) {
        binding.discountPriceTv.text = getString(R.string.order_price_format, discountPrice)
    }

    override fun setFinalPrice(finalPrice: Int) {
        binding.finalPriceTv.text = getString(R.string.order_price_format, finalPrice)
        binding.payBtn.text = getString(R.string.order_pay_format, finalPrice)
    }

    override fun showOrderDetailPage(orderId: Long) {
        OrderDetailActivity.startActivity(this, orderId = orderId, serverUrl = url)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            setResult(RESULT_OK)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val ORDER_PRODUCTS_KEY = "order_products_key"

        fun startActivity(context: Context, orderPendingCart: CartState, serverUrl: String) {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(ORDER_PRODUCTS_KEY, orderPendingCart)
            intent.putExtra(ServerType.INTENT_KEY, serverUrl)
            context.startActivity(intent)
        }
    }
}
