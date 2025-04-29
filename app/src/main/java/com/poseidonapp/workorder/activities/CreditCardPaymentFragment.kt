package com.poseidonapp.workorder.activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.poseidonapp.R
import com.poseidonapp.databinding.FragmentCreditCardPaymentBinding
import com.poseidonapp.ui.base.BaseFragment
import com.poseidonapp.utils.HandleClickListener


class CreditCardPaymentFragment : BaseFragment(), HandleClickListener{


    var binding: FragmentCreditCardPaymentBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (binding == null)
            binding = FragmentCreditCardPaymentBinding.inflate(inflater, container, false)
        initUI()
        return binding!!.root
    }

    private fun initUI() {
        binding!!.handleClick=this
//        ivBack
    }

    override fun onViewClick(view: View) {
        when (view.id) {
            R.id.ivBack->{
//                findNavController().popBackStack()
                requireActivity().supportFragmentManager.popBackStack()


            }

        }

    }


}