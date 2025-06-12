//
//// ===== 7. TESTE UNITÁRIO =====
//package com.lucaflix.service;
//
//import com.lucaflix.model.Media;
//import com.lucaflix.repository.MediaRepository;
//import com.lucaflix.service.utils.UrlFormatter;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//@ExtendWith(MockitoExtension.class)
//class SitemapServiceTest {
//
//    @Mock
//    private MediaRepository mediaRepository;
//
//    @Mock
//    private UrlFormatter urlFormatter;
//
//    @InjectMocks
//    private SitemapService sitemapService;
//
//    @Test
//    void testGenerateSitemapXml() {
//        // Configurar URL base
//        ReflectionTestUtils.setField(sitemapService, "baseUrl", "https://lucaflix.com");
//
//        // Mock de dados
//        Media filme = new Media();
//        filme.setId(1L);
//        filme.setTitle("Filme Teste");
//        filme.setFilme(true);
//        filme.setAnoLancamento(new Date());
//
//        when(mediaRepository.findAll()).thenReturn(Arrays.asList(filme));
//        when(urlFormatter.formatTitleForUrl(1L, "Filme Teste", filme.getAnoLancamento(), true))
//                .thenReturn("/1/filme-filme-teste-2024");
//
//        // Executar teste
//        String xml = sitemapService.generateSitemapXml();
//
//        // Verificar resultado
//        assertNotNull(xml);
//        assertTrue(xml.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"));
//        assertTrue(xml.contains("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">"));
//        assertTrue(xml.contains("https://lucaflix.com/"));
//        assertTrue(xml.contains("</urlset>"));
//    }
//}