package com.baozistore;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:teste")
@AutoConfigureMockMvc
class BaoziStoreApiTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    private long criar(String url, String json) throws Exception {
        String resposta = mvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode node = mapper.readTree(resposta);
        return node.get("id").asLong();
    }

    @Test
    void fluxoCompletoClienteProdutoPedido() throws Exception {
        long clienteId = criar("/clientes", "{\"nome\":\"Teste123\",\"clienteDesde\":\"2026-10-01\"}");
        long produtoId = criar("/produtos", "{\"nome\":\"Baozi\",\"preco\":7.50,\"estoque\":true}");
        long pedidoId = criar("/pedidos",
                "{\"clienteId\":" + clienteId + ",\"produtoId\":" + produtoId + ",\"quantidade\":6}");

        mvc.perform(get("/pedidos/" + pedidoId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cliente.nome").value("Teste123"))
                .andExpect(jsonPath("$.produto.nome").value("Baozi"))
                .andExpect(jsonPath("$.quantidade").value(6));

        mvc.perform(get("/clientes")).andExpect(status().isOk());
        mvc.perform(get("/produtos")).andExpect(status().isOk());
        mvc.perform(get("/pedidos")).andExpect(status().isOk());

        // cliente com pedido não pode ser apagado
        mvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isConflict());

        mvc.perform(delete("/pedidos/" + pedidoId)).andExpect(status().isNoContent());
        mvc.perform(get("/pedidos/" + pedidoId)).andExpect(status().isNotFound());
        mvc.perform(delete("/clientes/" + clienteId)).andExpect(status().isNoContent());
    }

    @Test
    void pedidoComClienteInexistenteRetorna400() throws Exception {
        mvc.perform(post("/pedidos").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"clienteId\":999,\"produtoId\":999,\"quantidade\":1}"))
                .andExpect(status().isBadRequest());
    }
}
