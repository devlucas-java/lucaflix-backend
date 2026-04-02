package com.lucaflix.service;

import com.lucaflix.dto.mapper.PageMapper;
import com.lucaflix.dto.mapper.UserMapper;
import com.lucaflix.dto.request.others.FilterUserDTO;
import com.lucaflix.dto.request.user.UpdateUserDTO;
import com.lucaflix.dto.response.others.PaginatedResponseDTO;
import com.lucaflix.dto.response.user.UserDTO;
import com.lucaflix.model.User;
import com.lucaflix.model.enums.Plan;
import com.lucaflix.model.enums.Role;
import com.lucaflix.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PageMapper pageMapper;

    private User user;
    private UserDTO userDTO;
    private UUID userId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setFirstName("Lucas");
        user.setLastName("Silva");
        user.setUsername("lucassilva");
        user.setEmail("lucas@email.com");
        user.setRole(Role.USER);
        user.setIsAccountLocked(false);
        user.setPlan(Plan.FREE);

        userDTO = new UserDTO();
        userDTO.setId(userId.toString());
        userDTO.setFirstName("Lucas");
        userDTO.setLastName("Silva");
        userDTO.setUsername("lucassilva");
        userDTO.setEmail("lucas@email.com");
    }

    // -------------------------------------------------------------------------
    // getMe
    // -------------------------------------------------------------------------

    @Test
    void getMe_ShouldReturnUserDTO_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getMe(user);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId.toString());
        verify(userRepository).findById(userId);
        verify(userMapper).toUserDTO(user);
    }

    @Test
    void getMe_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getMe(user))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void getUser_ShouldReturnUserDTO_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.getUser(userId);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(userId.toString());
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.getUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    // -------------------------------------------------------------------------
    // filterUser
    // -------------------------------------------------------------------------

    @Test
    void filterUser_ShouldReturnPaginatedResponse() {
        FilterUserDTO filter = new FilterUserDTO();
        Page<User> userPage = new PageImpl<>(List.of(user));
        PaginatedResponseDTO<UserDTO> paginatedResponse = new PaginatedResponseDTO<>();

        when(userRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(userPage);
        when(pageMapper.toPaginatedDTO(eq(userPage), any(Function.class))).thenReturn(paginatedResponse);

        PaginatedResponseDTO<UserDTO> result = userService.filterUser(filter, 0, 10);

        assertThat(result).isNotNull();
        verify(userRepository).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void updateMe_ShouldUpdateAllFields_WhenAllFieldsAreProvided() {
        UpdateUserDTO request = new UpdateUserDTO();
        request.setFirstName("Novo");
        request.setLastName("Nome");
        request.setUsername("novousername");
        request.setEmail("novo@email.com");

        when(userRepository.existsByUsername("novousername")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        UserDTO result = userService.updateMe(user, request);

        assertThat(result).isNotNull();
        verify(userRepository).save(user);
        assertThat(user.getFirstName()).isEqualTo("Novo");
        assertThat(user.getLastName()).isEqualTo("Nome");
        assertThat(user.getUsername()).isEqualTo("novousername");
        assertThat(user.getEmail()).isEqualTo("novo@email.com");
    }

    @Test
    void updateMe_ShouldThrowException_WhenUsernameAlreadyInUse() {
        UpdateUserDTO request = new UpdateUserDTO();
        request.setUsername("outrousername");

        when(userRepository.existsByUsername("outrousername")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateMe(user, request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Username is already in use");
    }

    @Test
    void updateMe_ShouldNotUpdateFields_WhenFieldsAreNullOrBlank() {
        UpdateUserDTO request = new UpdateUserDTO();
        request.setFirstName(null);
        request.setLastName("  ");

        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.updateMe(user, request);

        assertThat(user.getFirstName()).isEqualTo("Lucas");
        assertThat(user.getLastName()).isEqualTo("Silva");
    }

    // -------------------------------------------------------------------------
    // deleteUser
    // -------------------------------------------------------------------------

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository).delete(user);
    }

    @Test
    void deleteUser_ShouldThrowEntityNotFoundException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.deleteUser(userId))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("User not found by id");
    }

    // -------------------------------------------------------------------------
    // demoteUser
    // -------------------------------------------------------------------------

    @Test
    void demoteUser_ShouldDemoteFromSuperadminToAdmin() {
        user.setRole(Role.SUPERADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.demoteUser(userId);

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
        verify(userRepository).save(user);
    }

    @Test
    void demoteUser_ShouldDemoteFromAdminToUser() {
        user.setRole(Role.ADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.demoteUser(userId);

        assertThat(user.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void demoteUser_ShouldThrowException_WhenUserAlreadyHasLowestRole() {
        user.setRole(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.demoteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("USER");
    }

    // -------------------------------------------------------------------------
    // promoteUser
    // -------------------------------------------------------------------------

    @Test
    void promoteUser_ShouldPromoteFromUserToAdmin() {
        user.setRole(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.promoteUser(userId);

        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void promoteUser_ShouldPromoteFromAdminToSuperadmin() {
        user.setRole(Role.ADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.promoteUser(userId);

        assertThat(user.getRole()).isEqualTo(Role.SUPERADMIN);
    }

    @Test
    void promoteUser_ShouldThrowException_WhenUserAlreadyIsSuperadmin() {
        user.setRole(Role.SUPERADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.promoteUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SUPERADMIN");
    }

    // -------------------------------------------------------------------------
    // lockUser
    // -------------------------------------------------------------------------

    @Test
    void lockUser_ShouldLockUser_WhenUserIsNotSuperadmin() {
        user.setRole(Role.USER);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.LockeUser(userId);

        assertThat(user.getIsAccountLocked()).isTrue();
    }

    @Test
    void lockUser_ShouldThrowException_WhenUserIsSuperadmin() {
        user.setRole(Role.SUPERADMIN);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThatThrownBy(() -> userService.LockeUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("SUPERADMIN");
    }

    // -------------------------------------------------------------------------
    // unlockUser
    // -------------------------------------------------------------------------

    @Test
    void unlockUser_ShouldUnlockUser() {
        user.setIsAccountLocked(true);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.unLockUser(userId);

        assertThat(user.getIsAccountLocked()).isFalse();
    }

    @Test
    void unlockUser_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.unLockUser(userId))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found by id");
    }

    // -------------------------------------------------------------------------
    // updatePlan
    // -------------------------------------------------------------------------

    @Test
    void updatePlan_ShouldUpdateUserPlan() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toUserDTO(user)).thenReturn(userDTO);

        userService.updatePlan(userId, Plan.PREMIUM);

        assertThat(user.getPlan()).isEqualTo(Plan.PREMIUM);
        verify(userRepository).save(user);
    }

    @Test
    void updatePlan_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updatePlan(userId, Plan.PREMIUM))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found by id");
    }
}