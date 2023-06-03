package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.OrderInfoUIModel
import woowacourse.shopping.repositoryImpl.CartRepositoryImpl
import woowacourse.shopping.service.RemoteCartService
import woowacourse.shopping.ui.order.orderAdapter.OrderAdapter
import woowacourse.shopping.utils.ServerURL

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinding()
        initPresenter()
        initOrderInfo()
        setPointInput()
    }

    private fun initBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)
    }

    private fun initPresenter() {
        presenter = OrderPresenter(this, CartRepositoryImpl(RemoteCartService(ServerURL.url)))
    }

    private fun initOrderInfo() {
        val ids: List<Int> = intent.getIntegerArrayListExtra(KEY_ORDER_ID_LIST)?.toList() ?: return
        presenter.getOrderInfo(ids)
    }

    override fun initOrderPageInfo(orderInfo: OrderInfoUIModel) {
        binding.orderInfo = orderInfo
        binding.rvOrderItems.adapter = OrderAdapter(orderInfo.cartItems)
    }

    override fun setButtonEnable(isEnabled: Boolean) {
        binding.btnOrder.isEnabled = isEnabled
    }

    override fun showPointErrorMessage(errorCode: Int) {
        Toast.makeText(this@OrderActivity, errorCode, Toast.LENGTH_SHORT).show()
    }

    override fun updatePurchasePrice(discountPrice: Int, totalPrice: Int) {
        binding.tvPurchaseDiscountPrice.text = getString(R.string.product_price, discountPrice)
        binding.tvPurchaseTotalPrice.text = getString(R.string.product_price, totalPrice)
    }

    private fun setPointInput() {
        binding.etDiscountPoint.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                presenter.checkPointAvailable(s.toString().toIntOrNull())
            }

            override fun afterTextChanged(s: Editable?) {
                presenter.setTotalPrice(s.toString().toIntOrNull())
            }
        })
    }

    companion object {
        private const val KEY_ORDER_ID_LIST = "order_list"

        @StringRes
        val ERROR_POINT_INVALID_NUMBER = R.string.order_invalid_point

        @StringRes
        val ERROR_POINT_UNAVAILABLE = R.string.order_unavailable_point

        fun getIntent(context: Context, ids: ArrayList<Int>): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putIntegerArrayListExtra(KEY_ORDER_ID_LIST, ids)
            }
        }
    }
}
