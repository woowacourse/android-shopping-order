package woowacourse.shopping.presentation.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import woowacourse.shopping.R
import woowacourse.shopping.data.HttpErrorHandler
import woowacourse.shopping.data.cart.CartRepositoryImpl
import woowacourse.shopping.data.cart.CartServiceHelper
import woowacourse.shopping.data.common.PreferenceUtil
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.presentation.order.OrderActivity

class CartActivity : AppCompatActivity(), CartContract.View {
    private lateinit var binding: ActivityCartBinding
    private lateinit var cartAdapter: CartAdapter
    private lateinit var cartProductPriceView: TextView
    private val httpErrorHandler = HttpErrorHandler(this)
    private val presenter: CartContract.Presenter by lazy { initPresenter() }

    private fun initPresenter(): CartContract.Presenter {
        return CartPresenter(
            this,
            CartRepositoryImpl(CartServiceHelper(PreferenceUtil(this)), httpErrorHandler),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpBinding()
        managePaging()
        initView()
    }

    override fun onStart() {
        super.onStart()
        setLoadingViewVisible(true)
        presenter.loadCartItems()
    }

    private fun setUpBinding() {
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initView() {
        initCartAdapter()
        setToolBar()
        allOrderedCheckBoxChange()
        orderButtonClick()
    }

    private fun initCartAdapter() {
        cartAdapter = CartAdapter(
            presenter = presenter,
            updateProductPrice = ::updateProductPrice,
        )
        binding.recyclerCart.adapter = cartAdapter
    }

    private fun updateProductPrice(textView: TextView, cartProductInfoModel: CartProductInfoModel) {
        cartProductPriceView = textView
        presenter.updateProductPrice(cartProductInfoModel)
    }

    private fun setToolBar() {
        setSupportActionBar(binding.toolbarCart.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.arrow_back_24)
    }

    private fun managePaging() {
        onPlusPage()
        onMinusPage()
    }

    private fun onPlusPage() {
        binding.buttonPlusPage.setOnClickListener {
            presenter.plusPage()
            onPageChanged()
        }
    }

    private fun onPageChanged() {
        with(presenter) {
            refreshCurrentPageItems()
            checkPlusPageAble()
            checkMinusPageAble()
            checkCurrentPageProductsIsOrdered()
        }
    }

    private fun onMinusPage() {
        binding.buttonMinusPage.setOnClickListener {
            presenter.minusPage()
            onPageChanged()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) { // 뒤로가기
            android.R.id.home -> {
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun allOrderedCheckBoxChange() {
        binding.checkboxAllCart.setOnCheckedChangeListener { _, isChecked ->
            presenter.changeCurrentPageProductsOrder(isChecked)
            presenter.updateOrderPrice()
            presenter.updateOrderCount()
        }
    }

    private fun orderButtonClick() {
        binding.buttonCartOrder.setOnClickListener {
            presenter.order()
        }
    }
    override fun setCartItems(productModels: List<CartProductInfoModel>) {
        cartAdapter.submitList(productModels)
    }

    override fun setUpPlusPageState(isEnable: Boolean) {
        binding.buttonPlusPage.isClickable = isEnable
        if (isEnable) {
            binding.buttonPlusPage.setImageResource(R.drawable.icon_right_page_true)
        } else {
            binding.buttonPlusPage.setImageResource(R.drawable.icon_right_page_false)
        }
    }

    override fun setUpMinusPageState(isEnable: Boolean) {
        binding.buttonMinusPage.isClickable = isEnable
        if (isEnable) {
            binding.buttonMinusPage.setImageResource(R.drawable.icon_left_page_true)
        } else {
            binding.buttonMinusPage.setImageResource(R.drawable.icon_left_page_false)
        }
    }

    override fun setAllIsOrderCheck(isAllOrdered: Boolean) {
        binding.checkboxAllCart.isChecked = isAllOrdered
    }
    override fun setProductPrice(price: Int) {
        cartProductPriceView.text = getString(R.string.price_format, price)
    }

    override fun setPage(page: String) {
        binding.textCartPage.text = page
    }

    override fun setLoadingViewVisible(isVisible: Boolean) {
        if (isVisible) {
            binding.containerCart.visibility = View.GONE
            binding.flCartList.visibility = View.VISIBLE
        } else {
            binding.containerCart.visibility = View.VISIBLE
            binding.flCartList.visibility = View.GONE
        }
    }
    override fun setOrderPrice(totalPrice: Int) {
        binding.textCartPrice.text = getString(R.string.price_format, totalPrice)
    }

    override fun setOrderCount(count: Int) {
        binding.buttonCartOrder.text = getString(R.string.order_format, count)
    }

    override fun showOrderView(orderProductsModel: OrderProductsModel) {
        if (orderProductsModel.list.isEmpty()) Toast.makeText(
            this,
            getString(R.string.no_order_in_cart_message),
            Toast.LENGTH_SHORT
        ).show()
        else startActivity(OrderActivity.getIntent(this, orderProductsModel))
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
