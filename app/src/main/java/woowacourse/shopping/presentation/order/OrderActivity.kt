package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRemoteDataSource
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cash.CashRemoteDataSource
import woowacourse.shopping.data.cash.CashRepositoryDefault
import woowacourse.shopping.data.order.OrderRemoteDataSource
import woowacourse.shopping.data.order.OrderRepositoryDefault
import woowacourse.shopping.data.product.ProductRemoteDataSource
import woowacourse.shopping.data.util.RetrofitUtil
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.presentation.orderdetail.OrderDetailActivity
import woowacourse.shopping.util.executeExceptionHandler

class OrderActivity : AppCompatActivity(), OrderContract.View {

    private lateinit var binding: ActivityOrderBinding

    private val presenter: OrderContract.Presenter by lazy {
        val retrofit = RetrofitUtil.getInstance().retrofit
        val productDataSource = ProductRemoteDataSource(retrofit)
        val cartRepository = CartRepositoryImpl(CartRemoteDataSource(retrofit), productDataSource)
        val cashRepository = CashRepositoryDefault(CashRemoteDataSource(retrofit))
        val orderRepository = OrderRepositoryDefault(OrderRemoteDataSource(retrofit))
        OrderPresenter(this, cartRepository, cashRepository, orderRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadOrderCarts()
        loadRecentCash()
        initView()
    }

    private fun loadRecentCash() {
        presenter.loadCash()
    }

    private fun loadOrderCarts() {
        val cartIds = intent.getLongArrayExtra(ORDER_CART_ID_LIST)?.toList()
            ?: return executeExceptionHandler(getString(R.string.no_cart_error_message))
        presenter.loadOrderCarts(cartIds)
    }

    override fun showOrderCartProducts(cartProductModels: List<CartProductModel>) {
        binding.recyclerOrder.adapter = OrderAdapter(cartProductModels)
    }

    override fun showCash(cash: Int) {
        binding.textRecentCash.text = getString(R.string.price_format, cash)
        binding.editOrderAddCash.text = null
    }

    override fun showTotalPrice(totalPrice: Int) {
        binding.textTotalProductPrice.text = getString(R.string.price_format, totalPrice)
    }

    private fun initView() {
        setToolBar()
        initChargeCash()
        initOrder()
    }

    private fun initOrder() {
        binding.buttonPayment.setOnClickListener {
            presenter.orderCartProducts()
        }
    }

    private fun initChargeCash() {
        binding.textOrderAddCash.setOnClickListener {
            val cash = getChargeCash()
            if (cash > 0) {
                presenter.chargeCash(getChargeCash())
            }
        }
    }

    private fun getChargeCash(): Int {
        val cash = binding.editOrderAddCash.text.toString()
        if (cash == "") return 0
        return cash.toInt()
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarOrder.toolbar)
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

    override fun showOrderDetail(orderId: Long) {
        Toast.makeText(this, getString(R.string.complete_order), Toast.LENGTH_SHORT).show()
        startActivity(OrderDetailActivity.getIntent(this, orderId))
        finish()
    }

    override fun handleRequestError() {
        Toast.makeText(this, getString(R.string.loading_error_message), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val ORDER_CART_ID_LIST = "ORDER_CART_LIST"
        fun getIntent(context: Context, orderCartIds: List<Long>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putExtra(ORDER_CART_ID_LIST, orderCartIds.toLongArray())
            }
        }
    }
}
