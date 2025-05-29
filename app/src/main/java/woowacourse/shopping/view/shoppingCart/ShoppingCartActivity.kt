package woowacourse.shopping.view.shoppingCart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.view.common.QuantityObservable
import woowacourse.shopping.view.common.ResultFrom
import woowacourse.shopping.view.common.showSnackBar

class ShoppingCartActivity :
    AppCompatActivity(),
    ShoppingCartProductAdapter.ShoppingCartListener,
    ShoppingCartClickListener {
    private val viewModel: ShoppingCartViewModel by viewModels()
    private val binding: ActivityShoppingCartBinding by lazy {
        ActivityShoppingCartBinding.inflate(layoutInflater)
    }
    private val shoppingCartProductAdapter by lazy {
        ShoppingCartProductAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shoppingCartRoot)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initDataBinding()
        setupObservers()

        viewModel.updateShoppingCart()

        binding.shoppingCartProducts.apply {
            adapter = shoppingCartProductAdapter
            addOnScrollListener(
                object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(
                        recyclerView: RecyclerView,
                        dx: Int,
                        dy: Int,
                    ) {
                        super.onScrolled(recyclerView, dx, dy)
                        if (!recyclerView.canScrollVertically(1)) {
                            onPlusPage()
                        }
                    }
                },
            )
        }
    }

    private fun initDataBinding() {
        binding.viewModel = this.viewModel
        binding.lifecycleOwner = this@ShoppingCartActivity
        binding.shoppingCartListener = this@ShoppingCartActivity
    }

    private fun setupObservers() {
        viewModel.shoppingCart.observe(this) { shoppingCart: List<ShoppingCartItem> ->
            shoppingCartProductAdapter.submitList(shoppingCart)
        }

        viewModel.event.observe(this) { event: ShoppingCartEvent ->
            @StringRes val messageResourceId: Int =
                when (event) {
                    ShoppingCartEvent.UPDATE_SHOPPING_CART_FAILURE -> R.string.shopping_cart_update_shopping_cart_error_message

                    ShoppingCartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE -> R.string.shopping_cart_remove_shopping_cart_product_error_message

                    ShoppingCartEvent.DECREASE_SHOPPING_CART_PRODUCT_FAILURE -> R.string.products_minus_shopping_cart_product_error_message

                    ShoppingCartEvent.ADD_SHOPPING_CART_PRODUCT_FAILURE -> R.string.product_detail_add_shopping_cart_error_message
                }

            binding.root.showSnackBar(getString(messageResourceId))
        }

        viewModel.isLoading.observe(this) { loading ->
            if (loading) {
                binding.shoppingCartSkeletonLayout.visibility = View.VISIBLE
                binding.shoppingCartSkeletonLayout.startShimmer()
            } else {
                binding.shoppingCartSkeletonLayout.stopShimmer()
                binding.shoppingCartSkeletonLayout.visibility = View.GONE
            }
        }
    }

    override fun onMinusPage() {
        viewModel.minusPage()
    }

    override fun onPlusPage() {
        viewModel.plusPage()
    }

    override fun onRemoveButton(shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem) {
        viewModel.removeShoppingCartProduct(shoppingCartProductItem)
    }

    override fun onProductSelectedButton(
        shoppingCartProductItem: ShoppingCartItem.ShoppingCartProductItem,
        isSelected: Boolean,
    ) {
        viewModel.selectShoppingCartProduct(shoppingCartProductItem, isSelected)
    }

    override fun onPlusShoppingCartClick(quantityObservable: QuantityObservable) {
        viewModel.increaseQuantity(quantityObservable as ShoppingCartItem.ShoppingCartProductItem)
    }

    override fun onMinusShoppingCartClick(quantityObservable: QuantityObservable) {
        viewModel.decreaseQuantity(quantityObservable as ShoppingCartItem.ShoppingCartProductItem)
    }

    override fun onBackButtonClick() {
        val intent =
            Intent().apply {
                putExtra("updateProducts", viewModel.hasUpdatedProducts.value)
            }
        setResult(ResultFrom.SHOPPING_CART_BACK.RESULT_OK, intent)
        finish()
    }

    override fun onAllSelectedButtonClick(isChecked: Boolean) {
        viewModel.selectAllShoppingCartProducts(isChecked)
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, ShoppingCartActivity::class.java)
    }
}
