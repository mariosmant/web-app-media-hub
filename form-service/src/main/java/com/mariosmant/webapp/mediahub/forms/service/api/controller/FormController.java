package com.mariosmant.webapp.mediahub.forms.service.api.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mariosmant.webapp.mediahub.forms.service.domain.dto.FormSubmitRequest;
import com.mariosmant.webapp.mediahub.forms.service.domain.service.FormService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forms")
public class FormController {

    private final FormService formService;

    public FormController(FormService formService) {
        this.formService = formService;
    }

    // 1) application/json: pure DTO
    @PostMapping(
            path = "/submit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> submitJson(
            @Valid @RequestBody FormSubmitRequest formSubmitRequest
    ) {
        return ResponseEntity.ok().body(formService.submitForm(formSubmitRequest));
    }

    // 2) multipart/form-data: DTO + optional file(s)
    @PostMapping(
            path = "/submit",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<?> submitMultipart(
            @Valid @RequestPart("form") FormSubmitRequest formSubmitRequest,
            @RequestPart Map<String, List<MultipartFile>> files
    ) {
        return ResponseEntity.ok().body(formService.submitFormMultipart(formSubmitRequest, files));
    }
}
