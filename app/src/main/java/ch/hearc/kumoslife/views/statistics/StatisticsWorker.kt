package ch.hearc.kumoslife.views.statistics

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import ch.hearc.kumoslife.modelview.StatisticViewModel

class StatisticsWorker(context: Context, params: WorkerParameters) : Worker(context, params)
{
    private val viewModel: StatisticViewModel = StatisticViewModel.getInstance()

    override fun doWork(): Result
    {
        return try
        {
            viewModel.progressAllStatistics()
            Result.success()
        }
        catch (throwable: Throwable)
        {
            Result.failure()
        }
    }
}
