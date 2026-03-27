package com.lucaflix.service.utils.spec;


import com.lucaflix.dto.request.others.FilterUserDTO;
import com.lucaflix.model.User;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class UserSpecification implements Specification<User> {

    private final FilterUserDTO filter;

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

        List<Predicate> predicate = new ArrayList<>();

        if (StringUtils.hasText(filter.getFirstName())){
            Expression<String> firstNameToLowerCase = criteriaBuilder.lower(root.get("firstName"));
            Predicate p = criteriaBuilder.like(firstNameToLowerCase, "%" + filter.getFirstName().toLowerCase() + "%");
            predicate.add(p);
        }
        if (StringUtils.hasText(filter.getLastName())){
            Expression<String> lastNameToLowerCase = criteriaBuilder.lower(root.get("lastName"));
            Predicate p = criteriaBuilder.like(lastNameToLowerCase, "%" + filter.getLastName().toLowerCase() + "%");
            predicate.add(p);
        }
        if (StringUtils.hasText(filter.getUsername())){
            Expression<String> usernameToLowerCase = criteriaBuilder.lower(root.get("username"));
            Predicate p = criteriaBuilder.like(usernameToLowerCase, "%" + filter.getUsername().toLowerCase() + "%");
            predicate.add(p);
        }
        if (StringUtils.hasText(filter.getEmail())){
            Expression<String> emailToLowerCase = criteriaBuilder.lower(root.get("email"));
            Predicate p = criteriaBuilder.like(emailToLowerCase, "%" + filter.getEmail().toLowerCase() + "%");
            predicate.add(p);
        }
        if (filter.getStartDate() != null
                && filter.getEndDate() != null
                && filter.getStartDate().isBefore(filter.getEndDate())
                && filter.getEndDate().isBefore(LocalDate.now())
        ) {
            Predicate p = criteriaBuilder.between( root.get("dateRegistered"), filter.getStartDate(), filter.getEndDate());
            predicate.add(p);
        }
        if (filter.getRole() != null) {
            Predicate p = criteriaBuilder.equal(root.get("role"), filter.getRole());
            predicate.add(p);
        }
        if (filter.getPlan() != null) {
            Predicate p = criteriaBuilder.equal(root.get("plan"), filter.getPlan());
            predicate.add(p);
        }
        if (filter.getIsAccountEnabled() != null){
            Predicate p = criteriaBuilder.equal(root.get("isAccountEnabled"), filter.getIsAccountEnabled());
            predicate.add(p);
        }
        if (filter.getIsAccountLocked() != null){
            Predicate p = criteriaBuilder.equal(root.get("isAccountLocked"), filter.getIsAccountLocked());
            predicate.add(p);
        }

        return criteriaBuilder.and(predicate.toArray(new Predicate[predicate.size()]));
    }
}
