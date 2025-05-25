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
import java.util.Date;
import java.util.List;
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
    private final FilmeRepository filmeRepository;
    private final SerieRepository serieRepository;
    private final EpisodioRepository episodioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    /// BUSCA USUARIO POR USERNAME OU EMAIL
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmail(usernameOrEmail);
    }

    /// BUSCA USUARIO POR ID
    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

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
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    /// GERA USERNAME UNICO BASEADO NO NOME COMPLETO
    private String generateUniqueUsername(String nomeCompleto) {
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
            String firstName = nameParts[0].replaceAll("[^a-z0-9]", "");
            String lastName = nameParts[nameParts.length - 1].replaceAll("[^a-z0-9]", "");

            if (firstName.length() > 0 && lastName.length() > 0) {
                baseUsername = firstName + lastName.charAt(0);
            } else if (firstName.length() > 0) {
                baseUsername = firstName;
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

        /// ADICIONA NUMEROS ALEATORIOS ATE ENCONTRAR UM USERNAME UNICO
        while (existsByUsername(username)) {
            int randomNum = random.nextInt(9999) + 1;
            username = baseUsername + randomNum;

            /// EVITA LOOP INFINITO
            if (username.length() > 20) {
                username = baseUsername.substring(0, Math.min(baseUsername.length(), 10)) + randomNum;
            }
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
        String username = generateUniqueUsername(signUpRequest.getNomeCompleto());

        User user = new User();
        user.setUsername(username);
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
        if (request.getUsername() != null && !request.getUsername().trim().isEmpty()) {
            if (!user.getUsername().equals(request.getUsername()) && existsByUsername(request.getUsername())) {
                throw new IllegalArgumentException("Username já existe: " + request.getUsername());
            }
            user.setUsername(request.getUsername());
        }

        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            if (!user.getEmail().equals(request.getEmail()) && existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email já existe: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        User savedUser = userRepository.save(user);
        log.info("Usuário atualizado com sucesso com id: {}", savedUser.getId());
        return savedUser;
    }

    /// ALTERA SENHA DO USUARIO
    @Transactional
    public void changePassword(User user, String currentPassword, String newPassword) {
        /// VERIFICA SENHA ATUAL
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Senha atual está incorreta");
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

        /// VERIFICA SE NOVO EMAIL JA EXISTE
        if (existsByEmail(newEmail) && !user.getEmail().equals(newEmail)) {
            throw new IllegalArgumentException("Email já existe: " + newEmail);
        }

        /// ATUALIZA EMAIL
        user.setEmail(newEmail);
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
        if (adminPanelRepository.existsByUser(user)) {
            throw new IllegalArgumentException("Painel admin já existe para usuário: " + user.getId());
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
        if (!adminPanelRepository.existsByUser(user)) {
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
        if (!adminPanelRepository.existsByUser(user)) {
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
        adminPanelRepository.findByUser(user).ifPresent(adminPanelRepository::delete);

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

    /// DELETA USUARIO
    @Transactional
    public void deleteUser(UUID userId) {
        User user = getUserById(userId);

        /// DELETA PAINEL ADMIN SE EXISTIR
        adminPanelRepository.findByUser(user).ifPresent(adminPanelRepository::delete);

        /// DELETA USUARIO
        userRepository.delete(user);
        log.info("Usuário deletado: {}", userId);
    }

    /// ADICIONA FILME COMO ASSISTIDO
    @Transactional
    public void addMovieAsWatched(User user, Long filmeId) {
        Filme filme = filmeRepository.findById(filmeId)
                .orElseThrow(() -> new EntityNotFoundException("Filme não encontrado com id: " + filmeId));

        /// VERIFICA SE JA EXISTE NA LISTA
        Optional<MinhaLista> existingItem = minhaListaRepository.findByUserAndFilme(user, filme);

        if (existingItem.isPresent()) {
            /// ATUALIZA COMO ASSISTIDO
            MinhaLista item = existingItem.get();
            item.setAssistido(true);
            item.setDataUltimaVisualizacao(new Date());
            minhaListaRepository.save(item);
        } else {
            /// CRIA NOVO ITEM NA LISTA
            MinhaLista novaLista = new MinhaLista();
            novaLista.setUser(user);
            novaLista.setFilme(filme);
            novaLista.setAssistido(true);
            novaLista.setDataUltimaVisualizacao(new Date());
            minhaListaRepository.save(novaLista);
        }

        log.info("Filme {} marcado como assistido para usuário: {}", filmeId, user.getId());
    }

    /// ADICIONA SERIE COMO ASSISTINDO
    @Transactional
    public void addSeriesAsWatching(User user, Long serieId) {
        Serie serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new EntityNotFoundException("Série não encontrada com id: " + serieId));

        /// VERIFICA SE JA EXISTE NA LISTA
        Optional<MinhaLista> existingItem = minhaListaRepository.findByUserAndSerie(user, serie);

        if (!existingItem.isPresent()) {
            /// CRIA NOVO ITEM NA LISTA
            MinhaLista novaLista = new MinhaLista();
            novaLista.setUser(user);
            novaLista.setSerie(serie);
            novaLista.setAssistido(false); /// SERIES SEMPRE FALSE (CONTROLE POR EPISODIO)
            novaLista.setEpisodiosAssistidos(0);
            novaLista.setDataUltimaVisualizacao(new Date());
            minhaListaRepository.save(novaLista);

            log.info("Série {} adicionada como assistindo para usuário: {}", serieId, user.getId());
        }
    }

    /// MARCA EPISODIO COMO ASSISTIDO
    @Transactional
    public void markEpisodeAsWatched(User user, Long episodioId) {
        Episodio episodio = episodioRepository.findById(episodioId)
                .orElseThrow(() -> new EntityNotFoundException("Episódio não encontrado com id: " + episodioId));

        Serie serie = episodio.getTemporada().getSerie();

        /// BUSCA OU CRIA ITEM NA LISTA
        Optional<MinhaLista> itemLista = minhaListaRepository.findByUserAndSerie(user, serie);

        if (!itemLista.isPresent()) {
            /// CRIA NOVO ITEM NA LISTA
            MinhaLista novaLista = new MinhaLista();
            novaLista.setUser(user);
            novaLista.setSerie(serie);
            novaLista.setAssistido(false);
            novaLista.setEpisodiosAssistidos(1);
            novaLista.setDataUltimaVisualizacao(new Date());
            minhaListaRepository.save(novaLista);
        } else {
            /// INCREMENTA CONTADOR DE EPISODIOS ASSISTIDOS
            MinhaLista item = itemLista.get();
            item.setEpisodiosAssistidos(item.getEpisodiosAssistidos() + 1);
            item.setDataUltimaVisualizacao(new Date());
            minhaListaRepository.save(item);
        }

        log.info("Episódio {} marcado como assistido para usuário: {}", episodioId, user.getId());
    }

    /// OBTEM LISTA DO USUARIO
    public List<MinhaLista> getUserList(User user) {
        return minhaListaRepository.findByUserOrderByDataAdicaoDesc(user);
    }

    /// OBTEM FILMES ASSISTIDOS DO USUARIO
    public List<MinhaLista> getUserWatchedMovies(User user) {
        return minhaListaRepository.findByUserAndFilmeIsNotNullAndAssistidoTrueOrderByDataUltimaVisualizacaoDesc(user);
    }

    /// OBTEM SERIES ASSISTINDO DO USUARIO
    public List<MinhaLista> getUserWatchingSeries(User user) {
        return minhaListaRepository.findByUserAndSerieIsNotNullOrderByDataUltimaVisualizacaoDesc(user);
    }

    /// OBTEM INFORMACOES COMPLETAS DO USUARIO
    public AuthDTO.UserResponse getUserInfo(User user) {
        return convertToAuthUserResponse(user);
    }
}