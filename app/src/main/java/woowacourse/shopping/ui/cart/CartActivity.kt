package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import woowacourse.shopping.AppContainer
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingOrderApplication
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.cart.adapter.CartListAdapter
import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.ui.cart.uistate.OrderPriceInfoUIState
import woowacourse.shopping.ui.order.OrderResultActivity
import woowacourse.shopping.utils.PRICE_FORMAT
import woowacourse.shopping.utils.showToast

class CartActivity : AppCompatActivity(), CartContract.View {
    private val appContainer by lazy {
        val application = (application as ShoppingOrderApplication)
        if (application.appContainer == null) application.appContainer = AppContainer()
        application.appContainer ?: AppContainer()
    }

    private val binding: ActivityCartBinding by lazy {
        ActivityCartBinding.inflate(layoutInflater)
    }

    private val presenter: CartContract.Presenter by lazy {
        CartPresenter(
            this,
            appContainer.cartItemRepository,
            appContainer.orderRepository,
            appContainer.discountInfoRepository
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initPageUI()
        initOrderUI()
        initCartList()
        if (intent.getBooleanExtra(JUST_ADDED_CART_ITEM, false)) {
            presenter.onLoadCartItemsOfLastPage()
        } else {
            presenter.onLoadCartItemsOfFirstPage()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun setCartItems(cartItems: List<CartItemUIState>, initScroll: Boolean) {
        runOnUiThread {
            if (binding.layoutSkeletonCartList.isVisible) {
                binding.layoutSkeletonCartList.isVisible = false
                binding.layoutCartList.isVisible = true
                setOrderPrice(0)
                setOrderCount(0)
            }

            if (initScroll) binding.recyclerViewCart.smoothScrollToPosition(0)
            (binding.recyclerViewCart.adapter as CartListAdapter).setCartItems(cartItems)
        }
    }

    override fun setStateThatCanRequestNextPage(canRequest: Boolean) {
        runOnUiThread {
            binding.btnPageUp.isEnabled = canRequest
        }
    }

    override fun setPageUIVisibility(isVisible: Boolean) {
        runOnUiThread {
            binding.tvCartPage.isVisible = isVisible
            binding.btnPageUp.isVisible = isVisible
            binding.btnPageDown.isVisible = isVisible
        }
    }

    override fun setStateThatCanRequestPreviousPage(canRequest: Boolean) {
        runOnUiThread {
            binding.btnPageDown.isEnabled = canRequest
        }
    }

    override fun setPage(page: Int) {
        runOnUiThread {
            binding.tvCartPage.text = page.toString()
        }
    }

    override fun setStateOfAllSelection(isAllSelected: Boolean) {
        runOnUiThread {
            binding.cbPageAllSelect.setOnCheckedChangeListener { _, _ -> }
            binding.cbPageAllSelect.isChecked = isAllSelected
            binding.cbPageAllSelect.setOnCheckedChangeListener { _, isChecked ->
                presenter.onChangeSelectionOfAllCartItems(isChecked)
            }
        }
    }

    override fun setOrderPrice(price: Int) {
        runOnUiThread {
            binding.tvOrderPrice.text =
                getString(R.string.format_price).format(PRICE_FORMAT.format(price))
        }
    }

    override fun setOrderCount(count: Int) {
        runOnUiThread {
            binding.tvOrder.text = getString(R.string.order_with_count).format(count)
        }
    }

    override fun setCanOrder(canOrder: Boolean) {
        runOnUiThread {
            binding.tvOrder.isEnabled = canOrder
        }
    }

    override fun showOrderResult(orderId: Long) {
        runOnUiThread {
            OrderResultActivity.startActivity(this, orderId)
        }
    }

    override fun setCanSeeOrderPriceInfo(canSeeOrderPriceInfo: Boolean) {
        runOnUiThread {
            binding.btnOrderPriceInfo.isVisible = canSeeOrderPriceInfo
        }
    }

    override fun showOrderPriceInfo(orderPriceInfo: OrderPriceInfoUIState) {
        runOnUiThread {
            AlertDialog.Builder(this).apply {
                setTitle("할인 정보")
                setMessage(
                    orderPriceInfo.discountResult.joinToString(
                        separator = "\n\n",
                        postfix = "\n\n"
                    ) {
                        """
                        할인 정책: ${it.policyName}
                        할인율: ${it.discountRate}
                        할인 금액: ${it.discountPrice}
                        """.trimIndent()
                    } + """
                        원래 금액: ${orderPriceInfo.originalPrice}
                        주문 금액: ${orderPriceInfo.orderPrice}
                    """.trimIndent()
                )
            }
                .create()
                .show()
        }
    }

    override fun showMessage(message: String) {
        runOnUiThread {
            showToast(this, message)
        }
    }

    override fun refresh() {
        runOnUiThread {
            presenter.onLoadCartItemsOfFirstPage()
        }
    }

    private fun setActionBar() {
        setSupportActionBar(binding.toolbarCart)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navigationIcon = binding.toolbarCart.navigationIcon?.mutate()
        DrawableCompat.setTint(
            navigationIcon!!,
            ContextCompat.getColor(this, android.R.color.white),
        )
        binding.toolbarCart.navigationIcon = navigationIcon
    }

    private fun initPageUI() {
        binding.btnPageDown.setOnClickListener {
            presenter.onLoadCartItemsOfPreviousPage()
        }
        binding.btnPageUp.setOnClickListener {
            presenter.onLoadCartItemsOfNextPage()
        }
    }

    private fun initCartList() {
        binding.recyclerViewCart.adapter = CartListAdapter(
            onClickCloseButton = { presenter.onDeleteCartItem(it) },
            onClickCheckBox = { productId, isSelected ->
                presenter.onChangeSelectionOfCartItem(productId, isSelected)
            },
            onClickPlus = { presenter.onPlusCount(it) },
            onClickMinus = { presenter.onMinusCount(it) }
        )
        binding.recyclerViewCart.itemAnimator = null
    }

    private fun initOrderUI() {
        binding.cbPageAllSelect.isChecked = false
        binding.cbPageAllSelect.setOnCheckedChangeListener { _, isChecked ->
            presenter.onChangeSelectionOfAllCartItems(isChecked)
        }
        binding.btnOrderPriceInfo.setOnClickListener {
            presenter.onLoadOrderPriceInfo()
        }
        binding.tvOrder.text = getString(R.string.order)
        binding.tvOrder.setOnClickListener {
            presenter.onOrderSelectedCartItems()
        }
    }

    companion object {
        private const val JUST_ADDED_CART_ITEM = "JUST_ADDED_CART_ITEM"

        fun startActivity(context: Context, justAddedCartItem: Boolean = false) {
            Intent(context, CartActivity::class.java).apply {
                putExtra(JUST_ADDED_CART_ITEM, justAddedCartItem)
            }.run {
                context.startActivity(this)
            }
        }
    }
}
