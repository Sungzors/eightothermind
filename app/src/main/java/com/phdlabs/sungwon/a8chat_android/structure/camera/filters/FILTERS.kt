package com.phdlabs.sungwon.a8chat_android.structure.camera.filters

import android.graphics.Bitmap
import net.alhazmy13.imagefilter.ImageFilter

/**
 * Created by JPAM on 5/14/18.
 * [FILTERS]
 * Enum Filters used for [Bitmap] processing
 * */
enum class FILTERS(val filter: ImageFilter.Filter, val filterName: String) {
    GRAY(ImageFilter.Filter.GRAY, ImageFilter.Filter.GRAY.name),
    RELIEF(ImageFilter.Filter.RELIEF, ImageFilter.Filter.RELIEF.name),
    AVERAGE_BLUR(ImageFilter.Filter.AVERAGE_BLUR, ImageFilter.Filter.AVERAGE_BLUR.name),
    OIL(ImageFilter.Filter.OIL, ImageFilter.Filter.OIL.name),
    NEON(ImageFilter.Filter.NEON, ImageFilter.Filter.NEON.name),
    PIXELATE(ImageFilter.Filter.PIXELATE, ImageFilter.Filter.PIXELATE.name),
    TV(ImageFilter.Filter.TV, ImageFilter.Filter.TV.name),
    INVERT_COLOR(ImageFilter.Filter.INVERT, ImageFilter.Filter.INVERT.name),
    BLOCK(ImageFilter.Filter.BLOCK, ImageFilter.Filter.BLOCK.name),
    OLD(ImageFilter.Filter.OLD, ImageFilter.Filter.OLD.name),
    SHARPEN(ImageFilter.Filter.SHARPEN, ImageFilter.Filter.SHARPEN.name),
    LIGHT(ImageFilter.Filter.LIGHT, ImageFilter.Filter.LIGHT.name),
    LOMO(ImageFilter.Filter.LOMO, ImageFilter.Filter.LOMO.name),
    HDR(ImageFilter.Filter.HDR, ImageFilter.Filter.HDR.name),
    GAUSSIAN_BLUR(ImageFilter.Filter.GAUSSIAN_BLUR, ImageFilter.Filter.GAUSSIAN_BLUR.name),
    SOFT_GLOW(ImageFilter.Filter.SOFT_GLOW, ImageFilter.Filter.SOFT_GLOW.name),
    SKETCH(ImageFilter.Filter.SKETCH, ImageFilter.Filter.SKETCH.name),
    MOTION_BLUR(ImageFilter.Filter.MOTION_BLUR, ImageFilter.Filter.MOTION_BLUR.name),
    GOTHAM(ImageFilter.Filter.GOTHAM, ImageFilter.Filter.GOTHAM.name)
}