package woowacourse.shopping.feature.cart.recommend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import woowacourse.shopping.databinding.FragmentRecommendBinding

class RecommendFragment : Fragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentRecommendBinding? = null
    private val binding get() = _binding!!

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

        setupViews()
        setupBottomBar()
    }

    private fun setupViews() {
        binding.tvRecommendItemsTitle.text = "이런 상품은 어떠세요?"
        binding.tvRecommendItemsDescription.text = "* 최근 본 상품 기반으로 좋아하실 것 같은 상품들을 추천해드려요."

        // Todo (추천 상품 어댑터 설정)
        // binding.rvRecommendItems.adapter = recommendAdapter
    }

    private fun setupBottomBar() {
        binding.bottomBar.orderButton.text = "돌아가기"
        binding.bottomBar.orderButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.bottomBar.checkboxAll.visibility = View.GONE
        binding.bottomBar.tvAll.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
