package woowacourse.shopping.view.payment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityPaymentBinding
import woowacourse.shopping.model.data.BundleKeys.CART_IDS
import woowacourse.shopping.model.uimodel.OrderProductUIModel
import woowacourse.shopping.model.uimodel.PointUIModel
import woowacourse.shopping.model.uimodel.PriceUIModel
import woowacourse.shopping.server.retrofit.RetrofitClient
import woowacourse.shopping.view.orderdetail.OrderDetailActivity
import woowacourse.shopping.view.orderdetail.OrderDetailAdapter

class PaymentActivity : AppCompatActivity(), PaymentContract.View {

    override lateinit var presenter: PaymentContract.Presenter
    private lateinit var binding: ActivityPaymentBinding

    private lateinit var adapter: OrderDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPaymentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setPresenter()
        setAdapter()
        setView()
        setButtonOnClick()
    }

    private fun setPresenter() {
        presenter = PaymentPresenter(
            this,
            RetrofitClient.cartItemsService,
            RetrofitClient.membersService,
            RetrofitClient.payService
        )
    }

    private fun setAdapter() {
        adapter = OrderDetailAdapter(emptyList())
        binding.rvOrderProducts.adapter = adapter
    }

    private fun setView() {
        val cartIds = intent.getLongArrayExtra(CART_IDS) ?: LongArray(0)
        presenter.updateOrderProducts(cartIds.toTypedArray())
        presenter.getPoints()
    }

    private fun setButtonOnClick() {
        binding.tvPaymentButtom.setOnClickListener {
            presenter.payOrderProducts(
                binding.tvOrderTotalPrice.text.toString().toInt(),
                binding.etUsingPoint.text.toString().toInt()
            )
        }
        binding.etUsingPoint.addTextChangedListener(
            afterTextChanged = {
                binding.point = PointUIModel(it.toString().toInt())
            }
        )
    }

    override fun updateOrderProducts(orderItems: List<OrderProductUIModel>) {
        adapter.update(orderItems)
    }

    override fun showOrderDetail(orderId: Long) {
        startActivity(OrderDetailActivity.intent(this, orderId))
        finish()
    }

    override fun updatePoints(point: Int) {
        binding.tvAvailablePoint.text = this.getString(R.string.price_format).format(point)
    }

    override fun updateTotalPrice(price: Int) {
        binding.price = PriceUIModel(price)
    }

    companion object {
        fun intent(context: Context, cartIds: Array<Long>): Intent {
            return Intent(context, PaymentActivity::class.java).apply {
                putExtra(CART_IDS, cartIds)
            }
        }
    }
}
