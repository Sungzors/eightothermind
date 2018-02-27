package com.phdlabs.sungwon.a8chat_android.structure.camera.adapters.cameraRoll

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

/**
 * Created by jpam on 1/19/18.
 */
class SectionRecyclerViewAdapter
(
        val sectionAdapter: SectionAdapter,
        val itemAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /*Properties*/
    private var valid = true

    /*Initializatiion*/
    init {
        /*Observer*/
        itemAdapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {

            override fun onChanged() {
                //super.onChanged()
                toggleValid()
                notifyDataSetChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                //super.onItemRangeChanged(positionStart, itemCount)
                toggleValid()
                notifyItemChanged(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                //super.onItemRangeInserted(positionStart, itemCount)
                toggleValid()
                notifyItemRangeInserted(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                //super.onItemRangeRemoved(positionStart, itemCount)
                toggleValid()
                notifyItemRangeRemoved(positionStart, itemCount)
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            SectionedType.HEADER.value -> sectionAdapter.onCreateViewHolder(parent, viewType)
            else -> itemAdapter.onCreateViewHolder(parent, viewType -1)
        }
    }

    /*SectionRecyclerViewAdapter required methods*/

    override fun getItemCount() = if(valid) itemAdapter.itemCount + sectionAdapter.itemCount else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(isSectionHeaderPosition(position)) {
            sectionAdapter.onBindViewHolder(holder,position)
        } else {
            itemAdapter.onBindViewHolder(holder, sectionedPositionToPosition(position))
        }
    }

    override fun getItemViewType(position: Int) =
            if (isSectionHeaderPosition(position))
                SectionedType.HEADER.value
            else itemAdapter.getItemViewType(sectionedPositionToPosition(position)) + 1

    override fun getItemId(position: Int) =
            if(isSectionHeaderPosition(position))
                (Integer.MAX_VALUE - sectionAdapter.sections.indexOfKey(position)).toLong() else
                itemAdapter.getItemId(sectionedPositionToPosition(position))


    fun sectionedPositionToPosition(sectionedPosition: Int): Int {
        if (isSectionHeaderPosition(sectionedPosition)) {
            return RecyclerView.NO_POSITION
        }
        var offset = 0
        for (i in 0..sectionAdapter.sections.size() - 1) {
            if (sectionAdapter.sections.valueAt(i).sectionedPosition > sectionedPosition) {
                break
            }
            --offset
        }
        return sectionedPosition + offset
    }

    fun isSectionHeaderPosition(position: Int)= sectionAdapter.sections.get(position) != null
    fun toggleValid() = itemAdapter.itemCount > 0

}