package org.hammel.paintpdf.model

import org.hammel.paintpdf.command.Command

data class CommandManagerModel(val initialCommand: Command, val commands: MutableList<Command>)
