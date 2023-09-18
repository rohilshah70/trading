package com.example.trading.mvi

/**
 * Responsible for the current state of a ViewModel
 */
interface MviState

/**
 * Event are user actions like - clicking a button, editing a text-box etc
 */
interface MviEvent

/**
 * Side effects or simply effects are useful to execute functionalities that escape the scope of
 * a ViewModel.
 *
 * For example - making an API call, executing a db query, reading from external storage etc
 */
interface MviEffect
