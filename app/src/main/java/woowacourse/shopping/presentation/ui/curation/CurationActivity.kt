package woowacourse.shopping.presentation.ui.curation

import android.widget.Toast
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCurationBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.ui.EventObserver
import woowacourse.shopping.presentation.ui.UiState
import woowacourse.shopping.presentation.ui.ViewModelFactory

class CurationActivity : BindingActivity<ActivityCurationBinding>() {
    override val layoutResourceId: Int
        get() = R.layout.activity_curation

    private val viewModel: CurationViewModel by viewModels { ViewModelFactory() }

    private val curationAdapter: CurationAdapter by lazy { CurationAdapter(viewModel) }

    override fun initStartView() {
        initTitle()
        initObserver()
    }

    private fun initTitle() {
        title = getString(R.string.curation_title)
    }

    private fun initObserver() {
        binding.rvCurations.adapter = curationAdapter
        binding.curationActionHandler = viewModel
        binding.lifecycleOwner = this

        viewModel.cartProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {}
                is UiState.Success -> {
                    curationAdapter.submitList(it.data)
                }
            }
        }

        viewModel.eventHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is CurationEvent.SuccessOrder -> {
                        Toast.makeText(this, "주문이 성공적으로 진행되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )
    }
}
