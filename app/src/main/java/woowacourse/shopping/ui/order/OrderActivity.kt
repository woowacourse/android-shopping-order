package woowacourse.shopping.ui.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.data.datasource.local.AuthInfoDataSourceImpl
import woowacourse.shopping.data.datasource.remote.coupon.CouponDataSourceImpl
import woowacourse.shopping.data.datasource.remote.order.OrderDataSourceImpl
import woowacourse.shopping.data.repository.CouponRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.ui.order.contract.OrderContract
import woowacourse.shopping.ui.order.contract.presenter.OrderPresenter
import woowacourse.shopping.ui.orderdetail.OrderDetailActivity
import woowacourse.shopping.utils.Extensions.intentSerializable

class OrderActivity : AppCompatActivity(), OrderContract.View, OrderListener {

    private lateinit var binding: ActivityOrderBinding
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var rvAdapter: OrderAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolbar()

        presenter = OrderPresenter(
            intent.intentSerializable("KEY_CART_ITEMS", CartItemsUIModel::class.java)
                ?: throw IllegalArgumentException(),
            this,
            CouponRepositoryImpl(CouponDataSourceImpl(AuthInfoDataSourceImpl.getInstance(this))),
            OrderRepositoryImpl(OrderDataSourceImpl(AuthInfoDataSourceImpl.getInstance(this))),
        )

        binding.listener = this
    }

    private fun setToolbar() {
        setSupportActionBar(binding.tbOrder)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun setPrice(price: Int) {
        binding.price = price
    }

    override fun setCoupons(coupon: List<String>) {
        Log.d("OrderActivity", coupon.toString())
        val couponAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            coupon,
        )
        binding.spinnerCoupon.adapter = couponAdapter
    }

    override fun navigateToOrderDetail(id: Long) {
        startActivity(OrderDetailActivity.from(this, id))
        finish()
    }

    override fun setUpOrder(cartProducts: List<CartProductUIModel>) {
        rvAdapter = OrderAdapter(cartProducts)
        binding.rvOrder.adapter = rvAdapter
    }

    override fun onOrderClick() {
        presenter.navigateToOrderDetail()
    }

    override fun onItemSelected(couponName: String) {
        presenter.getTotalPrice(couponName)
    }

    companion object {
        fun from(
            context: Context,
            cartItems: CartItemsUIModel,
        ): Intent {
            return Intent(context, OrderActivity::class.java).apply {
                putExtra("KEY_CART_ITEMS", cartItems)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        }
    }
}
