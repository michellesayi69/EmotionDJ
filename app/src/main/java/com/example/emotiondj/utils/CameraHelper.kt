package com.example.emotiondj.utils

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.core.*
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraHelper(
    private val context: Context,
    private val previewView: PreviewView,
    private val onMoodDetected: (String, Float) -> Unit
) {
    private var cameraExecutor: ExecutorService = Executors.newSingleThreadExecutor()
    private var imageAnalysis: ImageAnalysis? = null
    private var camera: Camera? = null

    private val options = FaceDetectorOptions.Builder()
        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
        .build()
    private val detector = FaceDetection.getClient(options)

    fun startCamera(lifecycleOwner: androidx.lifecycle.LifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context.applicationContext)
        cameraProviderFuture.addListener({
            try {
                val cameraProvider = cameraProviderFuture.get()
                
                val previewResolutionSelector = ResolutionSelector.Builder()
                    .setResolutionStrategy(ResolutionStrategy(Size(640, 480), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                    .build()

                val preview = Preview.Builder()
                    .setResolutionSelector(previewResolutionSelector)
                    .build()
                    .also { it.setSurfaceProvider(previewView.surfaceProvider) }

                val analysisResolutionSelector = ResolutionSelector.Builder()
                    .setResolutionStrategy(ResolutionStrategy(Size(320, 240), ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER))
                    .build()

                imageAnalysis = ImageAnalysis.Builder()
                    .setResolutionSelector(analysisResolutionSelector)
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also { it.setAnalyzer(cameraExecutor) { imageProxy -> processImageProxy(imageProxy) } }

                val cameraSelector = when {
                    cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) -> CameraSelector.DEFAULT_FRONT_CAMERA
                    cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) -> CameraSelector.DEFAULT_BACK_CAMERA
                    else -> CameraSelector.Builder().build() // Fallback to any camera
                }

                cameraProvider.unbindAll()
                try {
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )
                    Log.d("CameraHelper", "Camera started with selector: $cameraSelector")
                } catch (e: IllegalArgumentException) {
                    Log.e("CameraHelper", "Primary camera selection failed, trying final fallback", e)
                    camera = cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.Builder().build(),
                        preview,
                        imageAnalysis
                    )
                }
                Log.d("CameraHelper", "Camera started")
            } catch (e: Exception) {
                Log.e("CameraHelper", "Camera start failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return imageProxy.close()
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        detector.process(image)
            .addOnSuccessListener { faces ->
                if (faces.isNotEmpty()) {
                    val face = faces[0]
                    val (mood, confidence) = MoodDetector.detectMood(face)
                    onMoodDetected(mood, confidence)
                }
            }
            .addOnFailureListener { e ->
                val isDownloadError = e is com.google.mlkit.common.MlKitException && 
                        (e.errorCode == 14 || // MODEL_NOT_DOWNLOADED
                         e.errorCode == 2 ||  // UNAVAILABLE
                         e.message?.contains("download", ignoreCase = true) == true ||
                         e.message?.contains("wait", ignoreCase = true) == true)
                
                if (isDownloadError) {
                    Log.w("CameraHelper", "Face detection module is still downloading: ${e.message}")
                } else {
                    Log.e("CameraHelper", "Detection error", e)
                }
            }
            .addOnCompleteListener { imageProxy.close() }
    }

    fun stopCamera() {
        cameraExecutor.shutdown()
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try { cameraProviderFuture.get().unbindAll() } catch (e: Exception) { }
        }, ContextCompat.getMainExecutor(context))
    }
}