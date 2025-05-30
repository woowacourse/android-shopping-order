package woowacourse.shopping.cart

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
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
            requireActivity(),
            CartViewModelFactory(requireActivity().application as ShoppingApplication),
        )[CartViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_cart_selection, container, false)
        binding.lifecycleOwner = this
        setCartProductAdapter()
        observeCartViewModel()
        return binding.root
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
                onQuantityControl = viewModel::updateQuantity,
                onCheckClick = viewModel::changeProductSelection,
            )
    }

    private fun observeCartViewModel() {
        viewModel.cartProducts.observe(viewLifecycleOwner) {
            updateCartItems()
            Log.d("test", "Cart Products: $it")
        }
        viewModel.isNextButtonEnabled.observe(viewLifecycleOwner) { updateCartItems() }
        viewModel.isPrevButtonEnabled.observe(viewLifecycleOwner) { updateCartItems() }
        viewModel.page.observe(viewLifecycleOwner) { updateCartItems() }
        viewModel.updatedItem.observe(viewLifecycleOwner) { item ->
            (binding.recyclerViewCart.adapter as CartAdapter).setCartItem(item)
        }
        viewModel.loadingState.observe(viewLifecycleOwner) {
            Log.d("LOADING_STATE", "State : $it")
            when (it.isLoading) {
                true -> {
                    binding.shimmerFrameLayoutCartProducts.startShimmer()
                    binding.shimmerFrameLayoutCartProducts.visibility = View.VISIBLE
                }

                false -> {
                    Log.d("LOADING_STATE", "$it")
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
        Log.d("UPDATE", "$cartItemsWithPaginationBtn")
        (binding.recyclerViewCart.adapter as CartAdapter).setCartItems(cartItemsWithPaginationBtn)
    }
}
