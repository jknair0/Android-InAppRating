package com.byjus.thelearningapp.inapprating

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode
import com.google.android.play.core.review.testing.FakeReviewManager
import com.google.android.play.core.tasks.RuntimeExecutionException
import com.google.android.play.core.tasks.Task
import io.reactivex.Completable
import io.reactivex.Single

class InAppRatingManager(context: Context, debugMode: Boolean) {

    private val reviewManager: ReviewManager = if (debugMode) FakeReviewManager(context) else ReviewManagerFactory.create(context)

    fun showRating(activity: Activity): Completable {
        return getReviewInfo(reviewManager)
            .flatMapCompletable { launchFlow(activity, it) }
    }

    private fun getReviewInfo(reviewManager: ReviewManager): Single<ReviewInfo> {
        return Single.create { emitter ->
            val taskPreComplete: Task<ReviewInfo> = reviewManager.requestReviewFlow()
            taskPreComplete.addOnCompleteListener { task: Task<ReviewInfo> ->
                if (task.isSuccessful) {
                    val reviewInfo: ReviewInfo = task.result
                    emitter.onSuccess(reviewInfo)
                } else {
                    val exception: Exception? = task.exception
                    @ReviewErrorCode val reviewErrorCode = (exception as? RuntimeExecutionException)?.errorCode
                    val errorMessage = exception?.localizedMessage?.let { "$it: error code - $reviewErrorCode" }
                    emitter.onError(
                        InAppRatingException(errorMessage ?: "Unknown Error while requestReviewFlow", reviewErrorCode)
                    )
                }
            }
        }
    }

    private fun launchFlow(activity: Activity, reviewInfo: ReviewInfo): Completable {
        return Completable.create { emitter ->
            val flow: Task<Void> = reviewManager.launchReviewFlow(activity, reviewInfo)
            flow.addOnCompleteListener {
                if (it.isSuccessful) {
                    emitter.onComplete()
                } else {
                    emitter.onError(it.exception ?: throw RuntimeException("Unknown Error while launching review flow"))
                }
            }
        }
    }

    enum class ErrorType(val errorCode: Int) {
        PLAY_STORE_NOT_FOUND(ReviewErrorCode.PLAY_STORE_NOT_FOUND), NO_ERROR(ReviewErrorCode.NO_ERROR)
    }

    class InAppRatingException(message: String, @ReviewErrorCode private val errorCode: Int?) :
        Exception(message) {

        val errorType: ErrorType? = errorCode?.let { errorCode -> ErrorType.values().find { it.errorCode == errorCode } }

    }

}