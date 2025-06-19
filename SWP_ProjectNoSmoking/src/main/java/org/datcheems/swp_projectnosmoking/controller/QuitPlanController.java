package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.QuitPlanService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quit-plan")
@CrossOrigin(origins = {"http://localhost:5175", "http://localhost:3000"})
public class QuitPlanController {

    private final QuitPlanService quitPlanService;

    public QuitPlanController(QuitPlanService quitPlanService) {
        this.quitPlanService = quitPlanService;
    }

    // Coach tạo kế hoạch cho member
    @PreAuthorize("hasRole('COACH')")
    @PostMapping
    public ResponseEntity<ResponseObject<QuitPlanResponse>> createQuitPlan(@RequestBody QuitPlanRequest request) {
        QuitPlanResponse responseData = quitPlanService.createQuitPlan(request);

        ResponseObject<QuitPlanResponse> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Quit plan created successfully");
        response.setData(responseData);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
