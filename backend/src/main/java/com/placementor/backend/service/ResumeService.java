package com.placementor.backend.service;

import com.placementor.backend.entity.Resume;
import com.placementor.backend.repository.ResumeRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class ResumeService {

    private static final Logger log = LoggerFactory.getLogger(ResumeService.class);

    private final ResumeRepository resumeRepository;

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    public Map<String, Object> uploadResume(Long userId, MultipartFile file) {
        String extractedText = extractTextFromPdf(file);

        File uploadsDir = new File("uploads");
        if (!uploadsDir.exists()) {
            uploadsDir.mkdirs();
        }

        try {
            File savedFile = new File(uploadsDir, file.getOriginalFilename());
            file.transferTo(savedFile);
        } catch (IOException e) {
            log.warn("Could not save copy of uploaded PDF file to disk", e);
        }

        Resume resume = resumeRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        if (resume != null) {
            resume.setExtractedText(extractedText);
            resume = resumeRepository.save(resume);
        } else {
            resume = Resume.builder()
                    .userId(userId)
                    .extractedText(extractedText)
                    .build();
            resume = resumeRepository.save(resume);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", resume.getId());
        response.put("filename", file.getOriginalFilename());
        response.put("uploaded_at", LocalDateTime.now().toString());
        response.put("extracted_text", resume.getExtractedText());

        return response;
    }

    public Map<String, Object> getResume(Long userId) {
        Resume resume = resumeRepository.findTopByUserIdOrderByIdDesc(userId).orElse(null);
        if (resume == null) {
            return null;
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", resume.getId());
        response.put("filename", "resume.pdf");
        response.put("uploaded_at", LocalDateTime.now().toString());
        response.put("extracted_text", resume.getExtractedText());

        return response;
    }

    private String extractTextFromPdf(MultipartFile file) {
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            PDFTextStripper pdfStripper = new PDFTextStripper();
            return pdfStripper.getText(document);
        } catch (IOException e) {
            log.error("Failed to extract text from PDF", e);
            return "";
        }
    }
}
