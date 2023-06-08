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
import woowacourse.shopping.ShoppingApplication.Companion.pref
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
import woowacourse.shopping.util.toast.Toaster

class OrderActivity : AppCompatActivity(), View {
    private lateinit var binding: ActivityOrderBinding
    private lateinit var adapter: OrderAdapter
    private val presenter: Presenter by lazy {
        injectOrderPresenter(
            view = this,
            cartProducts = intent.getParcelableExtraCompat(CART_PRODUCTS_KEY),
            baseUrl = pref.getBaseUrl().toString(),
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
            override fun beforeTextChanged(inputValue: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(inputValue: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val inputPoint =
                    if (inputValue.isNullOrBlank()) {
                        0
                    } else {
                        inputValue.toString().toInt()
                    }

                binding.discountPayment.setDiscount(inputPoint)
                presenter.calculateFinalPayment(inputPoint)
            }

            override fun afterTextChanged(p0: Editable?) {}
        })
    }

    override fun showTotalPayment(totalPayment: UiPrice) {
        binding.originalPrice = totalPayment
        showFinalPayment(UiPrice(totalPayment.value))
    }

    override fun showFinalPayment(finalPayment: UiPrice) {
        binding.finalPrice = finalPayment
    }

    override fun onOrderClickListener() {
        binding.orderBtn.setOnClickListener {
            presenter.order()
        }
    }

    override fun showLoadFailed(error: String) {
        Toaster.showToast(this, LOAD_ERROR_MESSAGE.format(error))
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
        private const val LOAD_ERROR_MESSAGE = "[ERROR] 데이터를 불러오는 데에 실패했습니다. : %s"

        fun newIntent(context: Context, cartProducts: List<UiCartProduct>): Intent {
            val parcelItems = UiCartProducts(cartProducts)
            val intent = Intent(context, OrderActivity::class.java)
            intent.putExtra(CART_PRODUCTS_KEY, parcelItems)
            return intent
        }
    }
}
