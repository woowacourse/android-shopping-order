package woowacourse.shopping.presentation.view.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.model.Server
import woowacourse.shopping.data.respository.order.OrderRepositoryImpl
import woowacourse.shopping.data.respository.point.PointRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.view.order.adapter.CardAdapter
import woowacourse.shopping.presentation.view.order.adapter.OrderProductAdapter
import woowacourse.shopping.presentation.view.orderdetail.OrderDetailActivity
import woowacourse.shopping.presentation.view.productlist.ProductListActivity.Companion.KEY_SERVER_SERVER
import woowacourse.shopping.presentation.view.util.createRetrofit
import woowacourse.shopping.presentation.view.util.getSerializableCompat
import woowacourse.shopping.presentation.view.util.showToast

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    override lateinit var presenter: OrderContract.Presenter

    private lateinit var server: Server
    private lateinit var cartItems: List<CartModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        setPresenter()
        setCardView()
        presenter.initReservedPoint()
        presenter.initSavingPoint()
        presenter.initCartProducts()
        presenter.initOrderDetail()
        setListener()
    }

    private fun setPresenter() {
        server = intent.getSerializableCompat(KEY_SERVER_SERVER) ?: return finish()
        cartItems = intent.getParcelableArrayListExtra<CartModel>(KEY_CART_ITEMS)?.toList()
            ?: return finish()

        val retrofit = createRetrofit(server)

        val pointRepository = PointRepositoryImpl(retrofit)
        val orderRepository = OrderRepositoryImpl(retrofit)

        presenter = OrderPresenter(this, cartItems, pointRepository, orderRepository)
    }

    private fun setListener() {
        binding.etOrderUsePoint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) =
                Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit

            override fun afterTextChanged(s: Editable?) {
                val availablePoint =
                    binding.tvOrderAvailablePoint.text.toString().substringBeforeLast("포").toInt()

                presenter.setPoint(s, availablePoint)
            }
        })

        binding.btOrderPay.setOnClickListener {
            val usedPoint =
                binding.tvOrderPaymentDetailUsePoint.text.toString().substringBeforeLast("포")
                    .toInt()
            presenter.order(usedPoint)
        }
    }

    override fun setAvailablePointView(point: Int) {
        binding.tvOrderAvailablePoint.text = getString(R.string.point_format, point)
    }

    override fun setSavingPoint(point: Int) {
        binding.tvOrderPointSave.text = getString(R.string.point_format, point)
    }

    override fun setCartProductsView(products: List<CartModel>) {
        val orderProductAdapter = OrderProductAdapter()
        orderProductAdapter.setItems(products)
        binding.rvOrderProductList.adapter = orderProductAdapter
    }

    private fun setCardView() {
        val cardAdapter = CardAdapter()
        binding.rvOrderCardList.adapter = cardAdapter
    }

    override fun setTotalPriceView(totalPrice: Int) {
        binding.tvOrderPaymentDetailOrderPrice.text =
            getString(R.string.product_price_format, totalPrice)
    }

    override fun setOrderPriceView(point: Int, totalPrice: Int) {
        binding.tvOrderPaymentDetailUsePoint.text = getString(R.string.point_format, point)
        binding.tvOrderPaymentDetailTotalPrice.text =
            getString(R.string.product_price_format, totalPrice - point)
    }

    override fun clearUsedPointView() {
        binding.etOrderUsePoint.text.clear()
    }

    override fun getMessage(resourceId: Int): String {
        return getString(resourceId)
    }

    override fun moveToOrderDetail(orderId: Long) {
        val intent = OrderDetailActivity.createIntent(this, server, orderId)
        startActivity(intent)
        finish()
    }

    override fun handleErrorView(message: String) {
        binding.root.post {
            showToast(message)
        }
    }

    companion object {
        private const val KEY_CART_ITEMS = "KEY_CART_ITEMS"

        internal fun createIntent(
            context: Context,
            cartItems: List<CartModel>,
            server: Server,
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putParcelableArrayListExtra(KEY_CART_ITEMS, ArrayList(cartItems))
            intent.putExtra(KEY_SERVER_SERVER, server)

            return intent
        }
    }
}
