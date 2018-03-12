package com.phdlabs.sungwon.a8chat_android.utility

/**
 * Created by paix on 2/7/18.
 * [SuffixDetector] used to analyze file types
 * TODO: Not currently used, could work for sending media with different specs on rooms
 */
class SuffixDetector {


    private object Holder {
        val INSTANCE = SuffixDetector()
    }

    companion object {
        //Singleton INSTANCE
        val instance: SuffixDetector by lazy { Holder.INSTANCE }
    }

    //TODO: Add all available file types for 8
    enum class FileType {
        JPEG,
        PDF,
        PNG,
        UNKOWN
    }

    /**
     * [getFileSuffix]
     * Return File Suffix
     * @return file suffix
     * */
    fun getFileSuffix(filepath: String): String? =
            filepath.substring(filepath.lastIndexOf("."))

    /**
     *
     * */

    /***/
    fun fileTypeDetector(fileName: String): FileType {
        val extension = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.count())
        when (extension) {
            "jpeg" -> {
                return FileType.JPEG
            }
            "png" -> {
                return FileType.PNG
            }
            else -> {
                return FileType.UNKOWN
            }
        }
    }

}