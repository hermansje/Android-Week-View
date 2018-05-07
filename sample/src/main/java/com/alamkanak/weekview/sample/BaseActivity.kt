package com.alamkanak.weekview.sample

import android.content.ClipData
import android.graphics.RectF
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.text.format.DateFormat
import android.text.format.DateUtils
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.alamkanak.weekview.DateTimeInterpreter
import com.alamkanak.weekview.MonthLoader
import com.alamkanak.weekview.WeekView
import com.alamkanak.weekview.WeekViewEvent
import java.text.SimpleDateFormat
import java.util.*

/**
 * This is a base activity which contains week view and all the codes necessary to initialize the
 * week view.
 * Created by Raquib-ul-Alam Kanak on 1/3/2014.
 * Website: http://alamkanak.github.io
 */
abstract class BaseActivity : AppCompatActivity(), WeekView.EventClickListener, MonthLoader.MonthChangeListener, WeekView.EventLongPressListener, WeekView.EmptyViewLongPressListener, WeekView.EmptyViewClickListener, WeekView.AddEventClickListener, WeekView.DropListener {
    private var mWeekViewType = TYPE_THREE_DAY_VIEW
    lateinit var weekView: WeekView
        protected set

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)

        val draggableView = findViewById<View>(R.id.draggable_view) as TextView
        draggableView.setOnLongClickListener(DragTapListener())


        // Get a reference for the week view in the layout.
        weekView = findViewById<View>(R.id.weekView) as WeekView

        // Show a toast message about the touched event.
        weekView.setOnEventClickListener(this)

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        weekView.monthChangeListener = this

        // Set long press listener for events.
        weekView.eventLongPressListener = this

        // Set long press listener for empty view
        weekView.emptyViewLongPressListener = this

        // Set EmptyView Click Listener
        weekView.emptyViewClickListener = this

        // Set AddEvent Click Listener
        weekView.addEventClickListener = this

        // Set Drag and Drop Listener
        weekView.setDropListener(this)

        // Set minDate
        /*Calendar minDate = Calendar.getInstance();
        minDate.set(Calendar.DAY_OF_MONTH, 1);
        minDate.add(Calendar.MONTH, 1);
        mWeekView.setMinDate(minDate);

        // Set maxDate
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 1);
        maxDate.set(Calendar.DAY_OF_MONTH, 10);
        mWeekView.setMaxDate(maxDate);

        Calendar calendar = (Calendar) maxDate.clone();
        calendar.add(Calendar.DATE, -2);
        mWeekView.goToDate(calendar);*/

        //mWeekView.setAutoLimitTime(true);
        //mWeekView.setLimitTime(4, 16);

        //mWeekView.setMinTime(10);
        //mWeekView.setMaxTime(20);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false)
    }

    override fun onResume() {
        super.onResume()
        /*mWeekView.setShowDistinctPastFutureColor(true);
        mWeekView.setShowDistinctWeekendColor(true);
        mWeekView.setFutureBackgroundColor(Color.rgb(24,85,96));
        mWeekView.setFutureWeekendBackgroundColor(Color.rgb(255,0,0));
        mWeekView.setPastBackgroundColor(Color.rgb(85,189,200));
        mWeekView.setPastWeekendBackgroundColor(Color.argb(50, 0,255,0));
        */
    }

    private inner class DragTapListener : View.OnLongClickListener {
        override fun onLongClick(v: View): Boolean {
            val data = ClipData.newPlainText("", "")
            val shadowBuilder = View.DragShadowBuilder(v)
            v.startDrag(data, shadowBuilder, v, 0)
            return true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        setupDateTimeInterpreter(id == R.id.action_week_view)
        when (id) {
            R.id.action_today -> {
                weekView.goToToday()
                return true
            }
            R.id.action_day_view -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    setDayViewType(TYPE_DAY_VIEW)
                }
                return true
            }
            R.id.action_three_day_view -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    setDayViewType(TYPE_THREE_DAY_VIEW)
                }
                return true
            }
            R.id.action_week_view -> {
                if (!item.isChecked) {
                    item.isChecked = true
                    setDayViewType(TYPE_WEEK_VIEW)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun setDayViewType(dayViewType: Int) {
        when (dayViewType) {
            TYPE_DAY_VIEW -> {
                mWeekViewType = TYPE_DAY_VIEW
                weekView.numberOfVisibleDays = 1
                // Lets change some dimensions to best fit the view.
                weekView.columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                weekView.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
                weekView.eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
            }
            TYPE_THREE_DAY_VIEW -> {
                mWeekViewType = TYPE_THREE_DAY_VIEW
                weekView.numberOfVisibleDays = 3
                // Lets change some dimensions to best fit the view.
                weekView.columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics).toInt()
                weekView.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
                weekView.eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12f, resources.displayMetrics).toInt()
            }
            TYPE_WEEK_VIEW -> {
                mWeekViewType = TYPE_WEEK_VIEW
                weekView.numberOfVisibleDays = 7
                // Lets change some dimensions to best fit the view.
                weekView.columnGap = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics).toInt()
                weekView.textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics).toInt()
                weekView.eventTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10f, resources.displayMetrics).toInt()
            }
        }
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private fun setupDateTimeInterpreter(shortDate: Boolean) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val dateFormat = DateFormat.getTimeFormat(this@BaseActivity)
                ?: SimpleDateFormat("HH:mm", Locale.getDefault())
        val format = SimpleDateFormat(" M/d", Locale.getDefault())
        weekView.dateTimeInterpreter = object : DateTimeInterpreter {
            override fun interpretTime(hour: Int, minutes: Int): String {
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minutes)
                return dateFormat.format(calendar.time)
            }

            override fun interpretDate(date: Calendar): String {
                var weekday = DateUtils.getDayOfWeekString(date.get(Calendar.DAY_OF_WEEK), DateUtils.LENGTH_SHORT)
                if (shortDate) {
                    val dayOfWeekString = DateUtils.getDayOfWeekString(date.get(Calendar.DAY_OF_WEEK), DateUtils.LENGTH_SHORTEST)
                    weekday = dayOfWeekString
                }
                return weekday + format.format(date.time)
            }

        }
    }

    protected fun getEventTitle(time: Calendar): String {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH))
    }

    override fun onEventClick(event: WeekViewEvent, eventRect: RectF) {
        Toast.makeText(this, "Clicked " + event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEventLongPress(event: WeekViewEvent, eventRect: RectF) {
        Toast.makeText(this, "Long pressed event: " + event.name, Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyViewLongPress(time: Calendar) {
        Toast.makeText(this, "Empty view long pressed: " + getEventTitle(time), Toast.LENGTH_SHORT).show()
    }

    override fun onEmptyViewClicked(date: Calendar) {
        Toast.makeText(this, "Empty view" + " clicked: " + getEventTitle(date), Toast.LENGTH_SHORT).show()
    }

    override fun onMonthChange(newYear: Int, newMonth: Int): List<WeekViewEvent>? {
        return null
    }

    override fun onAddEventClicked(startTime: Calendar, endTime: Calendar) {
        Toast.makeText(this, "Add event clicked.", Toast.LENGTH_SHORT).show()
    }

    override fun onDrop(view: View, date: Calendar) {
        Toast.makeText(this, "View dropped to " + date.toString(), Toast.LENGTH_SHORT).show()
    }

    companion object {
        val TYPE_DAY_VIEW = 1
        val TYPE_THREE_DAY_VIEW = 2
        val TYPE_WEEK_VIEW = 3
    }
}
