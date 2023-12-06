package com.skymilk.shoppingkt.dialogs

import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skymilk.shoppingkt.R

fun Fragment.setUpResetPassword(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val view = layoutInflater.inflate(R.layout.dialog_reset_password, null)
    dialog.setContentView(view)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    val editEmail = view.findViewById<EditText>(R.id.editEmail)
    val btnSend = view.findViewById<Button>(R.id.btnSend)
    val btnCancel = view.findViewById<Button>(R.id.btnCancel)

    btnSend.setOnClickListener {
        val email = editEmail.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    btnCancel.setOnClickListener {
        dialog.dismiss()
    }

    //포커스 설정
    editEmail.requestFocus()
}
