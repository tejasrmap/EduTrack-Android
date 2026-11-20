import docx
from docx import Document
from docx.shared import Inches, Pt, RGBColor
from docx.enum.text import WD_ALIGN_PARAGRAPH
from docx.enum.table import WD_TABLE_ALIGNMENT
from docx.oxml import OxmlElement, parse_xml
from docx.oxml.ns import nsdecls, qn
import os

def create_table_helpers(doc):
    def set_cell_props(cell, fill_hex=None, top_m=90, bottom_m=90, left_m=130, right_m=130):
        tcPr = cell._tc.get_or_add_tcPr()
        if fill_hex:
            shd = parse_xml(f'<w:shd {nsdecls("w")} w:fill="{fill_hex}"/>')
            tcPr.append(shd)
        tcMar = parse_xml(f'''
            <w:tcMar {nsdecls("w")}>
                <w:top w:w="{top_m}" w:type="dxa"/>
                <w:left w:w="{left_m}" w:type="dxa"/>
                <w:bottom w:w="{bottom_m}" w:type="dxa"/>
                <w:right w:w="{right_m}" w:type="dxa"/>
            </w:tcMar>
        ''')
        tcPr.append(tcMar)

    def set_table_borders(table, color="000000", sz="6"):
        tblPr = table._tbl.tblPr
        borders = parse_xml(f'''
            <w:tblBorders {nsdecls("w")}>
                <w:top w:val="single" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:left w:val="single" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:bottom w:val="single" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:right w:val="single" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:insideH w:val="single" w:sz="{sz}" w:space="0" w:color="{color}"/>
                <w:insideV w:val="single" w:sz="{sz}" w:space="0" w:color="{color}"/>
            </w:tblBorders>
        ''')
        tblPr.append(borders)

    def add_styled_table(headers, rows_data, col_widths=None):
        table = doc.add_table(rows=len(rows_data) + 1, cols=len(headers))
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        set_table_borders(table)

        # Header Row
        hdr_cells = table.rows[0].cells
        for idx, h_text in enumerate(headers):
            cell = hdr_cells[idx]
            p = cell.paragraphs[0]
            p.paragraph_format.space_before = Pt(2)
            p.paragraph_format.space_after = Pt(2)
            p.paragraph_format.line_spacing = 1.15
            run = p.add_run(h_text)
            run.font.name = 'Times New Roman'
            run.font.size = Pt(11)
            run.font.bold = True
            set_cell_props(cell, fill_hex="F1F5F9")
            if col_widths and idx < len(col_widths):
                cell.width = Inches(col_widths[idx])

        # Data Rows
        for r_idx, r_data in enumerate(rows_data):
            row_cells = table.rows[r_idx + 1].cells
            for c_idx, val in enumerate(r_data):
                cell = row_cells[c_idx]
                p = cell.paragraphs[0]
                p.paragraph_format.space_before = Pt(2)
                p.paragraph_format.space_after = Pt(2)
                p.paragraph_format.line_spacing = 1.15
                run = p.add_run(str(val))
                run.font.name = 'Times New Roman'
                run.font.size = Pt(10.5)
                set_cell_props(cell, fill_hex=None)
                if col_widths and c_idx < len(col_widths):
                    cell.width = Inches(col_widths[c_idx])

        doc.add_paragraph().paragraph_format.space_after = Pt(4)
        return table

    def add_code_box(code_text):
        table = doc.add_table(rows=1, cols=1)
        table.alignment = WD_TABLE_ALIGNMENT.CENTER
        cell = table.rows[0].cells[0]
        set_cell_props(cell, fill_hex="1E1F22", top_m=100, bottom_m=100, left_m=140, right_m=140)
        set_table_borders(table, color="334155", sz="8")
        cell.width = Inches(6.8)

        # Number lines like LabRescue
        lines = code_text.strip().splitlines()
        numbered_lines = []
        for i, l in enumerate(lines, 1):
            numbered_lines.append(f"{i:4d}  {l}")
        fmt_text = "\n".join(numbered_lines)

        p = cell.paragraphs[0]
        p.paragraph_format.space_before = Pt(2)
        p.paragraph_format.space_after = Pt(2)
        p.paragraph_format.line_spacing = 1.05
        run = p.add_run(fmt_text)
        run.font.name = 'Courier New'
        run.font.size = Pt(8.5)
        run.font.color.rgb = RGBColor(220, 225, 235)

        doc.add_paragraph().paragraph_format.space_after = Pt(6)
        return table

    return add_styled_table, add_code_box, set_cell_props, set_table_borders
