package woowacourse.shopping.presentation.myorder.detail

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.data.HttpErrorHandler
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.data.order.OrderRepositoryImpl
import woowacourse.shopping.data.order.OrderServiceHelper
import woowacourse.shopping.databinding.ActivityMyOrderDetailBinding
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.order.OrderItemsAdapter

class MyOrderDetailActivity : AppCompatActivity(), MyOrderDetailContract.View {
    private lateinit var binding: ActivityMyOrderDetailBinding
    private lateinit var orderItemsAdapter: OrderItemsAdapter
    private lateinit var presenter: MyOrderDetailContract.Presenter
    private lateinit var productPriceTextView: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        initPresenter()
        initView()
    }

    private fun setUpBinding() {
        binding = ActivityMyOrderDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initPresenter() {
        val orderId = intent.getIntExtra(ORDER_ID_KEY, 0)
        presenter = MyOrderDetailPresenter(
            this,
            OrderRepositoryImpl(
                OrderServiceHelper(PreferenceUtil(this)),
                HttpErrorHandler(this)
            ),
            orderId
        )
    }

    private fun initView() {
        orderItemsAdapter = OrderItemsAdapter(::updateProductPrice)
        binding.recyclerMyOrderDetail.adapter = orderItemsAdapter
        binding.recyclerMyOrderDetail.layoutManager = LinearLayoutManager(this)
        presenter.loadOrderDetail()
    }

    private fun updateProductPrice(textView: TextView, orderProductModel: OrderProductModel) {
        productPriceTextView = textView
        presenter.updateProductPrice(orderProductModel)
    }

    override fun setOrderProducts(orderProducts: List<OrderProductModel>) {
        orderItemsAdapter.submitList(orderProducts)
    }

    override fun setPaymentInfo(orderDetailModel: OrderDetailModel) {
        binding.orderDetailModel = orderDetailModel
    }

    override fun setProductPrice(price: Int) {
        productPriceTextView.text = getString(R.string.price_format, price)
    }

    companion object {
        private const val ORDER_ID_KEY = "ORDER_ID_KEY"

        fun getIntent(context: Context, orderId: Int): Intent {
            val intent = Intent(context, MyOrderDetailActivity::class.java)
            intent.putExtra(ORDER_ID_KEY, orderId)
            return intent
        }
    }
}
