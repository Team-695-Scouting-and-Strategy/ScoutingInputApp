package com.example.scoutinginputapp

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class ScoutingViewModel : ViewModel() {
    private val _teams = mutableStateListOf<Team>()
    val teams: List<Team> get() = _teams

    fun addTeam(number: String, name: String) {
        _teams.add(Team(number = number, name = name))
    }

    fun updateTeam(updatedTeam: Team) {
        val index = _teams.indexOfFirst { it.id == updatedTeam.id }
        if (index != -1) {
            _teams[index] = updatedTeam
        }
    }
}
