package org.hammel.paintpdf.ui.tools

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import paintpdf.R
import org.hammel.paintpdf.tools.FontType

class FontListAdapter internal constructor(
    context: Context,
    private val fontTypes: List<FontType>,
    private val onFontChanged: (FontType) -> Unit
) : RecyclerView.Adapter<FontListAdapter.ViewHolder>() {
    private val mInflater: LayoutInflater = LayoutInflater.from(context)
    private var selectedIndex = 0

    private val sansSerif = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    private val monospace = Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL)
    private val serif = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
    private val dubai = ResourcesCompat.getFont(context, R.font.dubai)
    private val stc = ResourcesCompat.getFont(context, R.font.stc_regular)

    private val typeFaces = arrayOf(
        sansSerif,
        monospace,
        serif,
        dubai,
        stc
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = mInflater.inflate(R.layout.pocketpaint_item_font, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val font = fontTypes[position]
        holder.fontChip.setText(font.nameResource)
        holder.fontChip.typeface = typeFaces[position]
        holder.fontChip.isChecked = position == selectedIndex
    }

    override fun getItemCount(): Int = fontTypes.size

    fun setSelectedIndex(selectedIndex: Int) {
        val oldSelectedIndex = this.selectedIndex
        this.selectedIndex = selectedIndex
        notifyItemChanged(oldSelectedIndex)
        notifyItemChanged(selectedIndex)
    }

    fun getSelectedItem(): FontType = fontTypes[selectedIndex]

    inner class ViewHolder internal constructor(itemView: View) :
        RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var fontChip: Chip = itemView.findViewById(R.id.pdfPaint_font_type)

        init {
            fontChip.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            setSelectedIndex(layoutPosition)
            onFontChanged(fontTypes[layoutPosition])
        }
    }
}
