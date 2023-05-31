package woowacourse.shopping.presentation.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.order.OrderServiceHelper
import woowacourse.shopping.data.user.UserRepositoryImpl
import woowacourse.shopping.data.user.UserServiceHelper
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.util.getParcelableExtraCompat
import woowacourse.shopping.util.noIntentExceptionHandler

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var orderAdapter: OrderListAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        initView()
    }

    private fun initView() {
        setToolBar()
        initPresenter()
        presenter.showUserTotalPoint()
        pointTextChange()
        presenter.showOrderPrice()
        presenter.showPaymentPrice()
        paymentButtonClick()
        initOrderProductsView()
    }

    private fun initPresenter() {
        intent.getParcelableExtraCompat<OrderProductsModel>(ORDER_PRODUCTS_KEY)
            ?.let { receivedProduct ->
                presenter = OrderPresenter(
                    view = this,
                    orderProductsModel = receivedProduct,
                    orderRepository = OrderRepositoryImpl(OrderServiceHelper(PreferenceUtil(this))),
                    userRepository = UserRepositoryImpl(UserServiceHelper(PreferenceUtil(this)))
                )
            } ?: noIntentExceptionHandler(getString(R.string.product_model_null_error_message))
    }

    private fun initOrderProductsView() {
        orderAdapter = OrderListAdapter()
        binding.recyclerOrder.adapter = orderAdapter
        presenter.loadOrderItems()
    }

    private fun setUpBinding() {
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarOrder.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
    }

    private fun pointTextChange() {
        binding.editTextOrderUsagePoint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) = Unit
            override fun afterTextChanged(e: Editable?) {
                val currentText = e.toString()
                presenter.checkPointAble(currentText)
                presenter.showOrderPrice()
                presenter.showPaymentPrice()
            }
        })
    }

    private fun paymentButtonClick() {
        binding.buttonOrderPayment.setOnClickListener {
            presenter.addOrder()
        }
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

    companion object {
        private const val ORDER_PRODUCTS_KEY = "ORDER_PRODUCTS_KEY"
        fun getIntent(
            context: Context,
            orderProductsModel: OrderProductsModel
        ): Intent {
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(ORDER_PRODUCTS_KEY, orderProductsModel)
            return intent
        }
    }

    override fun setUserTotalPoint(point: Int?) {
        if (point == -1) binding.textOrderTotalPoint.text =
            getString(R.string.not_load_error_message)
        binding.textOrderTotalPoint.text = getString(R.string.order_total_point_format, point)
        binding.editTextOrderUsagePoint.hint = point.toString()
    }

    override fun setUsagePoint(pointText: String) {
        with(binding.editTextOrderUsagePoint) {
            setText(pointText)
            setSelection(text.length)
        }
    }

    override fun setOrderPrice(price: Int) {
        binding.textOrderOriginalPrice.text = getString(R.string.price_format, price)
    }

    override fun setPaymentPrice(price: Int) {
        binding.textOrderFinalPrice.text = getString(R.string.price_format, price)
    }

    override fun showAddOrderComplete(completeMessage: String?) {
        if (completeMessage == null) {
            Toast.makeText(this, "결제가 이루어지지 않았어요 ㅠㅠ", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, completeMessage, Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun setOrderItems(orderProductsModel: List<OrderProductModel>) {
        orderAdapter.submitList(orderProductsModel)
    }
}
