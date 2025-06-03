package woowacourse.shopping.cart

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.FragmentCartRecommendationBinding
import woowacourse.shopping.product.catalog.CatalogItem.ProductItem
import woowacourse.shopping.product.catalog.ProductActionListener
import woowacourse.shopping.product.catalog.ProductAdapter
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.product.catalog.QuantityControlListener
import woowacourse.shopping.product.detail.DetailActivity

class CartRecommendationFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentCartRecommendationBinding? = null
    private val binding get() = _binding!!
    private val cartViewModel: CartViewModel by activityViewModels { CartViewModelFactory() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_cart_recommendation,
                container,
                false,
            )
        binding.lifecycleOwner = viewLifecycleOwner
        setProductAdapter()
        observeCartViewModel()
        return binding.root
    }

    private fun setProductAdapter() {
        val adapter =
            ProductAdapter(
                products = emptyList(),
                productActionListener =
                    object : ProductActionListener {
                        override fun onProductClick(product: ProductUiModel) {
                            val intent = DetailActivity.newIntent(requireContext(), product)
                            startActivity(intent)
                        }
                    },
                quantityControlListener =
                    object : QuantityControlListener {
                        override fun onClick(
                            buttonEvent: ButtonEvent,
                            product: ProductUiModel,
                        ) {
                            cartViewModel.updateQuantity(buttonEvent, product)
                        }

                        override fun onAdd(product: ProductUiModel) {
                            cartViewModel.addProduct(product)
                        }
                    },
            )

        binding.RecyclerViewCartRecommendation.adapter = adapter
    }

    private fun observeCartViewModel() {
        val recommendProductAdapter =
            binding.RecyclerViewCartRecommendation.adapter as ProductAdapter

        cartViewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            recommendProductAdapter.addLoadedItems(products.map { ProductItem(it) })
        }
        cartViewModel.updatedProduct.observe(viewLifecycleOwner) { product ->
            recommendProductAdapter.updateItem(product)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
