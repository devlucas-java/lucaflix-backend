package com.lucaflix.service;

import com.lucaflix.dto.admin.CreateTvDTO;
import com.lucaflix.dto.admin.UpdateTvDTO;
import com.lucaflix.dto.media.TvCompleteDTO;
import com.lucaflix.model.Tv;
import com.lucaflix.repository.TvRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class TvService {

    @Autowired
    private TvRepository tvRepository;

    @Transactional(readOnly = true)
    public List<TvCompleteDTO> findAll() {
        List<Tv> tvList = tvRepository.findAll();
        return tvList.stream()
                .map(this::convertToCompleteDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<TvCompleteDTO> findAll(Pageable pageable) {
        Page<Tv> tvPage = tvRepository.findAll(pageable);
        return tvPage.map(this::convertToCompleteDTO);
    }

    @Transactional(readOnly = true)
    public TvCompleteDTO findById(Long id) {
        Optional<Tv> tvOptional = tvRepository.findById(id);
        if (tvOptional.isPresent()) {
            return convertToCompleteDTO(tvOptional.get());
        } else {
            throw new RuntimeException("TV não encontrada com ID: " + id);
        }
    }

    public TvCompleteDTO create(CreateTvDTO createTvDTO) {
        Tv tv = new Tv();
        BeanUtils.copyProperties(createTvDTO, tv);
        tv.setDataCadastro(new Date());
        tv.setLikes(0L);

        Tv savedTv = tvRepository.save(tv);
        return convertToCompleteDTO(savedTv);
    }

    public TvCompleteDTO update(Long id, UpdateTvDTO updateTvDTO) {
        Optional<Tv> tvOptional = tvRepository.findById(id);
        if (tvOptional.isPresent()) {
            Tv tv = tvOptional.get();

            // Atualizar apenas os campos não nulos
            if (updateTvDTO.getTitle() != null) {
                tv.setTitle(updateTvDTO.getTitle());
            }
            if (updateTvDTO.getPaisOrigen() != null) {
                tv.setPaisOrigen(updateTvDTO.getPaisOrigen());
            }
            if (updateTvDTO.getCategoria() != null) {
                tv.setCategoria(updateTvDTO.getCategoria());
            }
            if (updateTvDTO.getMinAge() != null) {
                tv.setMinAge(updateTvDTO.getMinAge());
            }
            if (updateTvDTO.getEmbed1() != null) {
                tv.setEmbed1(updateTvDTO.getEmbed1());
            }
            if (updateTvDTO.getEmbed2() != null) {
                tv.setEmbed2(updateTvDTO.getEmbed2());
            }
            if (updateTvDTO.getImageURL1() != null) {
                tv.setImageURL1(updateTvDTO.getImageURL1());
            }
            if (updateTvDTO.getImageURL2() != null) {
                tv.setImageURL2(updateTvDTO.getImageURL2());
            }

            Tv updatedTv = tvRepository.save(tv);
            return convertToCompleteDTO(updatedTv);
        } else {
            throw new RuntimeException("TV não encontrada com ID: " + id);
        }
    }

    public void delete(Long id) {
        if (tvRepository.existsById(id)) {
            tvRepository.deleteById(id);
        } else {
            throw new RuntimeException("TV não encontrada com ID: " + id);
        }
    }

    public TvCompleteDTO incrementLikes(Long id) {
        Optional<Tv> tvOptional = tvRepository.findById(id);
        if (tvOptional.isPresent()) {
            Tv tv = tvOptional.get();
            tv.setLikes(tv.getLikes() + 1);
            Tv updatedTv = tvRepository.save(tv);
            return convertToCompleteDTO(updatedTv);
        } else {
            throw new RuntimeException("TV não encontrada com ID: " + id);
        }
    }

    private TvCompleteDTO convertToCompleteDTO(Tv tv) {
        TvCompleteDTO dto = new TvCompleteDTO();
        BeanUtils.copyProperties(tv, dto);
        return dto;
    }
}