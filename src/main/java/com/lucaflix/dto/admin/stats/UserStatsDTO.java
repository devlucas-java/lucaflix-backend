package com.lucaflix.dto.admin.stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserStatsDTO {
    private Long totalUsers;
    private Long activeUsers; // Usuários que já curtiram algo
    private Long usersWithLists; // Usuários que têm itens na lista
    private Double averageLikesPerUser;
    private Double averageListItemsPerUser;
    private String mostActiveUser; // Usuário com mais likes
    private String userWithLargestList; // Usuário com mais itens na lista
}
