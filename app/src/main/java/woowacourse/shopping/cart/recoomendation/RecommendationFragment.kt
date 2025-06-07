package woowacourse.shopping.cart.recoomendation

import android.os.Bundle
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

class RecommendationFragment : Fragment() {
    private lateinit var binding: FragmentCartRecommendationBinding
    private val viewModel: RecommendationFragmentViewModel by lazy {
        ViewModelProvider(
            this,
            RecommendationFragmentViewModelFactory(requireActivity().application as ShoppingApplication),
        )[RecommendationFragmentViewModel::class.java]
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
            quantityControlListener = { event, product ->
                if (event == 1) {
                    viewModel.increaseQuantity(product)
                } else {
                    viewModel.decreaseQuantity(product)
                }
            },
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        initViewBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun initViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ) {
        binding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_cart_recommendation,
                container,
                false,
            )
        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            recyclerViewCartRecommendation.adapter = adapter
            vm = viewModel
        }
    }

    private fun observeData() {
        viewModel.recommendedProducts.observe(viewLifecycleOwner) { products ->
            (binding.recyclerViewCartRecommendation.adapter as ProductAdapter).setItems(
                products.map {
                    ProductItem(
                        it,
                    )
                },
            )
        }
        viewModel.updatedItem.observe(viewLifecycleOwner) { product ->
            if (product != null) {
                (binding.recyclerViewCartRecommendation.adapter as ProductAdapter).updateItem(
                    product,
                )
            }
        }
    }
}
