package edu.univ.erp.util;

import edu.univ.erp.data.*;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import edu.univ.erp.domain.Grade;
import edu.univ.erp.domain.Enrollment;
import java.io.File;


public class ReportsUtil {

    public static String exportEnrollmentsCSV(int sectionId) {
        String csv = GradeDAO.exportGradesCSV(sectionId);
        return csv;
    }


    public static boolean exportEnrollmentsCSVToFile(int sectionId, File file) {
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(exportEnrollmentsCSV(sectionId));
            return true;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }


    public static boolean exportEnrollmentsPDF(int sectionId, File file) {
        try {
            try {
                String csv = exportEnrollmentsCSV(sectionId);
                List<String> lines = java.util.Arrays.asList(csv.split("\n"));

                Class<?> pdDocClass = Class.forName("org.apache.pdfbox.pdmodel.PDDocument");
                Class<?> pdPageClass = Class.forName("org.apache.pdfbox.pdmodel.PDPage");
                Class<?> pdPageContentStreamClass = Class.forName("org.apache.pdfbox.pdmodel.PDPageContentStream");
                Class<?> pdType1FontClass = Class.forName("org.apache.pdfbox.pdmodel.font.PDType1Font");

                Object doc = pdDocClass.getDeclaredConstructor().newInstance();
                Object page = pdPageClass.getDeclaredConstructor().newInstance();
                pdDocClass.getMethod("addPage", pdPageClass).invoke(doc, page);

                Object cs = pdPageContentStreamClass.getConstructor(pdDocClass, pdPageClass).newInstance(doc, page);
                Object font = pdType1FontClass.getField("HELVETICA").get(null);

                float y = 750f;
                for (String row : lines) {
                    pdPageContentStreamClass.getMethod("beginText").invoke(cs);
                    pdPageContentStreamClass.getMethod("setFont", Class.forName("org.apache.pdfbox.pdmodel.font.PDFont"), float.class).invoke(cs, font, 10f);
                    pdPageContentStreamClass.getMethod("newLineAtOffset", float.class, float.class).invoke(cs, 50f, y);
                    pdPageContentStreamClass.getMethod("showText", String.class).invoke(cs, row);
                    pdPageContentStreamClass.getMethod("endText").invoke(cs);
                    y -= 12;
                    if (y < 40) {
                        // new page
                        pdPageContentStreamClass.getMethod("close").invoke(cs);
                        page = pdPageClass.getDeclaredConstructor().newInstance();
                        pdDocClass.getMethod("addPage", pdPageClass).invoke(doc, page);
                        cs = pdPageContentStreamClass.getConstructor(pdDocClass, pdPageClass).newInstance(doc, page);
                        y = 750f;
                    }
                }
                pdPageContentStreamClass.getMethod("close").invoke(cs);
                pdDocClass.getMethod("save", File.class).invoke(doc, file);
                pdDocClass.getMethod("close").invoke(doc);
                return true;
            } catch (ClassNotFoundException cnf) {
                return exportEnrollmentsCSVToFile(sectionId, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

