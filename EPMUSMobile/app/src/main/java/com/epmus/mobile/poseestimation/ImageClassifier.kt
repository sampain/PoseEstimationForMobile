/*
 * Copyright 2018 Zihua Zeng (edvard_hua@live.com), Lang Feng (tearjeaker@hotmail.com)
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epmus.mobile.poseestimation

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.SystemClock
import android.util.Log
import com.epmus.mobile.ml.PoseModel
import org.tensorflow.lite.support.model.Model
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Classifies images with Tensorflow Lite.
 */
abstract class ImageClassifier
/** Initializes an `ImageClassifier`.  */
@Throws(IOException::class)
internal constructor(
    activity: Activity,
    val imageSizeX: Int, // Get the image size along the x axis.
    val imageSizeY: Int, // Get the image size along the y axis.
    // Get the number of bytes that is used to store a single color channel value.
    numBytesPerChannel: Int
) {

    /* Preallocated buffers for storing image data in. */
    private val intValues = IntArray(imageSizeX * imageSizeY)

    /** An instance of the driver class to run model inference with Tensorflow Lite.  */
    protected var tflite: PoseModel? = null

    /** A ByteBuffer to hold image data, to be feed into Tensorflow Lite as inputs.  */
    protected var imgData: ByteBuffer? = null

    var mPrintPointArray: Array<FloatArray>? = null

    val activity = activity
    fun initTflite() {
        val options = Model.Options.Builder().setDevice(Model.Device.GPU).build()
        tflite = PoseModel.newInstance(activity, options)
    }

    init {
        imgData = ByteBuffer.allocateDirect(
            DIM_BATCH_SIZE
                    * imageSizeX
                    * imageSizeY
                    * DIM_PIXEL_SIZE
                    * numBytesPerChannel
        )
        imgData!!.order(ByteOrder.nativeOrder())
        Log.d(TAG, "Created a Tensorflow Lite Image Classifier.")
    }

    /** Classifies a frame from the preview stream.  */
    fun classifyFrame(bitmap: Bitmap): String {
        if (tflite == null) {
            Log.e(TAG, "Image classifier has not been initialized; Skipped.")
            return "Uninitialized Classifier."
        }
        convertBitmapToByteBuffer(bitmap)
        // Here's where the magic happens!!!
        val startTime = SystemClock.uptimeMillis()
        runInference()
        val endTime = SystemClock.uptimeMillis()
        Log.d(TAG, "Timecost to run model inference: " + (endTime - startTime))

        bitmap.recycle()
        // Print the results.
        //    String textToShow = printTopKLabels();
        return (endTime - startTime).toString() + "ms"
    }


    /** Closes tflite to release resources.  */
    fun close() {
        tflite!!.close()
        tflite = null
    }

    /** Writes Image data into a `ByteBuffer`.  */
    @SuppressLint("LogNotTimber")
    private fun convertBitmapToByteBuffer(bitmap: Bitmap) {
        if (imgData == null) {
            return
        }
        imgData!!.rewind()
        bitmap.getPixels(intValues, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        // Convert the image to floating point.
        var pixel = 0
        val startTime = SystemClock.uptimeMillis()
        for (i in 0 until imageSizeX) {
            for (j in 0 until imageSizeY) {
                val v = intValues[pixel++]
                addPixelValue(v)
            }
        }
        val endTime = SystemClock.uptimeMillis()
        Log.d(
            TAG,
            "Timecost to put values into ByteBuffer: " + (endTime - startTime).toString()
        )
    }

    /**
     * Add pixelValue to byteBuffer.
     *
     * @param pixelValue
     */
    protected abstract fun addPixelValue(pixelValue: Int)

    /**
     * Read the probability value for the specified label This is either the original value as it was
     * read from the net's output or the updated value after the filter was applied.
     *
     * @param labelIndex
     * @return
     */
    protected abstract fun getProbability(labelIndex: Int): Float

    /**
     * Set the probability value for the specified label.
     *
     * @param labelIndex
     * @param value
     */
    protected abstract fun setProbability(
        labelIndex: Int,
        value: Number
    )

    /**
     * Get the normalized probability value for the specified label. This is the final value as it
     * will be shown to the user.
     *
     * @return
     */
    protected abstract fun getNormalizedProbability(labelIndex: Int): Float

    /**
     * Run inference using the prepared input in [.imgData]. Afterwards, the result will be
     * provided by getProbability().
     *
     *
     * This additional method is necessary, because we don't have a common base for different
     * primitive data types.
     */
    protected abstract fun runInference()

    companion object {

        /** Tag for the [Log].  */
        private const val TAG = "TfLiteCameraDemo"

        /** Dimensions of inputs.  */
        private const val DIM_BATCH_SIZE = 1

        private const val DIM_PIXEL_SIZE = 3

    }
}
