package org.navgurukul.commonui.platform

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.navgurukul.chat.core.model.Async
import org.navgurukul.chat.core.model.Fail
import org.navgurukul.chat.core.model.Loading
import org.navgurukul.chat.core.model.Success
import org.navgurukul.commonui.BuildConfig
import kotlin.reflect.KProperty1

open class BaseViewModel<VE: ViewEvents, S: ViewState>(initialState: S): ViewModel() {

    private val disposables = CompositeDisposable()

    // Used to post transient events to the View
    protected val _viewEvents = MutableLiveData<VE>()
    val viewEvents: LiveData<VE> = _viewEvents

    // Used to post transient events to the View
    private val _viewState = MutableLiveData<S>(initialState)
    val viewState: LiveData<S> = _viewState

    protected fun setState(reducer: S.() -> S) {
        _viewState.postValue(reducer(_viewState.value!!))
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    protected fun Disposable.disposeOnClear(): Disposable {
        disposables.add(this)
        return this
    }

    /**
     * Helper to map an [Observable] to an [Async] property on the state object.
     */
    fun <T> Observable<T>.execute(
        stateReducer: S.(Async<T>) -> S
    ) = execute({ it }, null, stateReducer)

    /**
     * Execute an [Observable] and wrap its progression with [Async] property reduced to the global state.
     *
     * @param mapper A map converting the Observable type to the desired Async type.
     * @param successMetaData A map that provides metadata to set on the Success result.
     *                        It allows data about the original Observable to be kept and accessed later. For example,
     *                        your mapper could map a network request to just the data your UI needs, but your base layers could
     *                        keep metadata about the request, like timing, for logging.
     * @param stateReducer A reducer that is applied to the current state and should return the
     *                     new state. Because the state is the receiver and it likely a data
     *                     class, an implementation may look like: `{ copy(response = it) }`.
     *
     *  @see Success.metadata
     */
    fun <T, V> Observable<T>.execute(
        mapper: (T) -> V,
        successMetaData: ((T) -> Any)? = null,
        stateReducer: S.(Async<V>) -> S
    ): Disposable {
        // Intentionally didn't use RxJava's startWith operator. When withState is called right after execute then the loading reducer won't be enqueued yet if startWith is used.
        setState { stateReducer(Loading()) }

        return map<Async<V>> { value ->
            val success = Success(mapper(value))
            success.metadata = successMetaData?.invoke(value)
            success
        }
            .onErrorReturn { e ->
                if (BuildConfig.DEBUG) Log.e(BaseViewModel::class.java.name, "Observable encountered error", e)
                Fail(e)
            }
            .subscribe { asyncData -> setState { stateReducer(asyncData) } }
            .disposeOnClear()
    }
}