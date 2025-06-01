package woowacourse.shopping.view.cart

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityShoppingCartBinding
import woowacourse.shopping.view.recommend.RecommendActivity
import woowacourse.shopping.view.showToast

class CartActivity :
    AppCompatActivity(),
    OnCartPaginationListener {
    private val viewModel: CartViewModel by viewModels()
    private val binding: ActivityShoppingCartBinding by lazy {
        ActivityShoppingCartBinding.inflate(layoutInflater)
    }
    private val cartProductAdapter by lazy {
        CartProductAdapter(
            viewModel::removeCartItem,
            this,
            viewModel::select,
            viewModel::unselect,
            viewModel::plusCartItemQuantity,
            viewModel::minusCartItemQuantity,
        )
    }

    private val recommendActivityResultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.loadCartItems()
            }
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
        bindData()
        handleEvents()
    }

    private fun initDataBinding() {
        binding.adapter = cartProductAdapter
        binding.onClickBackButton = {
            setResult(RESULT_OK)
            finish()
        }
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.onOrder = {
            val intent = RecommendActivity.newIntent(this)
            recommendActivityResultLauncher.launch(intent)
        }
    }

    private fun bindData() {
        viewModel.cartItems.observe(this) { shoppingCart: List<CartItemType> ->
            cartProductAdapter.submitList(shoppingCart)
        }
    }

    private fun handleEvents() {
        viewModel.event.observe(this) { event: CartEvent ->
            when (event) {
                CartEvent.LOAD_SHOPPING_CART_FAILURE ->
                    showToast(R.string.shopping_cart_load_shopping_cart_error_message)

                CartEvent.REMOVE_SHOPPING_CART_PRODUCT_FAILURE ->
                    showToast(R.string.shopping_cart_remove_shopping_cart_product_error_message)

                CartEvent.UPDATE_SHOPPING_CART_PRODUCT_SUCCESS -> {
                    setResult(RESULT_OK)
                    finish()
                }

                CartEvent.UPDATE_SHOPPING_CART_PRODUCT_FAILURE -> {
                    showToast(R.string.shopping_cart_update_shopping_cart_error_message)
                    finish()
                }

                CartEvent.PLUS_CART_ITEM_QUANTITY_FAILURE ->
                    showToast(R.string.shopping_cart_update_shopping_cart_quantity_error_message)

                CartEvent.MINUS_CART_ITEM_QUANTITY_FAILURE ->
                    showToast(R.string.shopping_cart_update_shopping_cart_quantity_error_message)
            }
        }

        onBackPressedDispatcher.addCallback {
            setResult(RESULT_OK)
            finish()
        }
    }

    override fun onMinusPage() {
        viewModel.minusPage()
    }

    override fun onPlusPage() {
        viewModel.plusPage()
    }

    companion object {
        fun newIntent(context: Context): Intent = Intent(context, CartActivity::class.java)
    }
}
