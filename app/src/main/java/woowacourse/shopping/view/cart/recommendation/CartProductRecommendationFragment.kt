package woowacourse.shopping.view.cart.recommendation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentCartProductRecommendationBinding
import woowacourse.shopping.view.cart.ShoppingCartViewModel

class CartProductRecommendationFragment(
    private val viewModel: ShoppingCartViewModel,
) : Fragment() {
    private var _binding: FragmentCartProductRecommendationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentCartProductRecommendationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadRecommendedProducts()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
