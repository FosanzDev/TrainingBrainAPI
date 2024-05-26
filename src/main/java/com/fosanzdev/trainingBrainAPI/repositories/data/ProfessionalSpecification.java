package com.fosanzdev.trainingBrainAPI.repositories.data;

import com.fosanzdev.trainingBrainAPI.models.data.Professional;
import com.fosanzdev.trainingBrainAPI.models.data.ProfessionalSkill;
import com.fosanzdev.trainingBrainAPI.models.data.Skill;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ProfessionalSpecification implements Specification<Professional> {

    private final String filter;
    private final Long filterId;
    private final String name;
    private final List<Long> hasSkills;
    private final Integer rating;

    public ProfessionalSpecification(String filter, Long filterId, String name, List<Long> hasSkills, Integer rating) {
        this.filter = filter;
        this.filterId = filterId;
        this.name = name;
        this.hasSkills = hasSkills;
        this.rating = rating;
    }

    @Override
    public Predicate toPredicate(Root<Professional> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.equals("branch")) {
            predicates.add(criteriaBuilder.equal(root.get("workTitle").get("branch").get("id"), filterId));
        } else if (filter.equals("worktitle")) {
            predicates.add(criteriaBuilder.equal(root.get("workTitle").get("id"), filterId));
        }

        if (name != null) {
            predicates.add(criteriaBuilder.like(root.get("account").get("name"), name));
        }

        if (hasSkills != null && !hasSkills.isEmpty()) {
            Join<Professional, ProfessionalSkill> professionalSkillJoin = root.join("professionalSkills");
            Join<ProfessionalSkill, Skill> skillJoin = professionalSkillJoin.join("skill");
            predicates.add(skillJoin.get("id").in(hasSkills));
        }

        if (rating != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), rating));
        }

        query.distinct(true);

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }
}
