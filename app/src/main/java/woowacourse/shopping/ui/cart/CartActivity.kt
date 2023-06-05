package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.model.CartProductModel
import woowacourse.shopping.model.OrderModel
import woowacourse.shopping.model.PageModel
import woowacourse.shopping.ui.cart.CartContract.View
import woowacourse.shopping.ui.cart.listener.CartClickListener
import woowacourse.shopping.ui.cart.recyclerview.adapter.CartAdapter
import woowacourse.shopping.ui.order.OrderActivity
import woowacourse.shopping.util.extension.setContentView
import woowacourse.shopping.util.extension.showToast
import woowacourse.shopping.util.inject.injectCartPresenter

class CartActivity : AppCompatActivity(), View, CartClickListener {
    private val presenter: CartPresenter by lazy { injectCartPresenter(this) }
    private lateinit var binding: ActivityCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater).setContentView(this)
        binding.lifecycleOwner = this
        binding.presenter = presenter
        binding.adapter = CartAdapter(this)
        presenter.fetchCart(START_PAGE)
    }

    override fun updateCart(cartProducts: List<CartProductModel>) {
        binding.adapter?.submitList(cartProducts)
    }

    override fun updatePageState(page: PageModel) {
        updateNavigatorEnabled(page.hasPrevious, page.hasNext)
        updatePageNumber(page)
    }

    private fun updateNavigatorEnabled(previousEnabled: Boolean, nextEnabled: Boolean) {
        binding.previousButton.isEnabled = previousEnabled
        binding.nextButton.isEnabled = nextEnabled
    }

    private fun updatePageNumber(page: PageModel) {
        binding.pageNumberTextView.text = page.toString()
    }

    override fun updateTotalPrice(totalPrice: Int) {
        binding.totalPriceTextView.text = getString(R.string.price_format, totalPrice)
    }

    override fun onCountChanged(cartProduct: CartProductModel, changedCount: Int) {
        presenter.updateProductCount(cartProduct, changedCount)
    }

    override fun onCheckStateChanged(cartProduct: CartProductModel, isChecked: Boolean) {
        presenter.updateProductSelectState(cartProduct, isChecked)
    }

    override fun onDeleteClick(cartProduct: CartProductModel) {
        presenter.deleteProduct(cartProduct)
    }

    override fun navigateToOrder(order: OrderModel) {
        startActivity(OrderActivity.getIntent(this, order))
        finish()
    }

    override fun navigateToHome() {
        setResult(RESULT_OK)
        finish()
    }

    override fun showErrorMessage(message: String) {
        showToast(message)
    }

    override fun showCartProductDeleteFailed() {
        showToast(getString(R.string.cart_product_delete_failed))
    }

    override fun showCartCountChangedFailed() {
        showToast(getString(R.string.cart_product_count_change_failed))
    }

    companion object {
        private const val START_PAGE = 1

        fun getIntent(context: Context) = Intent(context, CartActivity::class.java)
    }
}
