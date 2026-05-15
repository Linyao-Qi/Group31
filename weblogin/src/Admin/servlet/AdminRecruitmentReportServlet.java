package Admin.servlet;

import Admin.AdminRecruitmentReportService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class AdminRecruitmentReportServlet extends HttpServlet {
    private final AdminRecruitmentReportService reportService = new AdminRecruitmentReportService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!AdminWebAuthGuard.ensureAuthenticated(req, resp)) {
            return;
        }

        try {
            AdminRecruitmentReportService.ReportData reportData = reportService.buildReport();
            if ("csv".equalsIgnoreCase(req.getParameter("export"))) {
                exportCsv(resp, reportData);
                return;
            }
            if ("successful".equalsIgnoreCase(req.getParameter("export"))) {
                exportSuccessfulRecruitmentCsv(resp, reportData);
                return;
            }

            req.setAttribute("summary", reportData.getSummary());
            req.setAttribute("reportItems", reportData.getItems());
            req.setAttribute("applicationStatusCounts", reportData.getApplicationStatusCounts());
            req.setAttribute("keyFindings", reportData.getKeyFindings());
            req.setAttribute("successfulRecruitmentDetails", reportData.getSuccessfulRecruitmentDetails());
            req.setAttribute("generatedAt", reportData.getGeneratedAt());
        } catch (IOException ex) {
            req.setAttribute("error", "Failed to generate report: " + ex.getMessage());
        }

        req.getRequestDispatcher("/jsp/admin/report.jsp").forward(req, resp);
    }

    private void exportCsv(HttpServletResponse resp, AdminRecruitmentReportService.ReportData reportData)
            throws IOException {
        byte[] csvBytes = reportService.buildCsv(reportData).getBytes(StandardCharsets.UTF_8);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"recruitment_statistics_report.csv\"");
        resp.setContentLength(csvBytes.length);
        resp.getOutputStream().write(csvBytes);
    }

    private void exportSuccessfulRecruitmentCsv(
            HttpServletResponse resp,
            AdminRecruitmentReportService.ReportData reportData
    ) throws IOException {
        byte[] csvBytes = reportService.buildSuccessfulRecruitmentCsv(reportData).getBytes(StandardCharsets.UTF_8);
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/csv;charset=UTF-8");
        resp.setHeader("Content-Disposition", "attachment; filename=\"successful_recruitment_report.csv\"");
        resp.setContentLength(csvBytes.length);
        resp.getOutputStream().write(csvBytes);
    }
}
