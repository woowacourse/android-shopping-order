package woowacourse.shopping.ui.order

import android.R.layout.simple_list_item_1
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.model.Coupon
import woowacourse.shopping.R
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.datasource.remote.ordercomplete.OrderCompleteDataSourceImpl
import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.ui.order.adapter.OrderAdapter
import woowacourse.shopping.ui.order.presenter.OrderContract
import woowacourse.shopping.ui.order.presenter.OrderPresenter
import woowacourse.shopping.ui.ordercomplete.OrderCompleteActivity

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private val presenter: OrderContract.Presenter by lazy { initPresenter() }
    private lateinit var orderAdapter: OrderAdapter
    private lateinit var cartItems: CartItemsUIModel

    private fun initPresenter() =
        OrderPresenter(
            this,
            OrderRepositoryImpl(
                OrderDataSourceImpl(
                    ServiceFactory.orderService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
                OrderCompleteDataSourceImpl(
                    ServiceFactory.orderCompleteService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
            ),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cartItems = intent.getSerializableExtra(CART_ITEM) as CartItemsUIModel
        initView(cartItems)
        binding.tvOrderTotalBtn.setOnClickListener { startActivity(OrderCompleteActivity.from(this)) }
    }

    private fun initView(cartItems: CartItemsUIModel) {
        initOrderListAdapter(cartItems)
        presenter.fetchCoupons()
    }

    override fun setCoupons(coupons: List<Coupon>) {
        initCouponsAdapter(coupons)
    }

    private fun initCouponsAdapter(coupons: List<Coupon>) {
        val couponList: MutableList<String> = mutableListOf()
        couponList.add(BLANK)
        couponList.addAll(coupons.map { it.name })

        val adapter = ArrayAdapter(this, simple_list_item_1, couponList)
        binding.spinnerCoupon.adapter = adapter
        binding.spinnerCoupon.onItemSelectedListener = setItemSelectedListener(coupons)
    }

    private fun setItemSelectedListener(coupons: List<Coupon>) =
        object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                presenter.calculateTotal(position, coupons, cartItems)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("onNothingSelected", "onNothingSelected")
            }
        }

    private fun initOrderListAdapter(cartItems: CartItemsUIModel) {
        orderAdapter = OrderAdapter(cartItems.cartProducts)
        binding.rvOrder.adapter = orderAdapter
    }

    override fun setTotal(totalPrice: Int) {
        binding.tvOrderTotalBtn.text =
            String.format(getString(R.string.product_price), totalPrice)
    }

    companion object {
        private const val BLANK = " "
        private const val CART_ITEM = "CART_ITEM"
        fun from(context: Context, cartItems: CartItemsUIModel): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putExtra(CART_ITEM, cartItems)
            }
        }
    }
}
