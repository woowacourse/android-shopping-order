package woowacourse.shopping.cart.selection

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import woowacourse.shopping.R
import woowacourse.shopping.cart.CartAdapter
import woowacourse.shopping.cart.CartItem
import woowacourse.shopping.cart.CartViewModel
import woowacourse.shopping.cart.CartViewModelFactory
import woowacourse.shopping.cart.DeleteProductClickListener
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.product.catalog.ProductUiModel

class SelectionFragment : Fragment() {
    private lateinit var binding: FragmentCartSelectionBinding
    private val viewModel: CartViewModel by lazy {
        ViewModelProvider(
            this,
            CartViewModelFactory(),
        )[CartViewModel::class.java]
    }
    private val adapter: CartAdapter by lazy {
        CartAdapter(
            cartItems = emptyList(),
            onDeleteProductClick =
                DeleteProductClickListener { product ->
                    viewModel.deleteCartProduct(CartItem.ProductItem(product))
                },
            onPaginationButtonClick = viewModel::onPaginationButtonClick,
            quantityControlListener = viewModel::updateQuantity,
            onCheckClick = viewModel::changeProductSelection,
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_cart_selection, container, false)
        binding.apply {
            lifecycleOwner = this@SelectionFragment
            recyclerViewCart.adapter = adapter
            vm = viewModel
        }
        observeCartViewModel()
        return binding.root
    }

    private fun observeCartViewModel() {
        viewModel.apply {
            cartProducts.observe(viewLifecycleOwner) {
                updateCartItems()
            }
            isNextButtonEnabled.observe(viewLifecycleOwner) { updateCartItems() }
            isPrevButtonEnabled.observe(viewLifecycleOwner) { updateCartItems() }
            page.observe(viewLifecycleOwner) { updateCartItems() }
            updatedItem.observe(viewLifecycleOwner) { item ->
                (binding.recyclerViewCart.adapter as CartAdapter).setCartItem(item)
            }
        }
    }

    private fun updateCartItems() {
        val products: List<ProductUiModel> = viewModel.cartProducts.value ?: return
        val isNext: Boolean = viewModel.isNextButtonEnabled.value == true
        val isPrev: Boolean = viewModel.isPrevButtonEnabled.value == true
        val page: Int = viewModel.page.value ?: 0
        val paginationItem = CartItem.PaginationButtonItem(page + 1, isNext, isPrev)
        val cartItems: List<CartItem> = products.map { CartItem.ProductItem(it) }
        val cartItemsWithPaginationBtn =
            if (cartItems.isEmpty()) cartItems else cartItems + paginationItem
        (binding.recyclerViewCart.adapter as CartAdapter).setCartItems(cartItemsWithPaginationBtn)
    }
}
