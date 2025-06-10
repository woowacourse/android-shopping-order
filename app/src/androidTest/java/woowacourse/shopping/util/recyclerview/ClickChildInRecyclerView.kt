package woowacourse.shopping.util.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import woowacourse.shopping.util.clickOnViewChild

fun clickChildInRecyclerView(
    recyclerViewId: Int,
    position: Int,
    childViewId: Int,
) {
    onView(withId(recyclerViewId))
        .perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                position,
                clickOnViewChild(childViewId),
            ),
        )
    Thread.sleep(50)
}
