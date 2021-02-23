package dev.sijanrijal.covidtracker.util

import android.graphics.RectF
import com.robinhood.spark.SparkAdapter
import dev.sijanrijal.covidtracker.model.COVIDData

class GraphAdapter(private var list : List<COVIDData>) : SparkAdapter() {

    fun updateList(list : List<COVIDData>) {
        this.list = list
        notifyDataSetChanged()
    }

    var caseType : CASE_TYPE = CASE_TYPE.POSITIVE
    var timeLine = Metric.MAX

    override fun getCount() = list.size

    override fun getItem(index: Int) = list[index]

    fun getLastItem() = list.last()

    override fun getY(index: Int): Float {
        return when (caseType) {
            CASE_TYPE.POSITIVE -> list[index].dailyIncreaseCases
            CASE_TYPE.DEATH -> list[index].deathIncreaseCases
            CASE_TYPE.NEGATIVE -> list[index].dailyDecreaseCases
        }.toFloat()
    }

    override fun getDataBounds(): RectF {
        val bounds = super.getDataBounds()
        if (timeLine != Metric.MAX) {
            bounds.left = (count - timeLine.days).toFloat()
        }
        return bounds
    }
}