package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.isVisible
import woowacourse.shopping.R
import woowacourse.shopping.data.cart.CartItemRemoteService
import woowacourse.shopping.data.cart.CartItemRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.ui.cart.adapter.CartListAdapter
import woowacourse.shopping.ui.cart.uistate.CartItemUIState
import woowacourse.shopping.utils.ServerConfiguration

class CartActivity : AppCompatActivity(), CartContract.View {
    private val binding: ActivityCartBinding by lazy {
        ActivityCartBinding.inflate(layoutInflater)
    }

    private val cartListAdapter by lazy {
        CartListAdapter(
            cartListEvent = makeCartListEvent()
        )
    }

    private val presenter: CartContract.Presenter by lazy {
        CartPresenter(
            this,
            CartItemRepositoryImpl(
                CartItemRemoteService(ServerConfiguration.host)
            ),
            PAGE_SIZE
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setActionBar()

        initPageUI()
        initOrderUI()
        initCartList()
        loadLastPageIfFromCartItemAdd()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun setStateThatCanRequestPreviousPage(canRequest: Boolean) {
        runOnUiThread {
            binding.btnPageDown.isEnabled = canRequest
        }
    }

    override fun setStateThatCanRequestNextPage(canRequest: Boolean) {
        runOnUiThread {
            binding.btnPageUp.isEnabled = canRequest
        }
    }

    override fun setStateThatCanRequestPage(canRequest: Boolean) {
        runOnUiThread {
            binding.tvCartPage.isVisible = canRequest
            binding.btnPageUp.isVisible = canRequest
            binding.btnPageDown.isVisible = canRequest
        }
    }

    override fun setPage(page: Int) {
        runOnUiThread {
            binding.tvCartPage.text = page.toString()
        }
    }

    override fun setCartItems(cartItems: List<CartItemUIState>, initScroll: Boolean) {
        runOnUiThread {
            binding.layoutSkeletonCartList.isVisible = false
            binding.layoutCartList.isVisible = true
            if (initScroll) binding.recyclerViewCart.smoothScrollToPosition(0)
            cartListAdapter.setCartItems(cartItems)
        }
    }

    override fun setStateOfAllSelection(isAllSelected: Boolean) {
        runOnUiThread {
            binding.cbPageAllSelect.isChecked = isAllSelected
        }
    }

    override fun setOrderPrice(price: Int) {
        runOnUiThread {
            binding.tvOrderPrice.text = getString(R.string.product_price).format(price)
        }
    }

    override fun setOrderCount(count: Int) {
        runOnUiThread {
            binding.tvOrder.text = getString(R.string.order_with_count).format(count)
        }
    }

    private fun loadLastPageIfFromCartItemAdd() {
        if (intent.getBooleanExtra(JUST_ADDED_CART_ITEM, false)) {
            presenter.loadCartItemsOfLastPage()
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
            presenter.loadCartItemsOfPreviousPage()
        }
        binding.btnPageUp.setOnClickListener {
            presenter.loadCartItemsOfNextPage()
        }
    }

    private fun initCartList() {
        binding.recyclerViewCart.adapter = cartListAdapter
        presenter.loadCartItemsOfNextPage()
    }

    private fun initOrderUI() {
        binding.cbPageAllSelect.isChecked = false
        binding.cbPageAllSelect.setOnCheckedChangeListener { _, isChecked ->
            presenter.updateSelectionTotalCartItems(isChecked)
        }
        binding.tvOrder.text = getString(R.string.order)
    }

    private fun makeCartListEvent() = object : CartListEvent {
        override fun onClickCloseButton(id: Long) {
            presenter.deleteCartItem(id)
        }

        override fun onClickCheckBox(id: Long, isChecked: Boolean) {
            presenter.updateSelectionCartItem(id, isChecked)
        }

        override fun onClickPlus(id: Long) {
            presenter.plusCount(id)
        }

        override fun onClickMinus(id: Long) {
            presenter.minusCount(id)
        }
    }

    companion object {
        private const val PAGE_SIZE = 5
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
