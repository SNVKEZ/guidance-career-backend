package org.ssu.belous.utils;

import com.lowagie.text.Document;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.InputStream;

public class PdfBackgroundEvent extends PdfPageEventHelper {

    private Image logo;

    public PdfBackgroundEvent() {
        try {
            InputStream logoStream =
                    getClass().getResourceAsStream("/static/images/sguLogo.png");

            if (logoStream != null) {
                logo = Image.getInstance(logoStream.readAllBytes());
                logo.scaleToFit(90, 90);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки логотипа", e);
        }
    }

    @Override
    public void onEndPage(PdfWriter writer, Document document) {
        PdfContentByte canvas = writer.getDirectContentUnder();

        Color bg = new Color(189, 162, 231, 204);
        canvas.saveState();
        canvas.setColorFill(bg);
        canvas.rectangle(
                0,
                0,
                document.getPageSize().getWidth(),
                document.getPageSize().getHeight()
        );
        canvas.fill();
        canvas.restoreState();

        if (logo != null) {
            try {
                logo.setAbsolutePosition(document.getPageSize().getWidth() - 110, document.getPageSize().getHeight() - 110);
                PdfContentByte over = writer.getDirectContent();
                over.addImage(logo);
            } catch (Exception ignored) {
            }
        }
    }
}
