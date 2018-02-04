package com.phdlabs.sungwon.a8chat_android.structure.camera.fragments.normal

import android.content.Context
import android.util.AttributeSet
import android.view.TextureView

/**
 * Created by paix on 1/2/18.
 * A [TextureView] that can be adjusted to a specified aspect ratio
 * Used for Camera Preview
 */
class AutoFitTextureView : TextureView {

    /**
     * Required chained constructors
     * */
    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)
    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    /**
     * Properties
     * */
    var mRatioWidth: Int = 0
    var mRatioHeight: Int = 0


    /**
     * Sets the aspect ratio for this view. The size of the view will be measured based on the
     * calculated Ratio from the parameters.
     * Note that the actual size of the parameters don't matter, that is, calling this function
     * as setAspectRatio(2,3) && setApsectRatio(4,6) produce the same result.
     * @param width Relative horizontal size
     * @param height Relative vertical size
     * */
    fun setAspectRatio(width: Int, height: Int) {
        if (width < 0 || height < 0) {
            throw IllegalArgumentException("AutoFitTextView size can't be negative")
        }
        mRatioWidth = width
        mRatioHeight = height
        requestLayout()
    }

    /*LifeCycle*/
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = MeasureSpec.getSize(widthMeasureSpec)
        val height = MeasureSpec.getSize(heightMeasureSpec)
        if (0 == mRatioWidth || 0 == mRatioHeight) {
            setMeasuredDimension(width, height)
        } else {
            if (width < height * mRatioWidth / mRatioHeight) {
                //setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
                //setMeasuredDimension(width, height) //Full screen
                /*Switched to match aspect ratio*/
                setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
            } else {
                /*Switched to match aspect ratio*/
                setMeasuredDimension(width, width * mRatioHeight / mRatioWidth)
                //setMeasuredDimension(height * mRatioWidth / mRatioHeight, height)
                //setMeasuredDimension(width, height) //Full screen
            }
        }
    }
}