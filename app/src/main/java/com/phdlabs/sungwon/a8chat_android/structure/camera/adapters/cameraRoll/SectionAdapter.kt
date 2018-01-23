package com.phdlabs.sungwon.a8chat_android.structure.camera.adapters.cameraRoll

import android.support.v7.widget.RecyclerView
import android.util.SparseArray

/**
 * Created by jpam on 1/19/18.
 * Abstract class represents the base implementation
 * of the CameraRoll section.
 * To use this, it needs to extend the class & bind the
 * data like the [RecyclerView.Adapter]
 *
 */
abstract class SectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /*Properties*/
    var sections:SparseArray<Section> = SparseArray()

    /*Section Adapter required methods*/
    override fun getItemCount(): Int = sections.size()

    /*Sections configuration*/
    fun configSections(sections: List<Section>) {
        this.sections.clear()
        sections.sortedWith(Comparator { lhs, rhs ->
            if(lhs.firstPosition == rhs.firstPosition) 0
            else if (lhs.firstPosition < rhs.firstPosition) -1
            else 1
        })
        var offset = 0
        sections.forEach {
            it.sectionedPosition = it.firstPosition + offset
            this.sections.append(it.sectionedPosition, it)
            ++ offset
        }
    }
}