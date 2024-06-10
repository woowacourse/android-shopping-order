package woowacourse.shopping.presentation.ui.curation

import android.widget.Toast
import androidx.activity.viewModels
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ActivityCurationBinding
import woowacourse.shopping.presentation.base.BindingActivity
import woowacourse.shopping.presentation.base.ViewModelFactory
import woowacourse.shopping.presentation.common.EventObserver
import woowacourse.shopping.presentation.ui.curation.model.CurationNavigation
import woowacourse.shopping.presentation.ui.payment.PaymentActivity

class CurationActivity : BindingActivity<ActivityCurationBinding>(R.layout.activity_curation) {
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
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.uiState.observe(this) {
            curationAdapter.submitList(it.cartProducts)
        }

        viewModel.navigateHandler.observe(
            this,
            EventObserver {
                when (it) {
                    is CurationNavigation.ToPayment -> {
                        startActivity(PaymentActivity.createIntent(this, it.paymentUiModel))
                    }
                }
            },
        )
    }
}
