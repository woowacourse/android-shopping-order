package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.remote.order.OrderRemoteDataSource
import woowacourse.shopping.data.remote.user.UserRemoteDataSource
import woowacourse.shopping.data.remote.user.UserRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.OrderCartModel
import woowacourse.shopping.presentation.order.adapter.OrderCartAdapter
import woowacourse.shopping.presentation.productlist.ProductListActivity
import woowacourse.shopping.util.getParcelableArrayListExtraCompat
import woowacourse.shopping.util.noIntentExceptionHandler

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private val binding: ActivityOrderBinding by lazy {
        ActivityOrderBinding.inflate(layoutInflater)
    }
    private val presenter: OrderContract.Presenter by lazy {
        OrderPresenter(
            this,
            UserRepositoryImpl(UserRemoteDataSource(PreferenceUtil(this))),
            OrderRepositoryImpl(OrderRemoteDataSource(PreferenceUtil(this)))
        )
    }
    private val orderCartAdapter = OrderCartAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        initView()
    }

    private fun initView() {
        setAdapter()
        initOrderProduct()
        initPoint()
        presenter.loadTotalPrice()
        setOrderButtonClick()
    }

    private fun setOrderButtonClick() {
        binding.buttonOrder.setOnClickListener {
            presenter.order(binding.etOrderUsingPoint.text.toString())
        }
    }

    override fun showOrderSuccess() {
        Toast.makeText(this, getString(R.string.order_success_message), Toast.LENGTH_SHORT).show()
        val intent = Intent(this, ProductListActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        startActivity(intent)
    }

    override fun showOrderFail() {
        Toast.makeText(this, getString(R.string.order_fail_message), Toast.LENGTH_SHORT).show()
    }

    override fun updateTotalPrice(price: Int) {
        binding.textOrderPrice.text = getString(R.string.price_format, price)
    }

    private fun initPoint() {
        presenter.loadPoint()
    }

    private fun setAdapter() {
        binding.rvOrderProducts.adapter = orderCartAdapter
    }

    private fun setPointTextChangeListener() {
        binding.etOrderUsingPoint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.checkPointOver(text.toString())
            }

            override fun afterTextChanged(s: Editable?) = Unit
        })
    }

    private fun initOrderProduct() {
        intent.getParcelableArrayListExtraCompat<OrderCartModel>(ORDER_CART_KEY_VALUE)
            ?.let { orderCarts ->
                presenter.initOrderCarts(orderCarts)
            } ?: noIntentExceptionHandler(getString(R.string.order_cart_model_null_error_message))
    }

    override fun showOrderCarts(orderCarts: List<OrderCartModel>) {
        orderCartAdapter.submitList(orderCarts)
    }

    override fun showPoint(point: Int) {
        binding.tvOrderAccumulatedPoint.text = getString(R.string.price_format, point)
        setPointTextChangeListener()
    }

    companion object {
        private const val ORDER_CART_KEY_VALUE = "ORDER_CART_KEY_VALUE"
        fun getIntent(context: Context, orderCart: List<OrderCartModel>): Intent {
            return Intent(context, OrderActivity::class.java)
                .putParcelableArrayListExtra(ORDER_CART_KEY_VALUE, ArrayList(orderCart))
        }
    }
}
