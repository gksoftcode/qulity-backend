/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wisecode.core.controller;

import com.wisecode.core.entities.Attachment;
import com.wisecode.core.entities.WorkGuide;
import com.wisecode.core.repositories.AttachmentRepository;
import com.wisecode.core.repositories.WorkGuideRepository;
import com.wisecode.core.util.SystemUtil;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Mahesh
 */
@RestController
@RequestMapping("/api/report")
@Log4j2
public class ReportController {

    @Autowired
    WorkGuideRepository repository;

    @Autowired
    AttachmentRepository attachmentRepository;

    @PostMapping("/workguide/{id}")
    public @ResponseBody
    Map<String, Object> generateWorkProcedureReport(@PathVariable Long id) throws IOException, JRException {
        Map<String, Object> m = new HashMap<>();
        try {

            Map<String, Object> params = new HashMap<>();
            WorkGuide workguide = repository.getOne(id);
            params.put("procedure", workguide);

            params.put("attachment", attachmentRepository.findByDepartmentIdAndStatusOrderByOrderNo(workguide.getDepartmentId(), AttachmentController.STATUS_APPROVED));

            InputStream header = Thread.currentThread().getContextClassLoader().getResourceAsStream("jasper/images/HM_logo.jpg");
            params.put("header", header);

            InputStream tick = Thread.currentThread().getContextClassLoader().getResourceAsStream("jasper/images/tick.jpg");
            params.put("tick", tick);

            InputStream jasperStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("jasper/work_procedure.jasper");

            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperStream, params, new JREmptyDataSource());

            m.put("file", JasperExportManager.exportReportToPdf(jasperPrint));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return m;

    }
}
