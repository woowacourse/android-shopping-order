package woowacourse.shopping.cart

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.cart.CartItem.PaginationButtonItem
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.product.catalog.ProductUiModel

class CartSelectionFragment : Fragment() {
    private lateinit var binding: FragmentCartSelectionBinding
    private val viewModel: CartViewModel by lazy {
        ViewModelProvider(
            this,
            CartViewModelFactory(requireActivity().application as ShoppingApplication),
        )[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCartSelectionBinding.inflate(layoutInflater)
        binding.lifecycleOwner = this
        setCartProductAdapter()
        observeCartViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_cart_selection, container, false)
    }

    private fun setCartProductAdapter() {
        binding.recyclerViewCart.adapter =
            CartAdapter(
                cartItems = emptyList(),
                onDeleteProductClick =
                    DeleteProductClickListener { product ->
                        viewModel.deleteCartProduct(CartItem.ProductItem(product))
                    },
                onPaginationButtonClick = viewModel::onPaginationButtonClick,
                quantityControlListener = viewModel::updateQuantity,
            )
    }

    private fun observeCartViewModel() {
        viewModel.cartProducts.observe(this) { updateCartItems()
        Log.d("test", "Cart Products: $it")
        }
        viewModel.isNextButtonEnabled.observe(this) { updateCartItems() }
        viewModel.isPrevButtonEnabled.observe(this) { updateCartItems() }
        viewModel.page.observe(this) { updateCartItems() }
        viewModel.updatedItem.observe(this) { item ->
            (binding.recyclerViewCart.adapter as CartAdapter).setCartItem(item)
        }
        viewModel.loadingState.observe(this) {
            Log.d("LOADING_STATE", "State : $it")
            when (it.isLoading) {
                true -> {
                    binding.shimmerFrameLayoutCartProducts.startShimmer()
                    binding.shimmerFrameLayoutCartProducts.visibility = View.VISIBLE
                }

                false -> {
                    Log.d("LOADING_STATE", "${it}")
                    binding.shimmerFrameLayoutCartProducts.stopShimmer()
                    binding.shimmerFrameLayoutCartProducts.visibility = View.GONE
                }
            }
        }
    }

    private fun updateCartItems() {
        val products: List<ProductUiModel> = viewModel.cartProducts.value ?: return
        val isNext: Boolean = viewModel.isNextButtonEnabled.value == true
        val isPrev: Boolean = viewModel.isPrevButtonEnabled.value == true
        val page: Int = viewModel.page.value ?: 0
        val paginationItem = PaginationButtonItem(page + 1, isNext, isPrev)

        val cartItems: List<CartItem> = products.map { CartItem.ProductItem(it) }
        val cartItemsWithPaginationBtn =
            if (cartItems.isEmpty()) cartItems else cartItems + paginationItem
        (binding.recyclerViewCart.adapter as CartAdapter).setCartItems(cartItemsWithPaginationBtn)
    }
}
