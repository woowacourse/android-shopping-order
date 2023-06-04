package woowacourse.shopping.ui.order.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityOrderBinding
import woowacourse.shopping.domain.model.Point
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiCartProducts
import woowacourse.shopping.model.UiPrice
import woowacourse.shopping.ui.order.main.OrderContract.Presenter
import woowacourse.shopping.ui.order.main.OrderContract.View
import woowacourse.shopping.ui.order.main.recyclerview.adapter.OrderAdapter
import woowacourse.shopping.util.bindingadapter.setDiscount
import woowacourse.shopping.util.extension.getParcelableExtraCompat
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.extension.showToast
import woowacourse.shopping.util.inject.injectOrderPresenter

class OrderActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderAdapter
    private val presenter: Presenter by lazy {
        injectOrderPresenter(
            view = this,
            cartProducts = intent.getParcelableExtraCompat(CART_PRODUCTS_KEY)!!,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderBinding.inflate(layoutInflater).setContentView(this)
        setActionBar()
        presenter.loadOrderProducts()
        presenter.loadAvailablePoints()
        presenter.loadPayment()
        watchUsedPoints()
        onOrderClickListener()
    }

    private fun setActionBar() {
        setSupportActionBar(binding.orderToolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun showOrderProductList(cartProducts: List<UiCartProduct>) {
        binding.orderRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = OrderAdapter(cartProducts)
        binding.orderRecyclerView.adapter = adapter
    }

    override fun showAvailablePoint(point: Point) {
        binding.point = point
    }

    override fun watchUsedPoints() {
        binding.pointEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                if (binding.pointEditText.text != null) {
                    val discountPoint = binding.pointEditText.text.toString().toInt()
                    binding.discountPayment.setDiscount(discountPoint)
                    presenter.calculateFinalPayment(discountPoint)
                }
            }
        })
    }

    override fun showTotalPayment(totalPayment: UiPrice) {
        binding.originalPrice = totalPayment
    }

    override fun showFinalPayment(finalPayment: UiPrice) {
        binding.finalPrice = finalPayment
    }

    override fun onOrderClickListener() {
        binding.orderBtn.setOnClickListener {
            presenter.order()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        presenter.navigateToHome(item.itemId)
        return super.onOptionsItemSelected(item)
    }

    override fun navigateToHome(orderedProductCount: Int) {
        if (orderedProductCount > 0) {
            showToast(getString(R.string.order_success_message, orderedProductCount))
        }
        finish()
    }

    companion object {

        const val CART_PRODUCTS_KEY = "cart_products_key"

        fun newIntent(context: Context, cartProducts: List<UiCartProduct>): Intent {
            val parcelItems = UiCartProducts(cartProducts)
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(CART_PRODUCTS_KEY, parcelItems)
            return intent
        }
    }
}
