package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.service.CommunityService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/communities")
public class CommunityController {

    private final CommunityService communityService;

    public CommunityController(CommunityService communityService) {
        this.communityService = communityService;
    }

    @GetMapping
    public List<Community> getAll() {
        return communityService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Community> getById(@PathVariable Long id) {
        return communityService.findById(id);
    }

    // ===========================
    // CREATE COMMUNITY
    // ===========================
    @PostMapping(value = "", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> createCommunity(@RequestBody Community community) {
        try {
            if (community.getCode() == null || community.getCode().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("error", "Bendrijos kodas privalomas."));
            if (community.getName() == null || community.getName().isEmpty())
                return ResponseEntity.badRequest().body(Map.of("error", "Bendrijos pavadinimas privalomas."));

            Optional<Community> existing = communityService.findByCode(community.getCode());
            if (existing.isPresent())
                return ResponseEntity.badRequest().body(Map.of("error", "Toks bendrijos kodas jau egzistuoja."));

            communityService.save(community);
            return ResponseEntity.ok(Map.of("success", "Bendrija sėkmingai sukurta."));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }


    // ===========================
    // UPDATE COMMUNITY
    // ===========================
    @PutMapping("/{id}")
    @ResponseBody
    public String updateCommunity(@PathVariable Long id, @RequestBody Community updatedCommunity) {
        Optional<Community> existingOpt = communityService.findById(id);
        if (existingOpt.isEmpty()) {
            return "{\"error\":\"Bendrija nerasta.\"}";
        }

        Community community = existingOpt.get();
        community.setCode(updatedCommunity.getCode());
        community.setName(updatedCommunity.getName());
        community.setAddress(updatedCommunity.getAddress());

        communityService.save(community);
        return "{\"success\":\"Bendrija atnaujinta sėkmingai.\"}";
    }

    // ===========================
    // DELETE COMMUNITY
    // ===========================
    @DeleteMapping("/{id}")
    @ResponseBody
    public String deleteCommunity(@PathVariable Long id) {
        try {
            Optional<Community> existing = communityService.findById(id);
            if (existing.isEmpty()) {
                return "{\"error\":\"Bendrija nerasta.\"}";
            }

            communityService.deleteById(id);
            return "{\"success\":\"Bendrija pašalinta.\"}";
        } catch (Exception e) {
            return "{\"error\":\"Klaida šalinant bendriją: " + e.getMessage() + "\"}";
        }
    }


}
