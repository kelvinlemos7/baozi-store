package br.com.baozi.controller;

import br.com.baozi.model.Cliente;
import br.com.baozi.model.Pedido;
import br.com.baozi.model.Produto;
import br.com.baozi.repository.ClienteRepository;
import br.com.baozi.repository.PedidoRepository;
import br.com.baozi.repository.ProdutoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoController(PedidoRepository pedidoRepository,
                            ClienteRepository clienteRepository,
                            ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.produtoRepository = produtoRepository;
    }
    public record PedidoRequest(Long clienteId, Long produtoId, Integer quantidade) {}

    @PostMapping
    public ResponseEntity<Pedido> criar(@RequestBody PedidoRequest req) {
        Cliente cliente = clienteRepository.findById(req.clienteId()).orElse(null);
        Produto produto = produtoRepository.findById(req.produtoId()).orElse(null);
        if (cliente == null || produto == null || req.quantidade() == null || req.quantidade() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setProduto(produto);
        pedido.setQuantidade(req.quantidade());
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoRepository.save(pedido));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pedido> buscarPorId(@PathVariable Long id) {
        return pedidoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> apagar(@PathVariable Long id) {
        if (!pedidoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        pedidoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
