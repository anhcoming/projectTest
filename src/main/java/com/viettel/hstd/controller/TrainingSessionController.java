package com.viettel.hstd.controller;

import com.viettel.hstd.core.dto.BaseResponse;
import com.viettel.hstd.core.utils.SearchUtils;
import com.viettel.hstd.dto.hstd.TrainingSessionDTO;
import com.viettel.hstd.service.inf.TrainingSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TrainingSessionController {

    private final TrainingSessionService trainingSessionService;

    @PostMapping("/v1/training-sessions")
    public BaseResponse<Boolean> create(@RequestBody TrainingSessionDTO.Request request) {
        request.setId(null); //create
        trainingSessionService.save(request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);
    }


    @PostMapping("/v1/training-sessions/search")
    public BaseResponse<List<TrainingSessionDTO.Response>> search(@RequestBody TrainingSessionDTO.SearchCriteria searchCriteria) {
        return SearchUtils.getResponseFromPage(trainingSessionService.search(searchCriteria));
    }

    @PutMapping("/v1/training-sessions/{id}")
    public BaseResponse<Boolean> update(@RequestBody TrainingSessionDTO.Request request, @PathVariable Long id) {
        request.setId(id); //update
        trainingSessionService.save(request);
        return new BaseResponse
                .ResponseBuilder<Boolean>()
                .success(true);
    }

    @PostMapping("/v1/training-sessions/{id}/delete")
    public BaseResponse<Void> delete(@PathVariable Long id) {
        trainingSessionService.delete(id);
        return new BaseResponse
                .ResponseBuilder<Void>()
                .success(null);
    }
}
