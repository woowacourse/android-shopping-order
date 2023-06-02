package woowacourse.shopping.cart

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.common.model.CartProductModel
import woowacourse.shopping.common.utils.Toaster
import woowacourse.shopping.data.cart.CartRemoteDataSourceRetrofit
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding

class CartActivity : AppCompatActivity(), CartContract.View {
    private lateinit var binding: ActivityCartBinding
    private lateinit var presenter: CartContract.Presenter
    private lateinit var cartAdapter: CartAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(findViewById(R.id.cart_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.cartProductList.itemAnimator = null

        beforeLoad()
        initCartAdapter()

        setupCartProductAllCheckbox()

        initPresenter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun updateCart(
        cartProducts: List<CartProductModel>,
        currentPage: Int,
        isLastPage: Boolean
    ) {
        cartAdapter.updateCartProducts(cartProducts, currentPage, isLastPage)
        afterLoad()
    }

    override fun updateNavigationVisibility(visibility: Boolean) {
        cartAdapter.updateNavigationVisible(visibility)
    }

    override fun updateCartTotalPrice(price: Int) {
        binding.cartTotalPrice.text = getString(R.string.product_price, price)
    }

    override fun updateCartTotalQuantity(amount: Int) {
        binding.cartOrderButton.text = getString(R.string.order_button, amount)
    }

    override fun setResultForChange() {
        setResult(Activity.RESULT_OK)
    }

    override fun updateCartProduct(cartProduct: CartProductModel) {
        cartAdapter.updateCartProduct(cartProduct)
    }

    override fun updateAllChecked(isAllChecked: Boolean) {
        binding.cartProductAllCheckbox.isChecked = isAllChecked
    }

    override fun notifyLoadFailed() {
        runOnUiThread {
            Toaster.showToast(this, "장바구니 상품을 불러오는데 실패했습니다!")
        }
    }

    private fun initCartAdapter() {
        cartAdapter = CartAdapter(
            onCartItemRemoveButtonClick = { presenter.removeCartProduct(it) },
            onPreviousButtonClick = {
                beforeLoad()
                presenter.goToPreviousPage()
            },
            onNextButtonClick = {
                beforeLoad()
                presenter.goToNextPage()
            },
            onCheckBoxClick = { cartProductModel ->
                presenter.reverseCartProductChecked(cartProductModel)
                presenter.updateAllChecked()
            },
            onMinusAmountButtonClick = {
                presenter.decreaseCartProductQuantity(it)
            },
            onPlusAmountButtonClick = {
                presenter.increaseCartProductQuantity(it)
            }
        )
        binding.cartProductList.adapter = cartAdapter
    }

    private fun setupCartProductAllCheckbox() {
        binding.cartProductAllCheckbox.setOnClickListener {
            presenter.changeAllChecked(binding.cartProductAllCheckbox.isChecked)
        }
    }

    private fun initPresenter() {
        presenter = CartPresenter(
            this,
            cartRepository = CartRepositoryImpl(CartRemoteDataSourceRetrofit()),
            sizePerPage = SIZE_PER_PAGE
        )
    }

    private fun beforeLoad() {
        showSkeleton()
    }

    private fun showSkeleton() {
        binding.cartProductList.visibility = View.GONE
        binding.skeletonCartProductList.visibility = View.VISIBLE
    }

    private fun afterLoad() {
        showCart()
    }

    private fun showCart() {
        binding.skeletonCartProductList.visibility = View.GONE
        binding.cartProductList.visibility = View.VISIBLE
    }

    companion object {
        private const val SIZE_PER_PAGE = 5

        fun createIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
