package org.navgurukul.chat.features.home.room.detail.timeline.item

import com.airbnb.epoxy.EpoxyModelClass
import org.navgurukul.chat.R

@EpoxyModelClass
abstract class RedactedMessageItem : AbsMessageItem<RedactedMessageItem.Holder>() {

    override fun getDefaultLayout(): Int = R.layout.item_timeline_event_base
    override fun getViewType() = STUB_ID

    override fun shouldShowReactionAtBottom() = false

    class Holder : AbsMessageItem.Holder(STUB_ID)

    companion object {
        private val STUB_ID = R.id.messageContentRedactedStub
    }
}
