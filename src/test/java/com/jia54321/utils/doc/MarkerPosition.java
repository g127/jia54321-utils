package com.jia54321.utils.doc;

import com.jia54321.utils.doc.wordCfg.PositionType;
import lombok.Data;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;

@Data
public class MarkerPosition {
    private PositionType type;
    private int paragraphIndex = -1;
    private int tableIndex = -1;
    private int rowIndex = -1;
    private int cellIndex = -1;
    private String markerText;
    private XWPFParagraph paragraph;
    private XWPFTableCell tableCell;

}
