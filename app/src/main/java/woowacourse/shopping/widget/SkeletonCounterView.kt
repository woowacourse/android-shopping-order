package woowacourse.shopping.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import woowacourse.shopping.R
import woowacourse.shopping.databinding.ViewCounterBinding
import woowacourse.shopping.model.CartProductModel
import kotlin.properties.Delegates

class SkeletonCounterView : ConstraintLayout {
    private val binding by lazy {
        ViewCounterBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private var minCount: Int = DEFAULT_MIN_COUNT
    private var maxCount: Int = DEFAULT_MAX_COUNT

    private var isCountInitialized = false

    var count: Int by Delegates.observable(INITIAL_COUNT) { _, _, newCount ->
        if (newCount < minCount) count = minCount
        if (isCountInitialized) binding.skeletonLayout.visibility = View.GONE
        isCountInitialized = true
        binding.countTextView.text = newCount.toString()
    }

    init {
        binding.count = count
        binding.counterPlusButton.setOnClickListener { plusCount() }
        binding.counterMinusButton.setOnClickListener { minusCount() }
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initTypedArrayValue(attrs)
    }

    private fun initTypedArrayValue(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SkeletonCounterView)
        count = typedArray.getInt(R.styleable.SkeletonCounterView_count, INITIAL_COUNT)
        minCount = typedArray.getInt(R.styleable.SkeletonCounterView_min_count, DEFAULT_MIN_COUNT)
        maxCount = typedArray.getInt(R.styleable.SkeletonCounterView_max_count, DEFAULT_MAX_COUNT)

        val isUseSkeleton =
            typedArray.getBoolean(R.styleable.SkeletonCounterView_useSkeleton, false)
        binding.skeletonLayout.visibility = if (isUseSkeleton) VISIBLE else GONE
        typedArray.recycle()
    }

    fun setOnPlusClickListener(onPlusClick: (view: SkeletonCounterView, newCount: Int) -> Unit) {
        binding.counterPlusButton.setOnClickListener {
            plusCount()
            onPlusClick(this, count)
        }
    }

    fun setOnMinusClickListener(onMinusClick: (view: SkeletonCounterView, newCount: Int) -> Unit) {
        binding.counterMinusButton.setOnClickListener {
            minusCount()
            onMinusClick(this, count)
        }
    }

    private fun plusCount() {
        if (count < maxCount) ++count
    }

    private fun minusCount() {
        if (count > minCount) --count
    }

    companion object {
        private const val INITIAL_COUNT: Int = 1
        private const val DEFAULT_MIN_COUNT = 1
        private const val DEFAULT_MAX_COUNT = 99
    }

    interface OnCountChangedListener {
        fun onCountChanged(cartProduct: CartProductModel, changedCount: Int)
    }
}
