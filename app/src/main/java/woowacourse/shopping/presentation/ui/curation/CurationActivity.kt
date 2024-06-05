package woowacourse.shopping.presentation.ui.curation

import android.content.Context
import android.content.Intent
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
    private val orderItemsId: List<Long> by lazy {
        intent.getIntegerArrayListExtra(EXTRA_CART_PRODUCT)?.map { it.toLong() } ?: emptyList()
    }

    private val viewModel: CurationViewModel by viewModels { ViewModelFactory(orderItemsId) }

    private val curationAdapter: CurationAdapter by lazy { CurationAdapter(viewModel) }

    override fun initStartView() {
        title = "Curation"

        binding.rvCurations.adapter = curationAdapter
        binding.curationActionHandler = viewModel

        viewModel.cartProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    curationAdapter.submitList(it.data)
                }
            }
        }
        viewModel.orderProducts.observe(this) {
            when (it) {
                is UiState.Loading -> {
                }
                is UiState.Success -> {
                    binding.tvPrice.text =
                        getString(
                            R.string.won,
                            it.data.sumOf {
                                it.quantity * it.price
                            },
                        )

                    binding.tvOrderCount.text =
                        it.data.sumOf {
                            it.quantity
                        }.toString()
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

    companion object {
        const val EXTRA_CART_PRODUCT = "cartProduct"

        fun createIntent(
            context: Context,
            orderItemsId: List<Int>,
        ): Intent {
            return Intent(context, CurationActivity::class.java).apply {
                putIntegerArrayListExtra(EXTRA_CART_PRODUCT, orderItemsId as ArrayList<Int>)
            }
        }
    }
}
