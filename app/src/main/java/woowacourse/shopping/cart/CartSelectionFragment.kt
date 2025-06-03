package woowacourse.shopping.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.cart.CartItem.ProductItem
import woowacourse.shopping.databinding.FragmentCartSelectionBinding
import woowacourse.shopping.domain.LoadingState
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.product.catalog.QuantityControlListener

class CartSelectionFragment : Fragment() {
    private lateinit var binding: FragmentCartSelectionBinding
    private val viewModel: CartViewModel by viewModels({ requireActivity() })

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
                onDeleteProductClick =
                    DeleteProductClickListener { product ->
                        viewModel.deleteCartProduct(ProductItem(product))
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
            cartAdapter.submitList(products.toList())
        }
        viewModel.loadingState.observe(viewLifecycleOwner) { changeShimmerState(it) }
    }

    private fun changeShimmerState(state: LoadingState) {
        binding.shimmerFrameLayoutCartProducts.visibility = state.shimmerVisibility
        binding.recyclerViewCart.visibility = state.recyclerViewVisibility

        if (state.shimmerVisibility == View.VISIBLE) {
            binding.shimmerFrameLayoutCartProducts.startShimmer()
        } else {
            binding.shimmerFrameLayoutCartProducts.stopShimmer()
        }
    }
}
