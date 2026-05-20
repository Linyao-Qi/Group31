package TA;

import TaJob.JobPosting;
import TaJob.TaJobService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/ta/resumeSuggestion")
public class ResumeSuggestionServlet
        extends HttpServlet {

    private final ResumeSuggestionService
            suggestionService =
            new ResumeSuggestionService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        /*
         * Get current TA id
         */

        String taId =
                (String) request.getSession()
                        .getAttribute("taId");

        /*
         * Load TA profile
         */

        TAProfile profile =
                TAProfileService
                        .getProfileByTaId(taId);

        /*
         * Safety check
         */

        if (profile == null) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/ta/profile"
            );

            return;
        }

        /*
         * Create job service
         */

        String csvPath =
                request.getServletContext()
                        .getRealPath(
                                "/data/job.csv"
                        );

        TaJobService jobService =
                new TaJobService(csvPath);

        /*
         * Load jobs
         */

        List<JobPosting> jobs =
                jobService.getAllJobs();

        /*
         * Safety check
         */

        if (jobs.isEmpty()) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    "No jobs found."
            );

            return;
        }

        /*
         * TEMP:
         * Use first job
         */

        JobPosting job =
                jobs.get(0);

        /*
         * Get skills
         */

        String taSkills =
                profile.getSkills();

        String jobSkills =
                job.getSkillRequirement();

        /*
         * CV text
         */

        String cvText = "";

        if (profile.getCvPath() != null) {

            cvText =
                    profile.getCvPath();
        }

        /*
         * Generate suggestion
         */

        ResumeSuggestion suggestion =
                suggestionService
                        .generateSuggestion(
                                jobSkills,
                                taSkills,
                                cvText
                        );

        /*
         * Send to JSP
         */

        request.setAttribute(
                "suggestion",
                suggestion
        );

        request.getRequestDispatcher(
                "/jsp/TA/resumeSuggestion.jsp"
        ).forward(request, response);
    }
}