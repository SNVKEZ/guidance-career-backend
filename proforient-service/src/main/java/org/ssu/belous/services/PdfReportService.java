package org.ssu.belous.services;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.ssu.belous.dto.*;
import org.ssu.belous.utils.PdfBackgroundEvent;

import java.awt.*;
import java.io.ByteArrayOutputStream;

@Service
public class PdfReportService {

    public byte[] generate(TestAnalysisResult result, String testTitle) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document document = new Document(PageSize.A4);
            PdfWriter writer = PdfWriter.getInstance(document, out);
            writer.setPageEvent(new PdfBackgroundEvent());

            document.open();

            BaseFont baseFont = BaseFont.createFont(
                    "/fonts/timesnewromanpsmt.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED
            );

            Color black = Color.BLACK;
            Color white = Color.WHITE;

            Font titleFont = new Font(baseFont, 20, Font.BOLD | Font.ITALIC, black);
            Font headerFont = new Font(baseFont, 16, Font.BOLD | Font.ITALIC, black);
            Font headeronMainFont = new Font(baseFont, 16, Font.ITALIC, black);
            Font dimensionFont = new Font(baseFont, 16, Font.ITALIC, black);
            Font textFont = new Font(baseFont, 12, Font.NORMAL, black);
            Font linkFont = new Font(baseFont, 11, Font.UNDERLINE, white);


            Paragraph spacerTop = new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n");
            document.add(spacerTop);

            /* Заголовок */
            Paragraph title = new Paragraph(testTitle, titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);



            /* ---------- Вводное описание ---------- */
            if (!result.dimensions().isEmpty()) {
                DimensionResult main = result.dimensions().get(0);

                if (main.description() != null && !main.description().isBlank()) {
                    Paragraph intro = new Paragraph(
                            "На основе ваших ответов выделена наиболее выраженная характеристика:\n\n"
                                    + main.description(),
                            headeronMainFont
                    );
                    intro.setAlignment(Element.ALIGN_CENTER);
                    intro.setSpacingAfter(30);
                    document.add(intro);
                }
            }

            /* ---------- Новая страница ---------- */
            document.setMargins(40, 140, 40, 40);
            document.newPage();


            /* ---------- Основная склонность ---------- */
            if (!result.dimensions().isEmpty()) {
                Paragraph mainHeader = new Paragraph(
                        "Факультеты и направления для Вашей более выраженной склонности",
                        headerFont
                );
                mainHeader.setSpacingBefore(10);
                mainHeader.setSpacingAfter(15);
                document.add(mainHeader);

                renderDimension(
                        document,
                        result.dimensions().get(0),
                        dimensionFont,
                        textFont,
                        linkFont
                );
            }

            document.newPage();

            /* ---------- Менее выраженные склонности ---------- */
            if (result.dimensions().size() > 1) {
                Paragraph secondaryHeader = new Paragraph(
                        "Факультеты и направления для Ваших менее выраженных склонностей",
                        headerFont
                );
                secondaryHeader.setSpacingBefore(20);
                secondaryHeader.setSpacingAfter(15);
                document.add(secondaryHeader);

                for (int i = 1; i < result.dimensions().size(); i++) {
                    renderDimension(
                            document,
                            result.dimensions().get(i),
                            dimensionFont,
                            textFont,
                            linkFont
                    );
                }
            }

            document.newPage();

            /* ---------- Ответы пользователя ---------- */
            Paragraph qaHeader = new Paragraph("Ваши ответы:", headerFont);
            qaHeader.setSpacingBefore(30);
            qaHeader.setSpacingAfter(20);
            document.add(qaHeader);

            int index = 1;
            for (QuestionAnswerResult qa : result.questionAnswers()) {
                Paragraph q = new Paragraph(index + ". " + qa.questionText(), textFont);
                q.setSpacingAfter(4);
                document.add(q);

                Paragraph a = new Paragraph("Ответ: " + qa.answerText(), textFont);
                a.setIndentationLeft(15);
                a.setSpacingAfter(12);
                document.add(a);

                index++;
            }

            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка генерации PDF", e);
        }
    }

    /* ---------- Рендер одной характеристики ---------- */
    private void renderDimension(
            Document document,
            DimensionResult dim,
            Font headerFont,
            Font textFont,
            Font linkFont
    ) throws DocumentException {

        Paragraph dimTitle = new Paragraph(
                dim.dimensionName() + " — " + dim.selectedCount() + " ответов",
                headerFont
        );
        dimTitle.setSpacingBefore(10);
        dimTitle.setSpacingAfter(8);
        document.add(dimTitle);

        if (dim.faculties().isEmpty()) {
            Paragraph empty = new Paragraph(
                    "Для этой характеристики нет привязанных факультетов или направлений.",
                    textFont
            );
            empty.setIndentationLeft(15);
            empty.setSpacingAfter(10);
            document.add(empty);
            return;
        }

        for (FacultyDto faculty : dim.faculties()) {
            Paragraph facultyName = new Paragraph("• " + faculty.getName(), textFont);
            facultyName.setIndentationLeft(15);
            facultyName.setSpacingAfter(2);
            document.add(facultyName);

            if (faculty.getLink() != null && !faculty.getLink().isBlank()) {
                Anchor link = new Anchor(faculty.getLink(), linkFont);
                link.setReference(faculty.getLink());
                Paragraph linkP = new Paragraph(link);
                linkP.setIndentationLeft(30);
                linkP.setSpacingAfter(4);
                document.add(linkP);
            }

            for (DirectionDto dir : faculty.getDirections()) {
                Paragraph dirP = new Paragraph("– " + dir.getName(), textFont);
                dirP.setIndentationLeft(30);
                dirP.setSpacingAfter(2);
                document.add(dirP);
            }
        }

        document.add(Chunk.NEWLINE);
    }
}
