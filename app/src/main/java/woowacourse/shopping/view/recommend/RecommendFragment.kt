package woowacourse.shopping.view.recommend

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.RecentlyProductRepositoryImpl
import woowacourse.shopping.data.repository.real.OrderRepositoryImpl
import woowacourse.shopping.data.repository.real.RealProductRepositoryImpl
import woowacourse.shopping.data.repository.real.RealShoppingCartRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentlyProduct
import woowacourse.shopping.utils.ShoppingUtils.makeToast
import woowacourse.shopping.utils.exception.NoSuchDataException
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.view.cart.ShoppingCartFragment
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.cartcounter.OnClickCartItemCounter
import woowacourse.shopping.view.detail.ProductDetailFragment
import woowacourse.shopping.view.products.OnClickProducts
import woowacourse.shopping.view.products.ProductsListFragment

class RecommendFragment : Fragment(), OnClickRecommend, OnClickCartItemCounter, OnClickProducts {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentRecommendBinding? = null
    val binding: FragmentRecommendBinding get() = _binding!!
    private val recommendViewModel: RecommendViewModel by lazy {
        val viewModelFactory =
            ViewModelFactory {
                RecommendViewModel(
                    orderRepository = OrderRepositoryImpl(),
                    productRepository = RealProductRepositoryImpl(),
                    shoppingCartRepository = RealShoppingCartRepositoryImpl(),
                    recentlyRepository = RecentlyProductRepositoryImpl(requireContext()),
                )
            }
        viewModelFactory.create(RecommendViewModel::class.java)
    }

    private lateinit var adapter: RecommendAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivityListener) {
            mainActivityListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = recommendViewModel
        binding.onClickRecommend = this
        recommendViewModel.loadRecommendData()
        adapter =
            RecommendAdapter(
                onClickProducts = this,
                onClickCartItemCounter = this,
            )
        binding.recyclerView.adapter = adapter
        observeData()
        loadCheckedShoppingCart()
    }


    private fun observeData() {
        recommendViewModel.products.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)
        }
        recommendViewModel.recommendEvent.observe(viewLifecycleOwner) { state ->
            when (state) {
                is RecommendEvent.UpdateProductEvent.Success -> {
                    mainActivityListener?.apply {
                        saveUpdateProduct(
                            productId = state.product.id,
                            count = state.product.cartItemCounter.itemCount,
                        )
                        saveUpdateCartItem()
                    }
                }

                RecommendEvent.OrderRecommends.Success -> navigateToProduct()
            }
        }
        recommendViewModel.errorEvent.observe(viewLifecycleOwner) {
            requireContext().makeToast(
                getString(R.string.error_default),
            )
        }
    }

    override fun clickIncrease(product: Product) {
        recommendViewModel.increaseShoppingCart(product)
    }

    override fun clickDecrease(product: Product) {
        recommendViewModel.decreaseShoppingCart(product)
    }

    override fun clickLoadPagingData() {}

    override fun clickProductItem(productId: Long) {
        val productFragment =
            ProductDetailFragment().apply {
                arguments = ProductDetailFragment.createBundle(productId)
            }
        mainActivityListener?.changeFragment(productFragment)
    }

    override fun clickRecentlyItem(recentlyProduct: RecentlyProduct) {}

    override fun clickShoppingCart() {
        val shoppingCartFragment = ShoppingCartFragment()
        mainActivityListener?.changeFragment(shoppingCartFragment)
    }

    override fun clickOrder() {
        recommendViewModel.orderItems()
    }

    override fun clickBack() {
        mainActivityListener?.popFragment()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivityListener = null
    }

    private fun receiveCheckedShoppingCart(): ShoppingCart {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getSerializable(CHECKED_SHOPPING_CART, ShoppingCart::class.java)
                ?: throw NoSuchDataException()
        } else {
            arguments?.getSerializable(CHECKED_SHOPPING_CART) as? ShoppingCart
                ?: throw NoSuchDataException()
        }
    }

    private fun navigateToProduct() {
        mainActivityListener?.popAllFragment()
        val productFragment =
            ProductsListFragment()
        mainActivityListener?.changeFragment(productFragment)
    }

    private fun loadCheckedShoppingCart() {
        try {
            val shoppingCart = receiveCheckedShoppingCart()
            recommendViewModel.saveCheckedShoppingCarts(shoppingCart)
        } catch (e: Exception) {
            requireContext().makeToast(
                getString(R.string.error_data_load),
            )
            clickBack()
        }
    }

    companion object {
        fun createBundle(
            checkedShoppingCart: ShoppingCart,
        ): Bundle {
            return Bundle().apply {
                putSerializable(CHECKED_SHOPPING_CART, checkedShoppingCart)
            }
        }

        private const val CHECKED_SHOPPING_CART = "checkedShoppingCart"
    }
}
