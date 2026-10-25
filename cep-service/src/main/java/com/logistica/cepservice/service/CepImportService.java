package com.logistica.cepservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Importa a base de CEPs para o Postgres a partir de um CSV.
 *
 * Por que JDBC "clássico" (bloqueante) em vez de R2DBC aqui, se o resto do
 * serviço é 100% reativo? Importação em lote é, por natureza, um trabalho
 * batch: você quer inserir dezenas ou centenas de milhares de linhas o mais
 * rápido possível, sem se importar em "não bloquear uma thread de request"
 * (não existe request nenhum acontecendo — é um job de inicialização). Para
 * esse tipo de tarefa, JDBC com batchUpdate é simples, maduro e rápido; não
 * ganharíamos nada reescrevendo isso de forma reativa.
 *
 * Onde entram as VIRTUAL THREADS: dividimos o arquivo em lotes (chunks) e
 * processamos vários lotes em paralelo, cada um em sua própria virtual
 * thread. Como cada inserção em lote passa a maior parte do tempo esperando
 * o Postgres responder (I/O), virtual threads são ideais — dá para disparar
 * dezenas delas sem o custo de dezenas de threads de sistema operacional.
 */
@Service
public class CepImportService {

    private static final Logger log = LoggerFactory.getLogger(CepImportService.class);
    private static final int TAMANHO_LOTE = 500;

    private final JdbcTemplate jdbcTemplate;

    public CepImportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Lê o CSV de seed (classpath:seed/ceps-sample.csv), divide em lotes de
     * {@value #TAMANHO_LOTE} linhas e insere cada lote em paralelo usando
     * virtual threads. Retorna o total de linhas efetivamente importadas.
     */
    public int importarDoCsvDeSeed() {
        List<String[]> linhas = lerCsv();
        List<List<String[]>> lotes = particionar(linhas, TAMANHO_LOTE);

        log.info("Importando {} CEPs em {} lote(s) usando virtual threads", linhas.size(), lotes.size());

        // Executors.newVirtualThreadPerTaskExecutor(): cada tarefa submetida
        // ganha sua própria virtual thread, criada e descartada pela JVM sob
        // demanda — diferente de um pool de threads de plataforma, aqui não
        // existe um número fixo de "trabalhadores" para configurar.
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<Integer>> futuros = new ArrayList<>();
            for (List<String[]> lote : lotes) {
                futuros.add(executor.submit(() -> inserirLote(lote)));
            }

            int totalImportado = 0;
            for (Future<Integer> futuro : futuros) {
                totalImportado += aguardar(futuro);
            }

            log.info("Importação concluída: {} CEPs gravados", totalImportado);
            return totalImportado;
        }
    }

    private int inserirLote(List<String[]> lote) {
        int[][] resultados = jdbcTemplate.batchUpdate("""
                INSERT INTO endereco (cep, logradouro, bairro, cidade, uf, latitude, longitude)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT (cep) DO NOTHING
                """, lote, lote.size(), (ps, linha) -> {
            ps.setString(1, linha[0]);
            ps.setString(2, linha[1]);
            ps.setString(3, linha[2]);
            ps.setString(4, linha[3]);
            ps.setString(5, linha[4]);
            ps.setDouble(6, Double.parseDouble(linha[5]));
            ps.setDouble(7, Double.parseDouble(linha[6]));
        });
        int inseridos = resultados.length;
        log.debug("Lote de {} linhas processado nesta thread: {}", lote.size(), Thread.currentThread());
        return inseridos;
    }

    private int aguardar(Future<Integer> futuro) {
        try {
            return futuro.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Importação interrompida", e);
        } catch (ExecutionException e) {
            throw new IllegalStateException("Falha ao importar um dos lotes", e.getCause());
        }
    }
    private List<String[]> lerCsv() {
        List<String[]> linhas = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                new ClassPathResource("seed/ceps-sample.csv").getInputStream(), StandardCharsets.UTF_8))) {
            String linha = reader.readLine(); // cabeçalho
            while ((linha = reader.readLine()) != null) {
                if (!linha.isBlank()) {
                    linhas.add(linha.split(","));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Não foi possível ler o CSV de seed de CEPs", e);
        }
        return linhas;
    }

    private List<List<String[]>> particionar(List<String[]> linhas, int tamanho) {
        List<List<String[]>> lotes = new ArrayList<>();
        for (int i = 0; i < linhas.size(); i += tamanho) {
            lotes.add(linhas.subList(i, Math.min(i + tamanho, linhas.size())));
        }
        return lotes;
    }
}
