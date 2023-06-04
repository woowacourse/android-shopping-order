package woowacourse.shopping.ui.order

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.model.Coupon
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.remote.ServiceFactory
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.ui.order.presenter.OrderContract
import woowacourse.shopping.ui.order.presenter.OrderPresenter

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private val presenter: OrderContract.Presenter by lazy { initPresenter() }

    private fun initPresenter() =
        OrderPresenter(
            this,
            OrderRepositoryImpl(
                OrderDataSourceImpl(
                    ServiceFactory.orderService,
                    AuthInfoDataSourceImpl.getInstance(this),
                ),
            ),
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter.fetchCoupons()
    }

    override fun setCoupons(coupons: List<Coupon>) {
        val couponList: MutableList<String> = mutableListOf()
        couponList.add(BLANK)
        couponList.addAll(coupons.map { it.name })

        initAdapter(couponList)
    }

    private fun initAdapter(couponList: MutableList<String>) {
        val adapter = ArrayAdapter(this, R.layout.simple_list_item_1, couponList)
        binding.spinnerCoupon.adapter = adapter
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
