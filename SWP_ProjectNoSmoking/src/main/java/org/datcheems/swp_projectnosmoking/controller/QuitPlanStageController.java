package org.datcheems.swp_projectnosmoking.controller;

import org.datcheems.swp_projectnosmoking.dto.request.QuitPlanStageRequest;
import org.datcheems.swp_projectnosmoking.dto.response.QuitPlanStageResponse;
import org.datcheems.swp_projectnosmoking.dto.response.ResponseObject;
import org.datcheems.swp_projectnosmoking.service.QuitPlanStageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quit-plan-stages")
@CrossOrigin(origins = {"http://localhost:5175", "http://localhost:3000"})
public class QuitPlanStageController {

    private final QuitPlanStageService stageService;

    public QuitPlanStageController(QuitPlanStageService stageService) {
        this.stageService = stageService;
    }

    // API tạo giai đoạn mới cho kế hoạch
    @PostMapping
    public ResponseEntity<ResponseObject<String>> createStage(@RequestBody QuitPlanStageRequest request) {
        stageService.createStage(request);

        ResponseObject<String> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("Quit plan stage created successfully");
        response.setData("Stage created");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // API lấy danh sách các giai đoạn theo kế hoạch
    @GetMapping("/{quitPlanId}")
    public ResponseEntity<ResponseObject<List<QuitPlanStageResponse>>> getStages(@PathVariable Integer quitPlanId) {
        List<QuitPlanStageResponse> stageList = stageService.getStagesByPlanId(quitPlanId);

        ResponseObject<List<QuitPlanStageResponse>> response = new ResponseObject<>();
        response.setStatus("success");
        response.setMessage("List of quit plan stages");
        response.setData(stageList);

        return ResponseEntity.ok(response);
    }
}
