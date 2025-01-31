package org.merakilearn.ui.onboarding

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.merakilearn.core.datasource.Config
import org.merakilearn.core.utils.CorePreferences
import org.merakilearn.datasource.UserRepo
import org.merakilearn.datasource.network.model.OnBoardingData
import org.merakilearn.datasource.network.model.OnBoardingTranslations
import org.navgurukul.commonui.platform.*

class OnBoardingViewModel(
    onBoardingActivityArgs: OnBoardingActivityArgs?,
    userRepo: UserRepo,
    private val config: Config,
    private val corePreferences: CorePreferences
) :
    BaseViewModel<OnBoardingViewEvents, OnBoardingViewState>(OnBoardingViewState()) {

    init {
        viewModelScope.launch {
            val selectedLanguage = corePreferences.selectedLanguage
            val data =
                config.getObjectifiedValue<OnBoardingData>(
                    Config.ON_BOARDING_DATA
                )!!
            setState {
                copy(
                    onBoardingData = data,
                    onBoardingTranslations = data.onBoardingTranslations[selectedLanguage]
                )
            }
        }
    }

    enum class Language(val code: String) {
        ENGLISH("en"), HINDI("hi")
    }

    init {
        if (onBoardingActivityArgs?.showLoginFragment == true) {
            _viewEvents.setValue(OnBoardingViewEvents.ShowLoginScreen)
        } else {
            if (userRepo.isUserLoggedIn()) {
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen())
            } else {
                _viewEvents.setValue(OnBoardingViewEvents.ShowSelectLanguageFragment)
            }
        }
    }

    fun handle(action: OnBoardingViewActions) {
        when (action) {
            OnBoardingViewActions.NavigateNextFromOnBoardingScreen -> _viewEvents.setValue(
                OnBoardingViewEvents.ShowCourseSelectionScreen
            )
            is OnBoardingViewActions.SelectLanguage -> {
                corePreferences.selectedLanguage = action.language.code
                _viewEvents.setValue(
                    OnBoardingViewEvents.ShowOnBoardingPages
                )
            }
            is OnBoardingViewActions.SelectCourse -> {
                corePreferences.lastSelectedPathWayId = action.pathwayId
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen(pathwayId = action.pathwayId))
            }
            is OnBoardingViewActions.OpenHomeScreen ->{
                corePreferences.lastSelectedPathWayId=action.pathwayId
                _viewEvents.setValue(OnBoardingViewEvents.ShowMainScreen(pathwayId =action.pathwayId))
            }
        }
    }
}

sealed class OnBoardingViewEvents : ViewEvents {
    object ShowSelectLanguageFragment : OnBoardingViewEvents()
    data class ShowMainScreen(val pathwayId: Int? = null) : OnBoardingViewEvents()
    object ShowCourseSelectionScreen : OnBoardingViewEvents()
    object ShowOnBoardingPages : OnBoardingViewEvents()
    object ShowLoginScreen : OnBoardingViewEvents()
}

sealed class OnBoardingViewActions : ViewModelAction {
    object NavigateNextFromOnBoardingScreen : OnBoardingViewActions()
    data class SelectLanguage(val language: OnBoardingViewModel.Language) : OnBoardingViewActions()
    data class SelectCourse(val pathwayId: Int) : OnBoardingViewActions()
    data class OpenHomeScreen(val pathwayId:Int):OnBoardingViewActions()
}

data class OnBoardingViewState(
    val onBoardingData: OnBoardingData? = null,
    val onBoardingTranslations: OnBoardingTranslations? = null
) : ViewState