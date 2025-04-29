package com.poseidonapp

import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.poseidonapp.databinding.FragmentFlowTestBinding
import com.poseidonapp.retrofit.Resource
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.HandleClickListener
import java.util.*


class FlowTestFragment : BaseFragment(), HandleClickListener {

    var binding: FragmentFlowTestBinding?=null

    var ratedVolume=1000f
    var ratedPressure=2000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentFlowTestBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick = this

    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.tvDone -> {
                var value1=0
                var value2=0
                var value3=0
                var value4=0
                var value5=0
                var value6=0
                var value7=0
                var value8=0
                var value9=0

                if (!binding!!.valueOneED.text.toString().equals("")){
                    value1= binding!!.valueOneED.text.toString().toInt() ;
                }
                if (!binding!!.valueTwoED.text.toString().equals("")){
                     value2=binding!!.valueTwoED.text.toString().toInt()
                }
                if (!binding!!.valueThreeED.text.toString().equals("")){
                     value3=binding!!.valueThreeED.text.toString().toInt()
                }
                if (!binding!!.valueFourED.text.toString().equals("")){
                     value4=binding!!.valueFourED.text.toString().toInt()
                }
                if (!binding!!.valueFiveED.text.toString().equals("")){
                     value5=binding!!.valueFiveED.text.toString().toInt()
                }
                if (!binding!!.valueSixED.text.toString().equals("")){
                     value6=binding!!.valueSixED.text.toString().toInt()
                }
                if (!binding!!.valueSevenED.text.toString().equals("")){
                     value7 =binding!!.valueSevenED.text.toString().toInt()
                }
                if (!binding!!.valueEightED.text.toString().equals("")){
                    value8=binding!!.valueEightED.text.toString().toInt()
                }
                if (!binding!!.valueNineED.text.toString().equals("")){
                     value9=binding!!.valueNineED.text.toString().toInt()
                }


                binding!!.lineChart.visibility=View.VISIBLE

                binding!!.lineChart.setDrawGridBackground(false)
                binding!!.lineChart.description.isEnabled = false
                val xAxis = binding!!.lineChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                xAxis.setDrawGridLines(true)
                binding!!.lineChart.axisRight.isEnabled = true

                val entriesSet1 = listOf(
                    Entry(0f, (value3.toInt().toFloat()-value2.toInt().toFloat())),
                    Entry(ratedVolume, (value6.toInt().toFloat()-value5.toInt().toFloat())),
                    Entry((ratedVolume*1.5).toFloat(),(value9.toInt().toFloat()-value8.toInt().toFloat())))

                val entriesSet2 = listOf(
                    Entry(0f, (value3.toInt().toFloat()-value2.toInt().toFloat())),
                    Entry(ratedVolume, ratedPressure.toFloat()),
                    Entry((ratedVolume*1.5).toFloat(), (ratedPressure*0.65).toFloat()))

                val d = Date()
                val curentDate: CharSequence = DateFormat.format("MMMM d, yyyy ", d.getTime())

                val dataSet1 = LineDataSet(entriesSet1, curentDate.toString())
                dataSet1.lineWidth = 2f
                dataSet1.circleRadius = 4f
                dataSet1.color = resources.getColor(R.color.orange)
                dataSet1.setDrawValues(true)

                val dataSet2 = LineDataSet(entriesSet2, "Theoretical")
                dataSet2.lineWidth = 2f
                dataSet2.circleRadius = 4f
                dataSet2.color = Color.CYAN
                dataSet2.setDrawValues(true)

                val lineData = LineData(dataSet1, dataSet2)
                binding!!.lineChart.data = lineData
                binding!!.lineChart.setDrawGridBackground(false)
                binding!!.lineChart.description.isEnabled = false
                binding!!.lineChart.invalidate()

            }

            R.id.ivBack->{
                baseActivity!!.onBackPressed()
            }
        }}
}

