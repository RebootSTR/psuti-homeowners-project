
package ru.psuti.apache1337.homeowners.domain.profile

import android.text.Editable
import android.view.View
import android.widget.Button
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.google.android.material.textfield.TextInputLayout
import java.util.*
import kotlin.collections.HashSet

const val DEFAULT_ERROR_TEXT: String = "Некорректный ввод"

fun TextInputLayout.validateByPredicate(predicate: (it: Editable) -> Boolean,
                                        blocker: Blocker = Blocker(SingleErrorCounter()),
                                        errorText: String = DEFAULT_ERROR_TEXT) {
    this.editText?.setOnFocusChangeListener { v, hasFocus ->
        if (!hasFocus) {
            if (blocker.isContains(this) && this.editText?.text.toString() != "") {
                this.error = errorText
            }
        }
    }
    this.editText?.doAfterTextChanged {
        it?.let {
            this.isErrorEnabled = false
            if (!predicate(it)) {
                if (!blocker.isContains(this)) {
                    blocker.addError(this)
                }
            } else {
                if (blocker.isContains(this)) {
                    blocker.removeError(this)
                }
            }
        }
    }
    this.editText?.text?.let {
        if (!predicate(it)) {
            if (!blocker.isContains(this)) {
                blocker.addError(this)
            }
        }
    }
}

fun TextInputLayout.validateByRegex(regex: Regex,
                                    blocker: Blocker = Blocker(SingleErrorCounter()),
                                    errorText: String = DEFAULT_ERROR_TEXT) {
    this.validateByPredicate({it.matches(regex)}, blocker, errorText)
}

fun Button.addBlocker(blocker: Blocker, owner: LifecycleOwner) {
    blocker.errorWatcher.observe(owner) {
        this.isEnabled = !it.isError()
    }
}

interface ErrorCounter{

    fun addError(view: View)

    fun removeError(view: View)

    fun isError(): Boolean

    fun isContains(view: View) : Boolean
}

class SingleErrorCounter : ErrorCounter {
    var error = false

    override fun addError(view: View) {
        error = true
    }

    override fun removeError(view: View) {
        error = false
    }

    override fun isError(): Boolean {
        return error
    }

    override fun isContains(view: View) : Boolean {
        return isError()
    }
}

class UniqueErrorCounter : ErrorCounter {
    private val errors = HashSet<View>()

    override fun addError(view: View) {
        errors.add(view)
    }

    override fun removeError(view: View) {
        errors.remove(view)
    }

    override fun isError(): Boolean {
        return errors.isNotEmpty()
    }

    override fun isContains(view: View): Boolean {
        return errors.contains(view)
    }
}

class Blocker (private val errorCounter: ErrorCounter = UniqueErrorCounter()){
    val errorWatcher = MutableLiveData<ErrorCounter>(errorCounter)

    fun addError(view: View) {
        errorCounter.addError(view)
        errorWatcher.postValue(errorCounter)
    }

    fun removeError(view: View) {
        errorCounter.removeError(view)
        errorWatcher.postValue(errorCounter)
    }

    fun isContains(view: View): Boolean {
        return errorCounter.isContains(view)
    }
}
