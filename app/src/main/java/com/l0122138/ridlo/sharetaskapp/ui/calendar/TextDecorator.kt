package com.l0122138.ridlo.sharetaskapp.ui.calendar

import android.content.Context
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.l0122138.ridlo.sharetaskapp.R
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

class TextColorDecorator(context: Context) : DayViewDecorator {

    private val color: Int = ContextCompat.getColor(context, R.color.md_theme_onSurface)
    private val currentDate: CalendarDay = CalendarDay.today() // Get current date

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.month == currentDate.month
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(ForegroundColorSpan(color))
    }
}