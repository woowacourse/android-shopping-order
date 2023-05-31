package woowacourse.shopping.feature.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.appcompat.app.AppCompatActivity
import com.example.domain.repository.CartRepository
import woowacourse.shopping.R
import woowacourse.shopping.ServerType
import woowacourse.shopping.data.cart.CartRemoteRepository
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.model.CartProductState
import woowacourse.shopping.util.extension.formatPriceWon

class CartActivity : AppCompatActivity(), CartContract.View {
    private var _binding: ActivityCartBinding? = null
    private val binding: ActivityCartBinding
        get() = _binding!!

    private val url by lazy { intent.getStringExtra(ServerType.INTENT_KEY) ?: "" }
    private val presenter: CartContract.Presenter by lazy {
        val cartRepo: CartRepository = CartRemoteRepository(url = url)
        CartPresenter(this, cartRepo)
    }
    private val adapter: CartProductListAdapter by lazy {
        CartProductListAdapter(
            onCartProductDeleteClick = presenter::deleteCartProduct,
            plusQuantity = { state -> presenter.plusQuantity(state) },
            minusQuantity = { state -> presenter.minusQuantity(state) },
            updateChecked = { productId: Long, checked: Boolean ->
                presenter.updateChecked(productId, checked)
                presenter.loadCheckedCartProductCount()
            }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setToolBarBackButton()

        binding.cartProductRv.adapter = adapter
        binding.pageNumberPlusTv.setOnClickListener { presenter.plusPageNumber() }
        binding.pageNumberMinusTv.setOnClickListener { presenter.minusPageNumber() }
        binding.allCheckBox.setOnCheckedChangeListener { compoundButton, b ->
            presenter.checkAll()
            presenter.loadCart()
            presenter.loadCheckedCartProductCount()
        }

        presenter.loadCart()
        presenter.loadCheckedCartProductCount()
    }

    override fun updateItem(newItem: CartProductState) {
        adapter.updateItem(newItem)
    }

    override fun setCartProducts(cartProducts: List<CartProductState>) {
        runOnUiThread { adapter.setItems(cartProducts) }
    }

    override fun setCartPageNumber(number: Int) {
        runOnUiThread { binding.pageNumberTv.text = number.toString() }
    }

    override fun setCartPageNumberPlusEnable(isEnable: Boolean) {
        if (isEnable) {
            binding.pageNumberPlusTv.setBackgroundColor(getColor(R.color.teal_200))
        } else {
            binding.pageNumberPlusTv.setBackgroundColor(getColor(R.color.light_gray))
        }
    }

    override fun setCartPageNumberMinusEnable(isEnable: Boolean) {
        if (isEnable) {
            binding.pageNumberMinusTv.setBackgroundColor(getColor(R.color.teal_200))
        } else {
            binding.pageNumberMinusTv.setBackgroundColor(getColor(R.color.light_gray))
        }
    }

    override fun setCartProductCount(count: Int) {
        runOnUiThread {
            binding.orderBtn.text = getString(R.string.cart_order_btn_text).format(count)
        }
    }

    override fun setTotalCost(paymentAmount: Int) {
        runOnUiThread { binding.totalCostTv.formatPriceWon(paymentAmount) }
    }

    override fun showPageSelectorView() {
        binding.pageSelectorView.visibility = VISIBLE
    }

    override fun hidePageSelectorView() {
        binding.pageSelectorView.visibility = GONE
    }

    override fun showCartProducts() {
        runOnUiThread { binding.cartProductRv.visibility = VISIBLE }
        runOnUiThread { binding.skeletonLl.visibility = GONE }
    }

    private fun setToolBarBackButton() {
        setSupportActionBar(binding.cartTb)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    companion object {
        fun startActivity(context: Context, serverUrl: String) {
            val intent = Intent(context, CartActivity::class.java)
            intent.putExtra(ServerType.INTENT_KEY, serverUrl)
            context.startActivity(intent)
        }
    }
}
