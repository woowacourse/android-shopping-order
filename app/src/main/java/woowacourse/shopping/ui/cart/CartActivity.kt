package woowacourse.shopping.ui.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ConcatAdapter
import woowacourse.shopping.data.datasource.local.AuthInfoLocalDataSourceImpl
import woowacourse.shopping.data.datasource.remote.shoppingcart.ShoppingCartDataSourceImpl
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.databinding.ActivityCartBinding
import woowacourse.shopping.model.CartItemsUIModel
import woowacourse.shopping.model.CartProductUIModel
import woowacourse.shopping.model.CartUIModel
import woowacourse.shopping.model.ProductUIModel
import woowacourse.shopping.ui.cart.contract.CartContract
import woowacourse.shopping.ui.cart.contract.presenter.CartPresenter
import woowacourse.shopping.ui.cart.viewHolder.CartClickListener
import woowacourse.shopping.ui.order.OrderActivity
import woowacourse.shopping.ui.productdetail.ProductDetailActivity

class CartActivity : AppCompatActivity(), CartContract.View, CartClickListener {
    private lateinit var binding: ActivityCartBinding
    private lateinit var presenter: CartContract.Presenter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = CartPresenter(
            CartRepositoryImpl(ShoppingCartDataSourceImpl()),
            this,
            savedInstanceState?.getInt(KEY_OFFSET) ?: 0,
        )
        setToolbar()
        presenter.setUpCarts()
        presenter.onAllCheckboxClick(true)

        binding.allCheckBox.setOnClickListener {
            presenter.onAllCheckboxClick(binding.allCheckBox.isChecked)
        }
        binding.listener = this
    }

    private fun setToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            else -> return false
        }
        return true
    }

    override fun setCarts(products: List<CartProductUIModel>, cartUIModel: CartUIModel) {
        binding.cartRecyclerview.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this,
        )

        val cartAdapter = CartAdapter(
            products.map { it },
            this,
        )

        binding.cartRecyclerview.adapter = ConcatAdapter(
            cartAdapter,
            CartNavigationAdapter(cartUIModel, presenter::pageUp, presenter::pageDown),
        )
    }

    override fun navigateToItemDetail(product: ProductUIModel) {
        startActivity(ProductDetailActivity.from(this, product.id))
    }

    override fun setCartItemsPrice(price: Int) {
        binding.price = price
    }

    override fun setAllCheckbox(isChecked: Boolean) {
        if (binding.allCheckBox.isChecked != isChecked) {
            binding.allCheckBox.isChecked = isChecked
        }
    }

    override fun updateItem(id: Long, count: Int) {
        val adapter = binding.cartRecyclerview.adapter as? ConcatAdapter

        adapter?.adapters?.forEach { innerAdapter ->
            if (innerAdapter is CartAdapter) {
                innerAdapter.updateItem(id, count)
            }
        }
    }

    override fun updateChecked(id: Long, checked: Boolean) {
        val adapter = binding.cartRecyclerview.adapter as? ConcatAdapter

        adapter?.adapters?.forEach { innerAdapter ->
            if (innerAdapter is CartAdapter) {
                innerAdapter.updateChecked(id, checked)
            }
        }
    }

    override fun navigateToOrder(cartItems: CartItemsUIModel) {
        startActivity(OrderActivity.from(this, cartItems))
        finish()
    }

    override fun setAllOrderCount(count: Int) {
        binding.count = count
    }

    override fun onClick(id: Long) {
        presenter.navigateToItemDetail(id)
    }

    override fun onRemove(id: Long) {
        presenter.removeItem(id)
    }

    override fun onCheckChanged(id: Long, isChecked: Boolean) {
        presenter.onCheckChanged(id, isChecked)
    }

    override fun onOrderClick() {
        presenter.navigateToOrder()
    }

    override fun increaseCount(id: Long) {
        presenter.increaseCount(id)
    }

    override fun decreaseCount(id: Long) {
        presenter.decreaseCount(id)
    }

    companion object {
        const val KEY_OFFSET = "KEY_OFFSET"
        fun from(context: Context): Intent {
            return Intent(context, CartActivity::class.java)
        }
    }
}
