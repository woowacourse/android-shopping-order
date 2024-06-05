package woowacourse.shopping.ui.cart.cartitem

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import woowacourse.shopping.data.repository.CartRepositoryImpl
import woowacourse.shopping.data.repository.OrderRepositoryImpl
import woowacourse.shopping.data.repository.ProductRepositoryImpl
import woowacourse.shopping.data.local.room.recentproduct.RecentProductDatabase
import woowacourse.shopping.data.repository.RecentProductRepositoryImpl
import woowacourse.shopping.databinding.FragmentCartItemsBinding
import woowacourse.shopping.ui.cart.viewmodel.CartViewModel
import woowacourse.shopping.ui.cart.viewmodel.CartViewModelFactory

class CartItemFragment : Fragment() {
    private var _binding: FragmentCartItemsBinding? = null
    val binding: FragmentCartItemsBinding
        get() = requireNotNull(_binding) { "${this::class.java.simpleName}에서 에러가 발생했습니다." }

    private lateinit var adapter: CartAdapter

    private val viewModel: CartViewModel by activityViewModels {
        CartViewModelFactory(
            ProductRepositoryImpl(),
            CartRepositoryImpl(),
            RecentProductRepositoryImpl.get(RecentProductDatabase.database().recentProductDao()),
            OrderRepositoryImpl(),
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCartItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        observeCartItems()
        setCartAdapter()
        viewModel.isRecommendPage.value = false
    }

    private fun observeCartItems() {
        viewModel.cart.observe(viewLifecycleOwner) {
            adapter.submitList(it.cartItems)
        }
    }

    private fun setCartAdapter() {
        binding.rvCart.itemAnimator = null
        adapter = CartAdapter(viewModel)
        binding.rvCart.adapter = adapter
    }
}
