package eu.minted.komuna.controller;

import eu.minted.komuna.model.Community;
import eu.minted.komuna.service.CommunityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public Community getById(@PathVariable Long id) {
        return communityService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Community> create(@RequestBody Community community) {
        communityService.save(community);
        return ResponseEntity.ok(community);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Community> update(@PathVariable Long id, @RequestBody Community updatedCommunity) {
        Community existing = communityService.findById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        existing.setName(updatedCommunity.getName());
        existing.setCode(updatedCommunity.getCode());
        existing.setAddress(updatedCommunity.getAddress());

        communityService.save(existing);
        return ResponseEntity.ok(existing);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        System.out.println("Gaunamas trynimo ID: " + id);
        communityService.delete(id);
        return ResponseEntity.noContent().build();
    }


}
