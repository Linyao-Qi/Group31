package com;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/publishJob")
public class PublishJobServlet extends HttpServlet {
    private MoService moService = new MoService();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String moId = request.getParameter("moId");
        String jobName = request.getParameter("jobName");
        String jobRequirements = request.getParameter("jobRequirements");

        Job job = moService.publishJob(moId, jobName, jobRequirements);

        if (job != null) {
            request.setAttribute("msg", "岗位发布成功！岗位ID：" + job.getJobId());
        } else {
            request.setAttribute("msg", "发布失败！参数不能为空/格式错误");
        }
        request.getRequestDispatcher("/publishJob.jsp").forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}