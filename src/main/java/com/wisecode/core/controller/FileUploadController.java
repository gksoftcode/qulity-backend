package com.wisecode.core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.wisecode.core.conf.secuirty.CurrentUser;
import com.wisecode.core.entities.Attachment;
import com.wisecode.core.entities.User;
import com.wisecode.core.model.FileUploadDtls;
import com.wisecode.core.repositories.AttachmentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;

@CrossOrigin(origins = "*", allowedHeaders = "*",methods = {RequestMethod.GET,
        RequestMethod.HEAD,
        RequestMethod.OPTIONS,
        RequestMethod.DELETE,
        RequestMethod.POST,
        RequestMethod.PUT})
@RestController
@RequestMapping("/api/common/fileUploader")
@Log4j2
public class FileUploadController {

@Autowired
AttachmentRepository repository;

    @CrossOrigin
    @PostMapping("uploadFile")
    public ResponseEntity<FileUploadDtls> uploadFile(@RequestParam("file") MultipartFile file,
                                                     @RequestParam("name") String name,
                                                     @RequestParam("seq") Integer seq,
                                                     RedirectAttributes redirectAttributes,  @CurrentUser User user) {

        if(file != null && !file.isEmpty()){
            try{
                FileUploadDtls dtls = new FileUploadDtls();
                File temp = File.createTempFile("quality",null);
                file.transferTo(temp);
                dtls.setFilePath(temp.getPath());
                dtls.setFileType(file.getContentType());
                dtls.setName(name);
                dtls.setSeq(seq);
                dtls.setOriginalFileName(file.getOriginalFilename());
                return ResponseEntity.ok(dtls);
            }catch
            (Exception ex){
                return ResponseEntity.badRequest().body(null);
            }
        }
        return ResponseEntity.badRequest().body(null);
    }

    @GetMapping(
            value = "/getAttachment/{uuid}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<Resource> getAttachmentWithMediaType(@PathVariable String uuid) {
        Attachment atta = repository.findByUuid(uuid);
        HttpHeaders header = new HttpHeaders();
        try {
            String fileName = URLEncoder.encode(atta.getFileName(), "UTF-8" );
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        }catch (Exception ex){
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + atta.getFileName());
        }
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        ByteArrayResource resource = new ByteArrayResource(atta.getFileData());
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(atta.getFileData().length)
                .contentType(MediaType.parseMediaType(atta.getMimeType()))
                .body(resource);
    }

    @GetMapping(
            value = "/getTempAttachment/{uuid}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE
    )
    public ResponseEntity<Resource> getTempAttachmentWithMediaType(@PathVariable String uuid) {
        Attachment atta = repository.findByUuid(uuid);
        HttpHeaders header = new HttpHeaders();
        try {
            String fileName = URLEncoder.encode(atta.getFileTempName(), "UTF-8" );
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        }catch (Exception ex){
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + atta.getFileTempName());
        }
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        ByteArrayResource resource = new ByteArrayResource(atta.getTempFileData());
        return ResponseEntity.ok()
                .headers(header)
                .contentLength(atta.getTempFileData().length)
                .contentType(MediaType.parseMediaType(atta.getMimeTempType()))
                .body(resource);
    }
    @GetMapping(
            value = "/getPdfAttachment/{uuid}",
            produces = MediaType.APPLICATION_PDF_VALUE
    )
    public ResponseEntity<Resource> getPdfAttachmentWithMediaType(@PathVariable String uuid) {
        Attachment atta = repository.findByUuid(uuid);
        HttpHeaders header = new HttpHeaders();
        try {
            String fileName = URLEncoder.encode(atta.getFilePdfName(), "UTF-8" );
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);
        }catch (Exception ex){
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + atta.getFilePdfName());
        }
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");
        if(atta.getStatus() == AttachmentController.STATUS_CANCELED){
            try {
                byte[] bytes = atta.getPdfFileData();
                PdfReader reader = new PdfReader(bytes);
                int n = reader.getNumberOfPages();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PdfStamper stamper = new PdfStamper(reader, out);
                BaseFont bf = BaseFont.createFont(
                        "c:/windows/fonts/arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                Font font = new Font(bf, 20);
                //Font f = new Font(Font.FontFamily., 6.0F);
                font.setColor(255,0,0);
                PdfGState gs1 = new PdfGState();
                gs1.setFillOpacity(0.3F);
                for (int i = 1; i <= n; i++) {
                    Rectangle pagesize = reader.getPageSizeWithRotation(i);
                    float fh = pagesize.getHeight();
                    float fw = pagesize.getWidth();
                    float fq = (float) Math.sqrt(Math.pow(fh, 2.0D) + Math.pow(fw, 2.0D));
                    PdfContentByte over = stamper.getOverContent(i);
                    over.getPdfWriter().setRunDirection(PdfWriter.RUN_DIRECTION_RTL);
                    over.saveState();
                    over.setGState(gs1);
                    StringBuilder sb = new StringBuilder();
                    for (int x = 0; x < 100; x++) {
                        sb.append("ملغي");
                        sb.append("    ");
                    }
                    for (int j = 50; j < fq; j += 50) {
                        Phrase p = new Phrase(sb.toString(), font);
                        ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 0.0F, j, 45.0F,PdfWriter.RUN_DIRECTION_RTL,1);
                    }
                    for (int j = 0; j < fq; j += 50) {
                        Phrase p = new Phrase(sb.toString(), font);
                        ColumnText.showTextAligned(over, Element.ALIGN_CENTER, p, 0.0F, j, 45.0F,PdfWriter.RUN_DIRECTION_RTL,1);
                    }
                }
                stamper.close();
                reader.close();
                ByteArrayResource resource = new ByteArrayResource(out.toByteArray());
                return ResponseEntity.ok()
                        .headers(header)
                        .contentLength(out.toByteArray().length)
                        .contentType(MediaType.parseMediaType(atta.getMimePdfType()))
                        .body(resource);
            } catch (Exception e) {
               return ResponseEntity.badRequest().build();
            }
        }else {
            ByteArrayResource resource = new ByteArrayResource(atta.getPdfFileData());
            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(atta.getPdfFileData().length)
                    .contentType(MediaType.parseMediaType(atta.getMimePdfType()))
                    .body(resource);
        }
    }

}