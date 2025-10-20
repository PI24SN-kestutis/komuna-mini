package eu.minted.komuna.rest;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.model.Fee;
import eu.minted.komuna.service.CommunityService;
import eu.minted.komuna.service.FeeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CommunityService communityService;
    private final FeeService feeService;

    public ApiController(CommunityService communityService, FeeService feeService) {
        this.communityService = communityService;
        this.feeService = feeService;
    }

    @GetMapping("/communities")
    public List<Community> getAllCommunities() {
        return communityService.findAll();
    }

    @GetMapping("/fees")
    public List<Fee> getAllFees() {
        return feeService.findAll();
    }
}
