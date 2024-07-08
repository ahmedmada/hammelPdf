package org.hammel.paintpdf.command

import org.hammel.paintpdf.model.CommandManagerModel

interface CommandManager {
    val isUndoAvailable: Boolean
    val isRedoAvailable: Boolean
    val lastExecutedCommand: Command?
    val isBusy: Boolean
    val commandManagerModel: CommandManagerModel?

    fun addCommandListener(commandListener: CommandListener)

    fun removeCommandListener(commandListener: CommandListener)

    fun addCommand(command: Command?)

    fun addCommandWithoutUndo(command: Command?)

    fun setInitialStateCommand(command: Command)

    fun loadCommandsCatrobatImage(model: CommandManagerModel?)

    fun undo()

    fun redo()

    fun reset()

    fun shutdown()

    fun undoIgnoringColorChanges()

    fun undoIgnoringColorChangesAndAddCommand(command: Command)

    fun undoInConnectedLinesMode()

    fun redoInConnectedLinesMode()

    fun getCommandManagerModelForCatrobatImage(): CommandManagerModel?

    fun adjustUndoListForClippingTool()

    fun undoInClippingTool()

    fun popFirstCommandInUndo()

    fun popFirstCommandInRedo()

    fun executeAllCommands()

    fun getUndoCommandCount(): Int

    fun getColorCommandCount(): Int

    fun isLastColorCommandOnTop(): Boolean

    interface CommandListener {
        fun commandPostExecute()
    }
}
