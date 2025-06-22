package com.lucaflix.service;

import com.lucaflix.dto.auth.AuthDTO;
import com.lucaflix.dto.user.UserDTO;
import com.lucaflix.dto.user.UserMapper;
import com.lucaflix.model.*;
import com.lucaflix.model.enums.Role;
import com.lucaflix.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Optional;
import java.util.UUID;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final AdminPanelRepository adminPanelRepository;
    private final MinhaListaRepository minhaListaRepository;
    private final LikeRepository likeRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /// OBTEM USUARIO POR ID OU LANCA EXCECAO
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + id));
    }

    /// OBTEM USUARIO POR USERNAME OU EMAIL OU LANCA EXCECAO
    public User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com username/email: " + usernameOrEmail));
    }

    /// VERIFICA SE USERNAME JA EXISTE
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /// VERIFICA SE EMAIL JA EXISTE
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /// REMOVE ACENTOS E CARACTERES ESPECIAIS DE UMA STRING
    private String removeAccents(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "user";
        }
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /// GERA USERNAME UNICO BASEADO NO NOME COMPLETO
    private String generateUniqueUsername(String firstName, String lastName) {
        String nomeCompleto = (firstName + " " + (lastName != null ? lastName : "")).trim();

        /// REMOVE ACENTOS E CONVERTE PARA MINUSCULO
        String cleanName = removeAccents(nomeCompleto.toLowerCase());

        /// SEPARA O NOME EM PARTES
        String[] nameParts = cleanName.trim().split("\\s+");

        String baseUsername = "";

        if (nameParts.length == 1) {
            /// APENAS UM NOME - USA ELE COMPLETO
            baseUsername = nameParts[0].replaceAll("[^a-z0-9]", "");
        } else if (nameParts.length >= 2) {
            /// PRIMEIRO NOME + PRIMEIRA LETRA DO ULTIMO NOME
            String firstNameClean = nameParts[0].replaceAll("[^a-z0-9]", "");
            String lastNameClean = nameParts[nameParts.length - 1].replaceAll("[^a-z0-9]", "");

            if (firstNameClean.length() > 0 && lastNameClean.length() > 0) {
                baseUsername = firstNameClean + lastNameClean.charAt(0);
            } else if (firstNameClean.length() > 0) {
                baseUsername = firstNameClean;
            } else {
                baseUsername = "user";
            }
        }

        /// GARANTE QUE O USERNAME TEM PELO MENOS 3 CARACTERES
        if (baseUsername.length() < 3) {
            baseUsername = baseUsername + "123";
        }

        /// LIMITA O TAMANHO DO USERNAME BASE PARA 15 CARACTERES
        if (baseUsername.length() > 15) {
            baseUsername = baseUsername.substring(0, 15);
        }

        String username = baseUsername;
        Random random = new Random();
        int attempts = 0;
        final int MAX_ATTEMPTS = 100;

        /// ADICIONA NUMEROS ALEATORIOS ATE ENCONTRAR UM USERNAME UNICO
        while (existsByUsername(username) && attempts < MAX_ATTEMPTS) {
            int randomNum = random.nextInt(9999) + 1;
            username = baseUsername + randomNum;

            /// EVITA USERNAME MUITO LONGO
            if (username.length() > 20) {
                username = baseUsername.substring(0, Math.min(baseUsername.length(), 10)) + randomNum;
            }
            attempts++;
        }

        // Fallback caso não encontre username único
        if (attempts >= MAX_ATTEMPTS) {
            username = "user" + System.currentTimeMillis();
        }

        return username;
    }

    /// CRIA NOVO USUARIO
    @Transactional
    public User createUser(AuthDTO.SignUpRequest signUpRequest) {
        /// VALIDA SE EMAIL JA EXISTE
        if (existsByEmail(signUpRequest.getEmail())) {
            throw new IllegalArgumentException("Email já existe: " + signUpRequest.getEmail());
        }

        /// GERA USERNAME UNICO BASEADO NO NOME
        String username = generateUniqueUsername(signUpRequest.getFirstName(), signUpRequest.getLastName());

        User user = new User();
        user.setUsername(username);
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
        user.setRole(Role.USER);
        user.setIsAccountEnabled(true);
        user.setIsAccountLocked(false);
        user.setIsCredentialsExpired(false);
        user.setIsAccountExpired(false);

        User savedUser = userRepository.save(user);
        log.info("Usuário criado com sucesso - ID: {}, Username: {}, Email: {}",
                savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());

        return savedUser;
    }

    /// ATUALIZA DADOS DO USUARIO
    @Transactional
    public User updateUser(User user, UserDTO.UpdateUserRequest request) {
        // Atualiza campos se fornecidos
        if (request.getFirstName() != null && !request.getFirstName().trim().isEmpty()) {
            user.setFirstName(request.getFirstName().trim());
        }

        if (request.getLastName() != null && !request.getLastName().trim().isEmpty()) {
            user.setLastName(request.getLastName().trim());
        }

        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            String newUsername = request.getUsername().trim();
            if (!user.getUsername().equals(newUsername) && existsByUsername(newUsername)) {
                throw new IllegalArgumentException("Username já existe: " + newUsername);
            }
            user.setUsername(newUsername);
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (!user.getEmail().equalsIgnoreCase(newEmail) && existsByEmail(newEmail)) {
                throw new IllegalArgumentException("Email já existe: " + newEmail);
            }
            user.setEmail(newEmail);
        }

        User savedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso - ID: {}, Username: {}", savedUser.getId(), savedUser.getUsername());
        return savedUser;
    }

    /// ALTERA SENHA DO USUARIO
    @Transactional
    public void changePassword(User user, String currentPassword, String newPassword) {
        /// VERIFICA SENHA ATUAL
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Senha atual está incorreta");
        }

        /// VALIDA NOVA SENHA
        if (newPassword == null || newPassword.length() < 6) {
            throw new IllegalArgumentException("Nova senha deve ter pelo menos 6 caracteres");
        }

        /// ATUALIZA SENHA
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Senha alterada com sucesso para usuário: {}", user.getId());
    }

    /// ATUALIZA EMAIL DO USUARIO
    @Transactional
    public void updateEmail(User user, String newEmail, String currentPassword) {
        /// VERIFICA SENHA ATUAL
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Senha atual está incorreta");
        }

        /// NORMALIZA EMAIL
        String normalizedEmail = newEmail.trim().toLowerCase();

        /// VERIFICA SE NOVO EMAIL JA EXISTE
        if (existsByEmail(normalizedEmail) && !user.getEmail().equalsIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException("Email já existe: " + normalizedEmail);
        }

        /// ATUALIZA EMAIL
        user.setEmail(normalizedEmail);
        userRepository.save(user);
        log.info("Email atualizado com sucesso para usuário: {}", user.getId());
    }

    /// DEFINE SE CONTA ESTA BLOQUEADA
    @Transactional
    public void setAccountLocked(UUID userId, boolean locked) {
        User user = getUserById(userId);
        user.setIsAccountLocked(locked);
        userRepository.save(user);
        log.info("Conta do usuário {} foi {}", userId, locked ? "bloqueada" : "desbloqueada");
    }

    /// DEFINE SE CONTA ESTA HABILITADA
    @Transactional
    public void setAccountEnabled(UUID userId, boolean enabled) {
        User user = getUserById(userId);
        user.setIsAccountEnabled(enabled);
        userRepository.save(user);
        log.info("Conta do usuário {} foi {}", userId, enabled ? "habilitada" : "desabilitada");
    }

    /// CRIA PAINEL ADMINISTRATIVO
    @Transactional
    public AdminPanel createAdminPanel(User user, Integer adminLevel) {
        /// VERIFICA SE PAINEL ADMIN JA EXISTE
        Optional<AdminPanel> existingPanel = adminPanelRepository.findByUser(user);
        if (existingPanel.isPresent()) {
            log.warn("Painel admin já existe para usuário: {}", user.getId());
            return existingPanel.get();
        }

        AdminPanel adminPanel = new AdminPanel();
        adminPanel.setUser(user);
        adminPanel.setAdminLevel(adminLevel != null ? adminLevel : 1);

        AdminPanel savedAdminPanel = adminPanelRepository.save(adminPanel);
        log.info("Painel admin criado para usuário: {} com nível: {}", user.getId(), adminLevel);

        return savedAdminPanel;
    }

    /// ATUALIZA NIVEL ADMINISTRATIVO
    @Transactional
    public void updateAdminLevel(UUID userId, Integer adminLevel) {
        User user = getUserById(userId);
        AdminPanel adminPanel = adminPanelRepository.findByUser(user)
                .orElseThrow(() -> new EntityNotFoundException("Painel admin não encontrado para usuário: " + userId));

        adminPanel.setAdminLevel(adminLevel);
        adminPanelRepository.save(adminPanel);
        log.info("Nível admin atualizado para {} para usuário: {}", adminLevel, userId);
    }

    /// PROMOVE USUARIO A ADMINISTRADOR
    @Transactional
    public void promoteToAdmin(UUID userId, Integer adminLevel) {
        User user = getUserById(userId);
        user.setRole(Role.ADMIN);
        userRepository.save(user);

        /// CRIA PAINEL ADMIN SE NAO EXISTIR
        Optional<AdminPanel> existingPanel = adminPanelRepository.findByUser(user);
        if (existingPanel.isEmpty()) {
            createAdminPanel(user, adminLevel);
        } else {
            updateAdminLevel(userId, adminLevel);
        }

        log.info("Usuário promovido a admin: {}", userId);
    }

    /// PROMOVE USUARIO A SUPER ADMINISTRADOR
    @Transactional
    public void promoteToSuperAdmin(UUID userId) {
        User user = getUserById(userId);
        user.setRole(Role.SUPER_ADMIN);
        userRepository.save(user);

        /// CRIA OU ATUALIZA PAINEL ADMIN COM NIVEL MAXIMO
        Optional<AdminPanel> existingPanel = adminPanelRepository.findByUser(user);
        if (existingPanel.isEmpty()) {
            createAdminPanel(user, 99);
        } else {
            updateAdminLevel(userId, 99);
        }

        log.info("Usuário promovido a super admin: {}", userId);
    }

    /// REBAIXA USUARIO PARA USUARIO COMUM
    @Transactional
    public void demoteToUser(UUID userId) {
        User user = getUserById(userId);
        user.setRole(Role.USER);
        userRepository.save(user);

        /// REMOVE PAINEL ADMIN
        adminPanelRepository.findByUser(user).ifPresent(adminPanel -> {
            adminPanelRepository.delete(adminPanel);
            log.info("Painel admin removido para usuário: {}", userId);
        });

        log.info("Usuário rebaixado para usuário comum: {}", userId);
    }

    /// OBTEM PAINEL ADMINISTRATIVO DO USUARIO
    public Optional<AdminPanel> getAdminPanelByUser(User user) {
        return adminPanelRepository.findByUser(user);
    }

    /// CONVERTE USER PARA AUTH USER RESPONSE DTO
    public AuthDTO.UserResponse convertToAuthUserResponse(User user) {
        Optional<AdminPanel> adminPanel = getAdminPanelByUser(user);
        return userMapper.toAuthUserResponse(user, adminPanel);
    }

    /// CONVERTE USER PARA USER RESPONSE DTO
    public UserDTO.UserResponse convertToUserResponse(User user) {
        return userMapper.toUserResponse(user);
    }

    /// VERIFICA SENHA DO USUARIO
    public boolean verifyPassword(User user, String password) {
        return passwordEncoder.matches(password, user.getPassword());
    }

    /// DELETA USUARIO E TODOS OS DADOS RELACIONADOS DE FORMA SEGURA
    @Transactional
    public void deleteUserAndRelatedData(UUID userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com ID: " + userId));

            log.info("Iniciando exclusão do usuário {} e dados relacionados", user.getUsername());

            // 1. Deletar todos os likes do usuário
            // Isso remove os likes mas mantém as mídias
            likeRepository.deleteByUserId(userId);
            log.info("Likes do usuário {} deletados", user.getUsername());

            // 2. Deletar todos os itens da MinhaLista do usuário
            // Isso remove os itens da lista mas mantém as mídias
            minhaListaRepository.deleteByUserId(userId);
            log.info("Itens da lista do usuário {} deletados", user.getUsername());

            // 3. Deletar o painel admin se existir
            Optional<AdminPanel> adminPanel = adminPanelRepository.findByUser(user);
            if (adminPanel.isPresent()) {
                adminPanelRepository.delete(adminPanel.get());
                log.info("Painel admin do usuário {} deletado", user.getUsername());
            }

            // 4. Finalmente deletar o usuário
            // Agora não há mais referências que possam causar problemas
            userRepository.delete(user);
            log.info("Usuário {} deletado com sucesso", user.getUsername());

        } catch (EntityNotFoundException e) {
            log.error("Usuário não encontrado: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Erro ao deletar usuário e dados relacionados: {}", e.getMessage(), e);
            throw new RuntimeException("Falha ao deletar usuário e dados relacionados", e);
        }
    }
}