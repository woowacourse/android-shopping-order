package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartRemoteDataSource
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cash.CashRemoteDataSource
import woowacourse.shopping.data.cash.CashRepositoryDefault
import woowacourse.shopping.data.product.ProductRemoteDataSource
import woowacourse.shopping.data.shoppingpref.ShoppingOrderSharedPreference
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.CartProductModel
import woowacourse.shopping.util.executeExceptionHandler

class OrderActivity : AppCompatActivity(), OrderContract.View {

    private lateinit var binding: ActivityOrderBinding

    private val presenter: OrderContract.Presenter by lazy {
        val sharedPref = ShoppingOrderSharedPreference(applicationContext)
        val productDataSource = ProductRemoteDataSource(sharedPref.baseUrl, sharedPref.userInfo)
        val cartRepository: CartRepository =
            CartRepositoryImpl(
                CartRemoteDataSource(sharedPref.baseUrl, sharedPref.userInfo),
                productDataSource,
            )
        val cashRepository =
            CashRepositoryDefault(CashRemoteDataSource(sharedPref.baseUrl, sharedPref.userInfo))
        OrderPresenter(this, cartRepository, cashRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadOrderCarts()
        initView()
        initChargeCash()
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
        binding.textRecentCash.text = cash.toString()
        binding.editOrderAddCash.text = null
    }

    private fun initView() {
        presenter.loadCash()
    }

    private fun initChargeCash() {
        binding.textOrderAddCash.setOnClickListener {
            presenter.chargeCash(getChargeCash())
        }
    }

    private fun getChargeCash() = binding.editOrderAddCash.text.toString().toInt()

    companion object {
        private const val ORDER_CART_ID_LIST = "ORDER_CART_LIST"
        fun getIntent(context: Context, orderCartIds: List<Long>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putExtra(ORDER_CART_ID_LIST, orderCartIds.toLongArray())
            }
        }
    }
}
