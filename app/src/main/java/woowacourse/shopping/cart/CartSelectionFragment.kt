package woowacourse.shopping.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.ShoppingApplication
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.product.catalog.QuantityControlListener

class CartSelectionFragment : Fragment() {
    private lateinit var binding: FragmentCartSelectionBinding
    private val viewModel: CartViewModel by viewModels({ requireActivity() }) {
        CartViewModelFactory(requireActivity().application as ShoppingApplication)
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
                onPaginationButtonClick = {},
                onQuantityControl =
                    object : QuantityControlListener {
                        override fun onClick(
                            buttonEvent: ButtonEvent,
                            product: ProductUiModel,
                        ) = viewModel.updateQuantity(buttonEvent, product)

                        override fun onAdd(product: ProductUiModel) = viewModel.addProduct(product)
                    },
                onCheckClick = viewModel::changeProductSelection,
            )
    }

    private fun observeCartViewModel() {
        val cartAdapter = binding.recyclerViewCart.adapter as CartAdapter
        viewModel.cartProducts.observe(viewLifecycleOwner) { products ->
            cartAdapter.setCartItems(products.toList())
        }
        viewModel.updatedItem.observe(viewLifecycleOwner, cartAdapter::setCartItem)
        binding.shimmerFrameLayoutCartProducts.stopShimmer()
        binding.shimmerFrameLayoutCartProducts.visibility = View.GONE
        // viewModel.loadingState.observe(viewLifecycleOwner) { changeShimmerState(it.isLoading) }
    }

    private fun changeShimmerState(isLoading: Boolean) {
        when (isLoading) {
            true -> {
                binding.shimmerFrameLayoutCartProducts.startShimmer()
                binding.shimmerFrameLayoutCartProducts.visibility = View.VISIBLE
            }

            false -> {
                binding.shimmerFrameLayoutCartProducts.stopShimmer()
                binding.shimmerFrameLayoutCartProducts.visibility = View.GONE
            }
        }
    }
}
