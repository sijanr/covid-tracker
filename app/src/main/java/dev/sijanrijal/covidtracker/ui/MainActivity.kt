package dev.sijanrijal.covidtracker.ui

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import dev.sijanrijal.covidtracker.R
import dev.sijanrijal.covidtracker.databinding.ActivityMainBinding
import dev.sijanrijal.covidtracker.model.COVIDData
import dev.sijanrijal.covidtracker.util.CASE_TYPE
import dev.sijanrijal.covidtracker.util.DATA_TYPE
import dev.sijanrijal.covidtracker.util.GraphAdapter
import dev.sijanrijal.covidtracker.util.Metric
import dev.sijanrijal.covidtracker.viewmodels.MainActivityViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var graphAdapter: GraphAdapter

    private var currentDataSelection = DATA_TYPE.US
    private var currentStateSelection = ""
    private var stateAdapter: ArrayAdapter<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        graphAdapter = GraphAdapter(listOf())

        //initialize the views and set click listeners on radio buttons and country/state options
        init()

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        //display the data when it is ready
        viewModel.areDataReady.observe(this, { isReady ->
            if (isReady) {
                displayAndsetMenus(viewModel.stateList)
            }
        })
    }


    //hide the views that aren't ready to be displayed
    private fun init() {
        binding.radioButtonPositive.isChecked = true
        binding.radiobuttonMax.isChecked = true
        binding.state.visibility = View.INVISIBLE
        binding.menuUsState.visibility = View.INVISIBLE
        setEventListeners()
    }

    //display the views and the menus when the data is ready
    private fun displayAndsetMenus(list: MutableList<String>) {
        val nationAdapter = ArrayAdapter(this, R.layout.list_item, listOf("State", "US"))
        (binding.menuUsState.editText as? AutoCompleteTextView)?.setAdapter(nationAdapter)
        currentStateSelection = list.first()
        stateAdapter = ArrayAdapter(this, R.layout.list_item, viewModel.stateList)
        (binding.state.editText as? AutoCompleteTextView)?.setAdapter(stateAdapter)
        displayViews()
        updateGraph(viewModel.nationData)
    }

    //hide the progress bar and display the views when the data is ready
    private fun displayViews() {
        binding.radiogroupCaseType.visibility = View.VISIBLE
        binding.radiogroupTimeline.visibility = View.VISIBLE
        binding.menuUsState.visibility = View.VISIBLE
        binding.progressIndicator.visibility = View.GONE
        binding.graph.visibility = View.VISIBLE
        binding.graph.visibility = View.VISIBLE
    }

    //update graph with the latest data based on the user-selected option
    private fun updateGraph(covidData: List<COVIDData>) {
        val data = covidData.last()
        updateDateAndMetric(data)
        graphAdapter = GraphAdapter(covidData)
        binding.graph.adapter = graphAdapter
    }

    //update data and metric based on the user-selected option
    private fun updateDateAndMetric(data: COVIDData) {
        val numCases = when (graphAdapter.caseType) {
            CASE_TYPE.POSITIVE -> data.dailyIncreaseCases
            CASE_TYPE.NEGATIVE -> data.dailyDecreaseCases
            CASE_TYPE.DEATH -> data.deathIncreaseCases
        }
        binding.metricLabel.setCharacterLists(NumberFormat.getInstance().format(numCases))
        binding.metricLabel.text = NumberFormat.getInstance().format(numCases)
        val date = data.lastUpdatedNationalCases
        binding.dateLabel.text = SimpleDateFormat("MMM-dd-yyyy", Locale.US)
            .format(date)
    }

    //update the line color of the graph as the user selects different case types
    private fun updateGraphStyle() {
        val colorResource = when (graphAdapter.caseType) {
            CASE_TYPE.POSITIVE -> R.color.positive_increase
            CASE_TYPE.NEGATIVE -> R.color.negative_increase
            else -> R.color.death_increase
        }
        binding.graph.lineColor = ContextCompat.getColor(this, colorResource)
        binding.metricLabel.textColor = ContextCompat.getColor(this, colorResource)
    }

    //set click listeners
    private fun setEventListeners() {
        binding.graph.isScrubEnabled = true
        binding.graph.setScrubListener { data ->
            if (data is COVIDData) {
                updateDateAndMetric(data)
            }
        }

        binding.radiogroupTimeline.setOnCheckedChangeListener { _, checkedId ->
            graphAdapter.timeLine = when (checkedId) {
                R.id.radiobutton_week -> Metric.WEEK
                R.id.radiobutton_month -> Metric.MONTH
                else -> Metric.MAX
            }
            graphAdapter.notifyDataSetChanged()
        }

        binding.radiogroupCaseType.setOnCheckedChangeListener { _, checkedId ->
            graphAdapter.caseType = when (checkedId) {
                R.id.radioButton_positive -> CASE_TYPE.POSITIVE
                R.id.radioButton_negative -> CASE_TYPE.NEGATIVE
                else -> CASE_TYPE.DEATH
            }
            graphAdapter.notifyDataSetChanged()
            updateGraphStyle()
            updateDateAndMetric(graphAdapter.getLastItem())
        }

        (binding.menuUsState.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, view, _, _ ->
            if (view is TextView) {
                if (view.text.toString().toUpperCase() == DATA_TYPE.US.toString()) {
                    if (currentDataSelection != DATA_TYPE.US) {
                        currentDataSelection = DATA_TYPE.US
                        binding.state.visibility = View.INVISIBLE
                        updateGraph(viewModel.nationData)
                    }
                } else {
                    currentDataSelection = DATA_TYPE.STATE
                    binding.state.visibility = View.VISIBLE
                    (binding.state.editText as? AutoCompleteTextView)?.setText(
                        currentStateSelection,
                        false
                    )
                    updateGraph(viewModel.stateData[currentStateSelection]!!)
                }
            }
        }

        (binding.state.editText as? AutoCompleteTextView)?.setOnItemClickListener { _, view, _, _ ->
            if (view is TextView) {
                if (view.text != currentStateSelection) {
                    currentStateSelection = view.text.toString()
                    updateGraph(viewModel.stateData[currentStateSelection]!!)
                }
            }
        }


    }
}