package com.lucaflix.service.utils.spec;

import com.lucaflix.dto.request.others.FilterDTO;
import com.lucaflix.model.Movie;
import com.lucaflix.model.enums.Categories;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class MovieSpecification implements Specification<Movie> {

    private final FilterDTO filter;

    @Override
    public Predicate toPredicate(Root<Movie> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicate = new ArrayList<>();

        if (StringUtils.hasText(filter.getTitle())) {

            Expression<String> titleToLowerCase = criteriaBuilder.lower(root.get("title"));

            Predicate p = criteriaBuilder.like(titleToLowerCase, "%" + filter.getTitle().toLowerCase() + "%");
            predicate.add(p);
        }

        if (StringUtils.hasText(filter.getCountryOrigin())) {

            Expression<String> countryOriginToLowerCase = criteriaBuilder.lower(root.get("countryOrigin"));

            Predicate p = criteriaBuilder.like(countryOriginToLowerCase, "%" + filter.getCountryOrigin().toLowerCase() + "%");
            predicate.add(p);
        }

        if (filter.getYearStart() != null
                && filter.getYearEnd() != null
                && filter.getYearStart() <= filter.getYearEnd()
                && filter.getYearEnd() <= LocalDate.now().getYear()
        ) {
            Predicate p = criteriaBuilder.between(root.get("yearRelease"), filter.getYearStart(), filter.getYearEnd());
            predicate.add(p);
        }

        if (filter.getMinRating() != null
                && filter.getMaxRating() != null
                && filter.getMinRating() <= filter.getMaxRating()
                && filter.getMinRating() >= 0.0
                && filter.getMaxRating() <= 10.0
        ) {
            Predicate p = criteriaBuilder.between(root.get("rating"), filter.getMinRating(), filter.getMaxRating());
            predicate.add(p);
        }

        if (!filter.getCategories().isEmpty()){
            Join<Movie, Categories> categoriesJoin = root.join("categories");
            Predicate p = categoriesJoin.in(filter.getCategories());
            predicate.add(p);
        }


        return criteriaBuilder.and(predicate.toArray(new Predicate[predicate.size()]));
    }
}