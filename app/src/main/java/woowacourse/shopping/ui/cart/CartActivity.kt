package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication.Companion.pref
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.mapper.toUi
import woowacourse.shopping.model.UiCartProduct
import woowacourse.shopping.model.UiPage
import woowacourse.shopping.ui.cart.CartContract.View
import woowacourse.shopping.ui.cart.listener.CartClickListener
import woowacourse.shopping.ui.cart.recyclerview.adapter.CartAdapter
import woowacourse.shopping.ui.order.main.OrderActivity
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.extension.showToast
import woowacourse.shopping.util.inject.injectCartPresenter
import woowacourse.shopping.util.toast.Toaster

class CartActivity : AppCompatActivity(), View, CartClickListener {
    private val presenter: CartPresenter by lazy {
        injectCartPresenter(this, pref.getBaseUrl().toString())
    }
    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater).setContentView(this)
        binding.lifecycleOwner = this
        binding.presenter = presenter
        binding.adapter = CartAdapter(this)
        presenter.fetchCart(START_PAGE)
    }

    override fun updateCart(cartProducts: List<UiCartProduct>) {
        binding.adapter?.submitList(cartProducts)
    }

    override fun updateNavigatorEnabled(previousEnabled: Boolean, nextEnabled: Boolean) {
        binding.previousButton.isEnabled = previousEnabled
        binding.nextButton.isEnabled = nextEnabled
    }

    override fun updatePageNumber(page: UiPage) {
        binding.pageNumberTextView.text = page.toText()
    }

    override fun updateTotalPrice(totalPrice: Int) {
        binding.totalPriceTextView.text = getString(R.string.price_format, totalPrice)
    }

    override fun onCountChanged(cartProduct: UiCartProduct, changedCount: Int) {
        presenter.changeProductCount(cartProduct, changedCount)
    }

    override fun onCheckStateChanged(cartProduct: UiCartProduct, isChecked: Boolean) {
        presenter.changeProductSelectState(cartProduct, isChecked)
    }

    override fun onDeleteClick(cartProduct: UiCartProduct) {
        presenter.removeProduct(cartProduct)
    }

    override fun showOrderComplete(cartProducts: List<CartProduct>, productCount: Int) {
        navigateToOrder(cartProducts)
    }

    override fun showOrderFailed() {
        showToast(getString(R.string.order_failed_message))
    }

    override fun showLoadFailed(error: String) {
        Toaster.showToast(this, LOAD_ERROR_MESSAGE.format(error))
    }

    override fun navigateToHome() {
        finish()
    }

    override fun navigateToOrder(cartProducts: List<CartProduct>) {
        val intent = OrderActivity.newIntent(this, cartProducts.map { it.toUi() })
        startActivity(intent)
        finish()
    }

    companion object {
        private const val START_PAGE = 1
        private const val LOAD_ERROR_MESSAGE = "[ERROR] 데이터를 불러오는 데에 실패했습니다. : %s"

        fun getIntent(context: Context) = Intent(context, CartActivity::class.java)
    }
}
