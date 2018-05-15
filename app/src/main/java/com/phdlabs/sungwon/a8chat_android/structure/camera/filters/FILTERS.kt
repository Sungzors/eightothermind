package com.phdlabs.sungwon.a8chat_android.structure.camera.filters

import android.graphics.Bitmap
import net.alhazmy13.imagefilter.ImageFilter

/**
 * Created by JPAM on 5/14/18.
 * [FILTERS]
 * Enum Filters used for [Bitmap] processing
 * */
enum class FILTERS(val filter: ImageFilter.Filter, val filterName: String) {
    GRAY(ImageFilter.Filter.GRAY, "gray"),
    RELIEF(ImageFilter.Filter.RELIEF, "relief"),
    AVERAGE_BLUR(ImageFilter.Filter.AVERAGE_BLUR, "blur"),
    OIL(ImageFilter.Filter.OIL, "oil"),
    NEON(ImageFilter.Filter.NEON, "neon"),
    PIXELATE(ImageFilter.Filter.PIXELATE, "pixelate"),
    TV(ImageFilter.Filter.TV, "tv"),
    INVERT_COLOR(ImageFilter.Filter.INVERT, "invert"),
    BLOCK(ImageFilter.Filter.BLOCK, "bock"),
    OLD(ImageFilter.Filter.OLD, "old"),
    SHARPEN(ImageFilter.Filter.SHARPEN, "sharpen"),
    LIGHT(ImageFilter.Filter.LIGHT, "light"),
    LOMO(ImageFilter.Filter.LOMO, "lomo"),
    HDR(ImageFilter.Filter.HDR, "hdr"),
    GAUSSIAN_BLUR(ImageFilter.Filter.GAUSSIAN_BLUR, "G-blur"),
    SOFT_GLOW(ImageFilter.Filter.SOFT_GLOW, "glow"),
    SKETCH(ImageFilter.Filter.SKETCH, "sketch"),
    MOTION_BLUR(ImageFilter.Filter.MOTION_BLUR, "motion"),
    GOTHAM(ImageFilter.Filter.GOTHAM, "gotham")
}