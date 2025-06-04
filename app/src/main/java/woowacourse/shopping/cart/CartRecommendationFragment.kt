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
import woowacourse.shopping.databinding.FragmentCartRecommendationBinding
import woowacourse.shopping.product.catalog.CatalogItem.ProductItem
import woowacourse.shopping.product.catalog.ProductActionListener
import woowacourse.shopping.product.catalog.ProductAdapter
import woowacourse.shopping.product.catalog.ProductUiModel
import woowacourse.shopping.product.detail.DetailActivity

class CartRecommendationFragment : Fragment() {
    private lateinit var binding: FragmentCartRecommendationBinding
    private val viewModel: CartViewModel by lazy {
        ViewModelProvider(
            requireActivity(),
            CartViewModelFactory(requireActivity().application as ShoppingApplication),
        )[CartViewModel::class.java]
    }
    private val adapter: ProductAdapter by lazy {
        ProductAdapter(
            products = emptyList(),
            productActionListener =
                object : ProductActionListener {
                    override fun onProductClick(product: ProductUiModel) {
                        val intent = DetailActivity.newIntent(requireContext(), product)
                        startActivity(intent)
                    }
                    override fun onQuantityAddClick(product: ProductUiModel) {
                        viewModel.increaseQuantity(product)
                    }
                },
            quantityControlListener = { event, product -> },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        initViewBinding(inflater, container)
        observeData()
        return binding.root
    }

    private fun initViewBinding(inflater: LayoutInflater, container: ViewGroup?) {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_cart_recommendation,
                container,
                false,
            )
        binding.lifecycleOwner = viewLifecycleOwner
        binding.RecyclerViewCartRecommendation.adapter = adapter
    }

    private fun observeData() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            (binding.RecyclerViewCartRecommendation.adapter as ProductAdapter).setItems(products.map {
                ProductItem(
                    it
                )
            })
        }
    }
}
