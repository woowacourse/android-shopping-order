package woowacourse.shopping.view.cart

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.R
import woowacourse.shopping.data.repository.remote.RemoteShoppingCartRepositoryImpl
import woowacourse.shopping.databinding.FragmentShoppingCartBinding
import woowacourse.shopping.domain.model.cart.CartItem
import woowacourse.shopping.domain.model.cart.CartItemCounter.Companion.DEFAULT_ITEM_COUNT
import woowacourse.shopping.utils.helper.ToastMessageHelper.makeToast
import woowacourse.shopping.view.MainActivityListener
import woowacourse.shopping.view.ViewModelFactory
import woowacourse.shopping.view.cart.adapter.ShoppingCartAdapter
import woowacourse.shopping.view.cart.model.ShoppingCart
import woowacourse.shopping.view.detail.ProductDetailFragment
import woowacourse.shopping.view.recommend.RecommendFragment

class ShoppingCartFragment : Fragment(), OnClickNavigateShoppingCart {
    private var mainActivityListener: MainActivityListener? = null
    private var _binding: FragmentShoppingCartBinding? = null
    val binding: FragmentShoppingCartBinding get() = _binding!!

    private val shoppingCartViewModel: ShoppingCartViewModel by lazy {
        val viewModelFactory =
            ViewModelFactory {
                ShoppingCartViewModel(
                    shoppingCartRepository = RemoteShoppingCartRepositoryImpl(),
                )
            }
        viewModelFactory.create(ShoppingCartViewModel::class.java)
    }
    private lateinit var adapter: ShoppingCartAdapter

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
        _binding = FragmentShoppingCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        observeData()
    }

    private fun initView() {
        binding.lifecycleOwner = viewLifecycleOwner
        binding.vm = shoppingCartViewModel
        binding.onClickNavigateShoppingCart = this
        binding.onClickShoppingCart = shoppingCartViewModel
        adapter =
            ShoppingCartAdapter(
                onClickShoppingCart = shoppingCartViewModel,
                onClickCartItemCounter = shoppingCartViewModel,
                onClickNavigateShoppingCart = this,
            )
        adapter.setShowSkeleton(true)
        shoppingCartViewModel.loadPagingCartItemList()
        binding.rvShoppingCart.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun observeData() {
        shoppingCartViewModel.shoppingCart.cartItems.observe(viewLifecycleOwner) { cartItems ->
            adapter.setShowSkeleton(false)
            updateRecyclerView(cartItems)
        }
        shoppingCartViewModel.shoppingCartEvent.observe(viewLifecycleOwner) { cartState ->
            when (cartState) {
                is ShoppingCartEvent.UpdateProductEvent.Success -> {
                    adapter.updateCartItem(cartState.productId)
                    mainActivityListener?.saveUpdateProduct(
                        cartState.productId,
                        cartState.count,
                    )
                }

                is ShoppingCartEvent.UpdateProductEvent.DELETE -> {
                    mainActivityListener?.saveUpdateProduct(
                        cartState.productId,
                        DEFAULT_ITEM_COUNT,
                    )

                    requireContext().makeToast(
                        getString(
                            R.string.delete_cart_item,
                        ),
                    )
                }
                is ShoppingCartEvent.SendCartItem.Success -> {
                    navigateRecommend(cartState.shoppingCart)
                }

                ShoppingCartEvent.UpdateCheckBox.Success -> {}
            }
            adapter.notifyDataSetChanged()
        }

        shoppingCartViewModel.errorEvent.observe(viewLifecycleOwner) { errorState ->
            requireContext().makeToast(
                errorState.receiveErrorMessage(),
            )
        }

        mainActivityListener?.observeCartItem {
            shoppingCartViewModel.loadPagingCartItemList()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivityListener = null
    }

    override fun clickBack() {
        mainActivityListener?.popFragment()
    }

    override fun clickCartItem(productId: Long) {
        val productFragment =
            ProductDetailFragment().apply {
                arguments = ProductDetailFragment.createBundle(productId)
            }
        mainActivityListener?.changeFragment(productFragment)
    }

    private fun navigateRecommend(checkedShoppingCart: ShoppingCart) {
        val recommendFragment =
            RecommendFragment().apply {
                arguments =
                    RecommendFragment.createBundle(checkedShoppingCart)
            }
        mainActivityListener?.changeFragment(recommendFragment)
    }

    private fun updateRecyclerView(cartItems: List<CartItem>) {
        adapter.updateCartItems(cartItems)
    }
}
