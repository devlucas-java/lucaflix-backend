package com.lucaflix.controller;

import com.lucaflix.dto.admin.CreateTvDTO;
import com.lucaflix.dto.admin.UpdateTvDTO;
import com.lucaflix.dto.media.TvCompleteDTO;
import com.lucaflix.service.TvService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tv")
@CrossOrigin(origins = "*")
public class TvController {

    @Autowired
    private TvService tvService;

    @GetMapping
    public ResponseEntity<List<TvCompleteDTO>> getAllTvs() {
        try {
            List<TvCompleteDTO> tvList = tvService.findAll();
            return ResponseEntity.ok(tvList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<TvCompleteDTO>> getAllTvsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "dataCadastro") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            Page<TvCompleteDTO> tvPage = tvService.findAll(pageable);
            return ResponseEntity.ok(tvPage);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<TvCompleteDTO> getTvById(@PathVariable Long id) {
        try {
            TvCompleteDTO tv = tvService.findById(id);
            return ResponseEntity.ok(tv);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<TvCompleteDTO> createTv(@Valid @RequestBody CreateTvDTO createTvDTO) {
        try {
            TvCompleteDTO createdTv = tvService.create(createTvDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTv);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<TvCompleteDTO> updateTv(
            @PathVariable Long id,
            @Valid @RequestBody UpdateTvDTO updateTvDTO) {
        try {
            TvCompleteDTO updatedTv = tvService.update(id, updateTvDTO);
            return ResponseEntity.ok(updatedTv);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTv(@PathVariable Long id) {
        try {
            tvService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<TvCompleteDTO> likeTv(@PathVariable Long id) {
        try {
            TvCompleteDTO likedTv = tvService.incrementLikes(id);
            return ResponseEntity.ok(likedTv);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/popular")
    public ResponseEntity<List<TvCompleteDTO>> getPopularTvs(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "likes"));
            Page<TvCompleteDTO> popularTvs = tvService.findAll(pageable);
            return ResponseEntity.ok(popularTvs.getContent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}