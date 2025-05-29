package woowacourse.shopping.feature.cart.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import woowacourse.shopping.data.ShoppingDatabase
import woowacourse.shopping.data.carts.repository.CartRemoteDataSourceImpl
import woowacourse.shopping.data.carts.repository.CartRepositoryImpl
import woowacourse.shopping.data.goods.repository.GoodsLocalDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRemoteDataSourceImpl
import woowacourse.shopping.data.goods.repository.GoodsRepositoryImpl
import woowacourse.shopping.databinding.FragmentRecommendBinding
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.feature.cart.adapter.RecommendAdapter
import woowacourse.shopping.feature.QuantityChangeListener
import woowacourse.shopping.feature.cart.CartViewModel
import woowacourse.shopping.feature.cart.CartViewModelFactory

class RecommendFragment : Fragment() {
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!

    private val viewModel: CartViewModel by viewModels {
        CartViewModelFactory(CartRepositoryImpl(CartRemoteDataSourceImpl()),GoodsRepositoryImpl(GoodsRemoteDataSourceImpl(),
            GoodsLocalDataSourceImpl(ShoppingDatabase.getDatabase(requireContext()))
        ))
    }

    val recommendAdapter by lazy {
        RecommendAdapter(
            lifecycleOwner = viewLifecycleOwner,
            quantityChangeListener = object : QuantityChangeListener {
                override fun onIncrease(cartItem: CartItem) {
                    viewModel.addCartItemOrIncreaseQuantity(cartItem)
                }

                override fun onDecrease(cartItem: CartItem) {
                    viewModel.removeCartItemOrDecreaseQuantity(cartItem.copy(quantity = 1))
                }
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 세팅
        binding.rvRecommendItems.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = recommendAdapter
        }

        // ViewModel의 recommendedGoods를 구독
        viewModel.recommendedGoods.observe(viewLifecycleOwner) { goodsList ->
            recommendAdapter.setItems(goodsList)
        }

        // 데이터 로드 요청
        viewModel.loadRecommendedGoods()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}