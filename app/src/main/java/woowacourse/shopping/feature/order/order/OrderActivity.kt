package woowacourse.shopping.feature.order.order

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.remote.CartRepositoryImpl
import woowacourse.shopping.data.repository.remote.OrderRepositoryImpl
import woowacourse.shopping.data.service.cart.CartRemoteService
import woowacourse.shopping.data.service.order.OrderRemoteService
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.feature.order.detail.OrderDetailActivity
import woowacourse.shopping.model.CartProductUiModel
import woowacourse.shopping.util.toMoneyFormat

class OrderActivity : AppCompatActivity(), OrderContract.View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderProductAdapter
    private lateinit var presenter: OrderContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order)

        supportActionBar?.title = getString(R.string.order)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val cartIds: List<Long> =
            intent.getLongArrayExtra(PRODUCTS_ID_KEY)?.toList() ?: listOf()
        presenter = OrderPresenter(
            cartIds,
            this,
            CartRepositoryImpl(CartRemoteService()),
            OrderRepositoryImpl(
                OrderRemoteService(),
            ),
        )
        presenter.requestProducts()
        binding.btnOrder.setOnClickListener {
            presenter.order()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showProducts(products: List<CartProductUiModel>) {
        adapter = OrderProductAdapter(products.map { it.productUiModel })
        binding.recyclerviewOrderedProducts.adapter = adapter
    }

    override fun showDiscount(standardPrice: Int, discountAmount: Int) {
        binding.textNoDiscount.visibility = View.GONE
        binding.textDiscountCondition.text =
            getString(R.string.discount_condition, standardPrice.toMoneyFormat())
        binding.textDiscountAmount.text =
            getString(R.string.discount_amount, discountAmount.toMoneyFormat())
    }

    override fun showNonDiscount() {
        binding.textNoDiscount.visibility = View.VISIBLE
        binding.textDiscountCondition.visibility = View.GONE
        binding.textDiscountAmount.visibility = View.GONE
    }

    override fun showPayAmountInfo(totalPrice: Int, discountAmount: Int) {
        binding.textOrderPrice.text = getString(R.string.price_format, totalPrice.toMoneyFormat())
        binding.textDiscountPrice.text =
            getString(R.string.price_format, discountAmount.toMoneyFormat())
    }

    override fun showPayAmount(payAmount: Int) {
        binding.textFinalPrice.text = getString(R.string.price_format, payAmount.toMoneyFormat())
    }

    override fun succeedInOrder(orderId: Long) {
        Toast.makeText(this, getString(R.string.order_success), Toast.LENGTH_SHORT).show()
        val orderDetailIntent = OrderDetailActivity.getIntent(this, orderId)
        startActivity(orderDetailIntent)
    }

    override fun failToOrder() {
        Toast.makeText(this, getString(R.string.order_fail), Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val PRODUCTS_ID_KEY = "productsId"
        fun getIntent(context: Context, cartId: List<Long>): Intent {
            return Intent(context, OrderActivity::class.java)
                .putExtra(PRODUCTS_ID_KEY, cartId.toLongArray())
        }
    }
}
