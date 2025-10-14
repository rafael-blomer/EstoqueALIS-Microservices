package br.com.rafaelblomer.business.producers;

import br.com.rafaelblomer.business.dtos.PublicacaoEmailDTO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UsuarioProducer {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.verificacao}")
    private String routingKeyVerificacao;

    @Value(value = "${broker.queue.email.trocasenha}")
    private String routingKeyTrocaSenha;

    @Value(value = "${broker.queue.exclusao}")
    private String routingKeyExclusao;

    public void publicarMensagemEmailConfirmacao(PublicacaoEmailDTO publicacaoEmailDTO) {
        rabbitTemplate.convertAndSend(routingKeyVerificacao, publicacaoEmailDTO);
    }

    public void publicarMensagemTrocaSenha(PublicacaoEmailDTO publicacaoEmailDTO) {
        rabbitTemplate.convertAndSend(routingKeyTrocaSenha, publicacaoEmailDTO);
    }

    public void publicarMensagemExclusaoUsuario(Long userId) {
        rabbitTemplate.convertAndSend(routingKeyExclusao, userId);
    }
}
